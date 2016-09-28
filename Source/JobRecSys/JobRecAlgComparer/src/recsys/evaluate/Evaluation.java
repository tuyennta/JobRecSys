package recsys.evaluate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import recsys.algorithms.cbf.CB;
import recsys.algorithms.collaborativeFiltering.CollaborativeFiltering;
import recsys.algorithms.hybird.HybirdRecommeder;
import recsys.datapreparer.CollaborativeFilteringDataPreparer;
import recsys.datapreparer.ContentBasedDataPreparer;
import uit.se.evaluation.dtos.ScoreDTO;
import uit.se.evaluation.metrics.AveragePrecision;
import uit.se.evaluation.metrics.FMeasure;
import uit.se.evaluation.metrics.NDCG;
import uit.se.evaluation.metrics.Precision;
import uit.se.evaluation.metrics.RMSE;
import uit.se.evaluation.metrics.Recall;
import uit.se.evaluation.metrics.ReciprocalRank;
import uit.se.evaluation.utils.DatasetUtil;
import utils.MysqlDBConnection;

/**
 * Created with IntelliJ IDEA. User: tuynguye Date: 8/12/16 Time: 9:47 AM To
 * change this template use File | Settings | File Templates.
 */
public class Evaluation {
	String evaluationType;
	int evaluationParam;
	String algorithm;
	int topN;
	String inputDir;
	String evaluationDir;
	String taskId;
	Properties config;
	int truthRank = 3;

