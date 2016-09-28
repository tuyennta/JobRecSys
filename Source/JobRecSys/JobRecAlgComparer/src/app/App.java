package app;

import org.apache.log4j.Logger;

import recsys.algorithms.cbf.CB;
import recsys.algorithms.collaborativeFiltering.CollaborativeFiltering;
import recsys.algorithms.hybird.HybirdRecommeder;
import recsys.evaluate.Evaluation;

public class App {
	public static Logger log = Logger.getLogger(App.class.getName());

	public static void main(String[] args) {
		switch (args[0]) {
		case "rec":
			recommend(args);
			break;
		case "eval":
			evaluate(args);
			break;
		default:
			break;
		}
	}

	private static void recommend(String[] args) {
		switch (args[1]) {
		case "cf":
			collaborativeFiltering(args[2], args[3], args[4]);
			break;
		case "cb":
			contentBase(args[2], args[3], args[4]);
			break;
		case "hb":
			hybrid(args[2], args[3]);
			break;
		default:
			break;
		}
	}

	private static void evaluate(String[] args) {
		Evaluation eval = new Evaluation(args[2], Integer.valueOf(args[3]), args[1], args[4], args[5], args[6]);
		eval.evaluate();
	}

	private static void collaborativeFiltering(String input, String output, String taskId) {
		CollaborativeFiltering cf = new CollaborativeFiltering(input, output, taskId);
		cf.recommend();
	}

	private static void hybrid(String input, String output) {
		HybirdRecommeder hybridRecommender = new HybirdRecommeder();
		hybridRecommender.setInputDirectory(input);
		hybridRecommender.setOutputDirectory(output);
		hybridRecommender.init();
		
	}

	private static void contentBase(String input, String output, String taskId) {

		CB cb = new CB(input, output, taskId, false);
		try {
			cb.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
