package recsys.algorithms.collaborativeFiltering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.algorithms.RecommendationAlgorithm;
import recsys.datapreparer.CollaborativeFilteringDataPreparer;

public class CollaborativeFiltering extends RecommendationAlgorithm {

	DataModel dataModel;
	Recommender recommender;
	UserSimilarity userSimilarity;
	ItemSimilarity itemSimilarity;
	UserNeighborhood userNeighborhood;
	List<Integer> listUserIds;

	public CollaborativeFiltering(String inputDir, String outputDir) {

		/* prepare configuration */
		super(inputDir, outputDir);

		/* learn model */
		initModel();

		/* List users will be recommended */
		listUserIds = new CollaborativeFilteringDataPreparer(this.inputDirectory).getListUserId();
	}

	public CollaborativeFiltering(String evaluationDir, Properties config) {

		/* prepare configuration */
		super(evaluationDir, config);

		/* learn model */
		initModel();

		/* List users will be recommended */
		listUserIds = new CollaborativeFilteringDataPreparer(this.testDirectory).getListUserId();
	}

	private void initModel() {

		/* read configuration */
		readConfiguration(configDirectory);

		/* init data model */
		try {
			dataModel = new FileDataModel(new File(inputDirectory + "Score.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (config.getProperty("cf.type").equals("UserBased")) {
			try {
				/* init user similarity measure */
				initUserSimilaritymeasure(config.getProperty("cf.similarity"));

				/* init user neighborhood */
				String param = config.getProperty("cf.neighborhood.param.topn");
				if(param == null || param == "" ){
					param = config.getProperty("cf.neighborhood.param.threshold");
				}
				initUserNeighborhood(config.getProperty("cf.neighborhood.type"),
						param);
			} catch (TasteException e) {
				e.printStackTrace();
			}
		} else {
			/* init item similarity measure */
			try {
				initItemSimilaritymeasure(config.getProperty("cf.similarity"));
			} catch (TasteException e) {
				e.printStackTrace();
			}
		}
	}

	public void recommend() {
		switch (config.getProperty("cf.type")) {
		case "UserBased":
			for (int userId : listUserIds) {
				UserBased(userId);
			}
			break;
		case "ItemBased":
			for (Integer userId : listUserIds) {
				ItemBased(userId);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Recommendation using user-Based method
	 */
	private void UserBased(int userIDToRecommend) {
		// initialize recommender
		recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
		try {
			writeOutput(userIDToRecommend, recommender.recommend(userIDToRecommend,
					Integer.valueOf(config.getProperty("cf.recommendItems")), new IDRescorer() {
						@Override
						public double rescore(long userid, double originalSocre) {
							return originalSocre;
						}

						@Override
						public boolean isFiltered(long itemId) {
							return false;
						}
					}));
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recommendation using item-Based method
	 */
	private void ItemBased(int userIDToRecommend) {
		// initialize recommender
		recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
		try {
			writeOutput(userIDToRecommend, recommender.recommend(userIDToRecommend,
					Integer.valueOf(config.getProperty("cf.recommendItems")), new IDRescorer() {
						@Override
						public double rescore(long userid, double originalSocre) {
							return originalSocre;
						}

						@Override
						public boolean isFiltered(long itemId) {
							return false;
						}
					}));
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the user similarity measure
	 */
	private void initUserSimilaritymeasure(String similarity) throws TasteException {
		switch (similarity) {
		case "LOGLIKELIHOOD_SIMILARITY":
			userSimilarity = new LogLikelihoodSimilarity(dataModel);
			break;
		case "EUCLIDEAN_DISTANCE":
			userSimilarity = new EuclideanDistanceSimilarity(dataModel, Weighting.WEIGHTED);
			break;
		case "PEARSON_CORRELATION":
			userSimilarity = new PearsonCorrelationSimilarity(dataModel, Weighting.WEIGHTED);
			break;
		case "SPEARMAN_CORRELATION":
			userSimilarity = new SpearmanCorrelationSimilarity(dataModel);
			break;
		case "TANIMOTO_COOFFICIENT":
			userSimilarity = new TanimotoCoefficientSimilarity(dataModel);
			break;
		default:
			break;
		}
	}

	private void initItemSimilaritymeasure(String similarity) throws TasteException {
		switch (similarity) {
		case "LOGLIKELIHOOD_SIMILARITY":
			itemSimilarity = new LogLikelihoodSimilarity(dataModel);
			break;
		case "EUCLIDEAN_DISTANCE":
			itemSimilarity = new EuclideanDistanceSimilarity(dataModel, Weighting.WEIGHTED);
			break;
		case "PEARSON_CORRELATION":
			itemSimilarity = new PearsonCorrelationSimilarity(dataModel, Weighting.WEIGHTED);
			break;
		case "SPEARMAN_CORRELATION":
		case "TANIMOTO_COOFFICIENT":
			itemSimilarity = new TanimotoCoefficientSimilarity(dataModel);
			break;
		default:
			break;
		}
	}

	private void initUserNeighborhood(String neighborhood, String neighborhoodParam) throws TasteException {
		switch (neighborhood) {
		case "NearestNUserNeighborhood":
			userNeighborhood = new NearestNUserNeighborhood(Integer.valueOf(neighborhoodParam), userSimilarity,
					dataModel);
			break;
		case "ThresholdUserNeighborhood":
			userNeighborhood = new ThresholdUserNeighborhood(Float.valueOf(neighborhoodParam), userSimilarity,
					dataModel);
		default:
			break;
		}
	}

	private void writeOutput(int userId, List<RecommendedItem> recommendedItems) {
		FileWriter fwr;
		try {
			File out = new File(outputDirectory);
			if (!out.exists()) {
				out.mkdirs();
			}
			File fileOut = new File(out.getAbsolutePath() + File.separator + "Score.txt");
			if (!fileOut.exists()) {
				fileOut.createNewFile();
			}
			fwr = new FileWriter(fileOut, true);
			BufferedWriter wr = new BufferedWriter(fwr);
			System.out.println("start writing data");
			for (RecommendedItem rec : recommendedItems) {
				wr.write(userId + "\t" + rec.getItemID() + "\t" + rec.getValue());
				System.out.println("Result: " + userId + "\t" + rec.getItemID() + "\t" + rec.getValue());
				wr.newLine();
			}
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