	public Evaluation(String evalType, int evalParam, String algorithm, String input, String evalDir, String taskId) {
		this.algorithm = algorithm;
		this.evaluationParam = evalParam;
		this.evaluationType = evalType;
		this.inputDir = input;
		this.evaluationDir = evalDir;
		this.taskId = taskId;
		this.config = new Properties();
		try {
			config.load(new FileInputStream(evalDir + "config.properties"));
			topN = Integer.valueOf(config.getProperty("topn"));
			truthRank = Integer.valueOf(config.getProperty("relevant.score"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void percentageSplit() {
		/**
		 * First step: preparing data, split training and testing data set
		 */
		CollaborativeFilteringDataPreparer dataPreparer = new CollaborativeFilteringDataPreparer(inputDir);
		dataPreparer.splitDataSet(evaluationParam, evaluationDir);
		if (!algorithm.equals("cf")) {
			ContentBasedDataPreparer cbDataPreparer = new ContentBasedDataPreparer(inputDir);
			cbDataPreparer.splitDataSet(evaluationDir, evaluationParam);
		}

		/**
		 * Second step: call Algorithm execute on training data set
		 */
		trainAlgorithm();

		/**
		 * Third step: convert result to boolean type
		 */
		HashMap<Integer, List<ScoreDTO>> groundTruth = DatasetUtil.getGroundTruth(evaluationDir + "testing\\Score.txt",
				truthRank);
		HashMap<Integer, List<ScoreDTO>> rankList = DatasetUtil.getRankList(evaluationDir + "result\\Score.txt",
				truthRank);

		/**
		 * Four step: evaluation
		 */
		HashMap<String, Double> evaluationResult = computeEvaluation(rankList, groundTruth);

		/**
		 * Fifth step: write result to DB
		 */
		writeResult(taskId, evaluationResult, true);
	}

	private void customValidation() {
		/**
		 * First step: preparing data, split training and testing data set
		 */
		CollaborativeFilteringDataPreparer dataPreparer = new CollaborativeFilteringDataPreparer(inputDir);
		dataPreparer.copyFileTo(inputDir + "Score.txt", evaluationDir + "training\\Score.txt");
		if (!algorithm.equals("cf")) {
			ContentBasedDataPreparer cbDataPreparer = new ContentBasedDataPreparer(inputDir);
			cbDataPreparer.copyFileTo(inputDir, evaluationDir + "training\\");
		}

		/**
		 * Second step: call Algorithm execute on training data set
		 */
		trainAlgorithm();

		/**
		 * Third step: convert result to boolean type
		 */
		HashMap<Integer, List<ScoreDTO>> groundTruth = DatasetUtil.getGroundTruth(evaluationDir + "testing\\Score.txt",
				truthRank);
		HashMap<Integer, List<ScoreDTO>> rankList = DatasetUtil.getRankList(evaluationDir + "result\\Score.txt",
				truthRank);

		/**
		 * Fourth step: evaluation
		 */
		HashMap<String, Double> evaluationResult = computeEvaluation(rankList, groundTruth);

		/**
		 * Fifth step: write result to DB
		 */
		writeResult(taskId, evaluationResult, true);
	}

	private void crossValidation() {

		CollaborativeFilteringDataPreparer dataPreparer = new CollaborativeFilteringDataPreparer(inputDir);
		HashMap<String, Double> evaluationResult = null;
		for (int i = 0; i < evaluationParam; i++) {
			/**
			 * First step: preparing data, split training and testing data set
			 */
			dataPreparer.splitDataSet(i, evaluationParam, inputDir, evaluationDir);
			if (!algorithm.equals("cf")) {
				ContentBasedDataPreparer cbDataPreparer = new ContentBasedDataPreparer(inputDir);
				cbDataPreparer.splitDataSet(evaluationDir, evaluationParam);
			}

			/**
			 * Second step: call Algorithm execute on training data set
			 */
			trainAlgorithm();

			/**
			 * Third step: convert result to boolean type
			 */
			HashMap<Integer, List<ScoreDTO>> groundTruth = DatasetUtil
					.getGroundTruth(evaluationDir + "testing\\Score.txt", truthRank);
			HashMap<Integer, List<ScoreDTO>> rankList = DatasetUtil.getRankList(evaluationDir + "result\\Score.txt",
					truthRank);

			/**
			 * Four step: compute evaluation
			 */
			evaluationResult = updateEvaluationResult(evaluationResult, computeEvaluation(rankList, groundTruth));
		}

		/**
		 * Fifth step: write result to DB
		 */
		for (String key : evaluationResult.keySet()) {
			evaluationResult.put(key, evaluationResult.get(key) / evaluationParam);
		}
		writeResult(taskId, evaluationResult, true);
	}

	private HashMap<String, Double> updateEvaluationResult(HashMap<String, Double> oldResult,
			HashMap<String, Double> newResult) {
		if (oldResult == null)
			return newResult;
		for (String key : newResult.keySet()) {
			newResult.put(key, newResult.get(key) + oldResult.get(key));
		}
		return newResult;
	}

	public void evaluate() {

		switch (evaluationType) {
		case "cross":
			crossValidation();
			break;
		case "partitioning":
			percentageSplit();
			break;
		default:
			customValidation();
			break;
		}

	}

	private HashMap<String, Double> computeEvaluation(HashMap<Integer, List<ScoreDTO>> rankList,
			HashMap<Integer, List<ScoreDTO>> groundTruth) {
		double preTopN = 0;
		double precision = 0;
		double recall = 0;
		double recTopN = 0;
		double f = 0;
		double ndcgTopN = 0;
		double rmse = 0;
		double mrr = 0;
		double map = 0;
		for (Integer userId : rankList.keySet()) {
			preTopN += Precision.computePrecisionTopN(rankList.get(userId), groundTruth.get(userId), topN);
			precision += Precision.computePrecision(rankList.get(userId), groundTruth.get(userId));
			recall += Recall.computeRecall(rankList.get(userId), groundTruth.get(userId));
			recTopN += Recall.computeRecallTopN(rankList.get(userId), groundTruth.get(userId), topN);
			f += FMeasure.computeF1(rankList.get(userId), groundTruth.get(userId));
			try {
				ndcgTopN += NDCG.computeNDCG(rankList.get(userId), groundTruth.get(userId), topN);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rmse += RMSE.computeRMSE(rankList.get(userId), groundTruth.get(userId));
			try {
				mrr += ReciprocalRank.computeRR(rankList.get(userId), groundTruth.get(userId));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map += AveragePrecision.computeAP(rankList.get(userId), groundTruth.get(userId));
		}
		int n = rankList.size();
		if (n != 0) {
			preTopN /= n;
			precision /= n;
			recall /= n;
			recTopN /= n;
			f /= n;
			ndcgTopN /= n;
			rmse /= n;
			mrr /= n;
			map /= n;
		}
		System.out.println("P@" + topN + ": " + preTopN);
		System.out.println("P:" + precision);
		System.out.println("R:" + recall);
		System.out.println("R@" + topN + ": " + recTopN);
		System.out.println("F:" + f);
		System.out.println("NDCG@" + topN + ": " + ndcgTopN);
		System.out.println("RMSE:" + rmse);
		System.out.println("MRR:" + mrr);
		System.out.println("MAP:" + map);

		System.out.println("-----------");

		HashMap<String, Double> evaluationResult = new HashMap<>(evaluationParam);
		evaluationResult.put("Precision", precision);
		evaluationResult.put("Recall", recall);
		evaluationResult.put("P@" + topN, preTopN);
		evaluationResult.put("R@" + topN, recTopN);
		evaluationResult.put("F1", f);
		evaluationResult.put("NDCG@" + topN, ndcgTopN);
		evaluationResult.put("RMSE", rmse);
		evaluationResult.put("MRR", mrr);
		evaluationResult.put("MAP", map);

		return evaluationResult;

	}

	private void writeResult(String taskId, HashMap<String, Double> evaluationResult, boolean writeToFile) {
		if (writeToFile) {
			/* Create file */
			File commandFile = new File(evaluationDir + "evaluationResult.txt");
			if (!commandFile.exists()) {
				try {
					commandFile.createNewFile();
					FileWriter fw = new FileWriter(commandFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					for (String key : evaluationResult.keySet()) {
						bw.write(key + "\t" + evaluationResult.get(key) + "\n");
					}
					bw.close();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		try {
			MysqlDBConnection con = new MysqlDBConnection("dbconfig.properties");
			if (con.connect()) {
				String sql = "INSERT INTO `evaluation`(`TaskId`, `Score`, `Metric`) VALUES ";
				for (String key : evaluationResult.keySet()) {
					sql += "(" + taskId + "," + evaluationResult.get(key) + ", '" + key + "'),";
				}
				sql = sql.substring(0, sql.length() - 1);
				con.write(sql);
				con.write("update task set Status = 'Done' where TaskId = " + taskId);
				con.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void trainAlgorithm() {
		switch (algorithm) {
		case "cf":
			trainCF();
			break;
		case "cb":
			trainCB();
			break;
		case "hb":
			trainHB();
			break;
		default:
			break;
		}

	}

	private void trainHB() {
		//HybirdRecommeder hybridRecommender = new HybirdRecommeder();
		//hybridRecommender.setInputDirectory(evaluationDir + "training\\");
		//hybridRecommender.setOutputDirectory(evaluationDir + "result\\");
		//hybridRecommender.init();
		//hybridRecommender.hibridRecommend();
	}

	private void trainCB() {
		CB cb = new CB(inputDir, evaluationDir, taskId, true);		
		try {
			cb.run();
		} catch (Exception ex) {
		}
	}

	private void trainCF() {
		CollaborativeFiltering cf = new CollaborativeFiltering(evaluationDir, config, taskId);
		cf.recommend();
	}
}
