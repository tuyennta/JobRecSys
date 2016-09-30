package recsys.algorithms.hybird;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import recsys.algorithms.RecommendationAlgorithm;
import recsys.algorithms.cbf.CB;
import recsys.algorithms.cbf.CBTopNJobs;
import recsys.algorithms.cbf.CbRecommededList;
import recsys.algorithms.collaborativeFiltering.CollaborativeFiltering;

public class HybirdRecommeder extends RecommendationAlgorithm {

	public HybirdRecommeder(String in, String out, String task, long startTime) {
		super(in, out, task, startTime);
	}

	private CB cbRecommender;
	private CollaborativeFiltering cfRecommender;

	public void init() {
		cbRecommender = new CB(inputDirectory, outputDirectory, taskId, false, this.startTime);
		cfRecommender = new CollaborativeFiltering(this.inputDirectory, this.outputDirectory, taskId, startTime);
	}

	private HashMap<String, List<RecommendedItem>> cfResults = new HashMap<String, List<RecommendedItem>>();

	public void writeResult(String path, HashMap<String, CbRecommededList> rss) {
		try {

			int topN = Integer.parseInt(this.config.getProperty("topn"));
			FileWriter fw = new FileWriter(path + "Score.txt", true);
			System.out.println("Start writing result!");
			double alpha = Double.parseDouble(config.getProperty("hb.alpha"));
			for (String i : rss.keySet()) {

				ArrayList<Double> scores = rss.get(i).getHybridValue(alpha);
				ArrayList<String> jobs = rss.get(i).getJobsArray();
				CBTopNJobs topNJobs = new CBTopNJobs(topN);
				for (int k = 0; k < scores.size(); k++) {
					topNJobs.add(jobs.get(k), scores.get(k));
				}
				for (int k = 0; k < topN; k++) {
					fw.append(i + "\t" + topNJobs.TopNjob[k] + "\t" + topNJobs.TopNscore[k] + "\r\n");
				}
			}
			fw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void run() {
		List<Integer> listUser = cfRecommender.getListUserIds();

		for (int i : listUser) {
			System.out.println("Get recommed for user " + i);
			List<RecommendedItem> data = cfRecommender.getListRecommendItemItemBased(1, 40000);
			cfResults.put(i + "", data);
		}
		try {
			HashMap<String, CbRecommededList> data = cbRecommender.run(cfResults);
			if (this.isRunningEvaluation)
				writeResult(outputDirectory + "result\\", data);
			else
				writeResult(outputDirectory, data);
			if (!this.isRunningEvaluation)
				updateDB("update task set ExecutionTime = '" + ((System.currentTimeMillis() - this.startTime)/1000)
					+ "', Status = 'Done' where TaskId = " + taskId);
			log.info("Finish HB");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
