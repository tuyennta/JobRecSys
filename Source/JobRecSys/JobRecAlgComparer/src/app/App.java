package app;

import org.apache.log4j.Logger;

import recsys.algorithms.cbf.CB;
import recsys.algorithms.collaborativeFiltering.CollaborativeFiltering;
import recsys.algorithms.hybird.HybirdRecommeder;
import recsys.evaluate.Evaluation;

public class App {
	public static Logger log = Logger.getLogger(App.class.getName());

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		switch (args[0]) {
		case "rec":
			recommend(args, startTime);
			break;
		case "eval":
			evaluate(args, startTime);
			break;
		default:
			break;
		}
	}

	private static void recommend(String[] args, long startTime) {
		switch (args[1]) {
		case "cf":
			collaborativeFiltering(args[2], args[3], args[4], startTime);
			break;
		case "cb":
			contentBase(args[2], args[3], args[4], startTime);
			break;
		case "hb":
			hybrid(args[2], args[3], args[4], startTime);
			break;
		default:
			break;
		}
	}

	private static void evaluate(String[] args, long startTime) {
		Evaluation eval = new Evaluation(args[2], Integer.valueOf(args[3]), args[1], args[4], args[5], args[6], startTime);
		eval.evaluate();
	}

	private static void collaborativeFiltering(String input, String output, String taskId, long startTime) {
		CollaborativeFiltering cf = new CollaborativeFiltering(input, output, taskId, startTime);
		cf.recommend();
	}

	private static void hybrid(String input, String output,String taskId, long startTime) {
		HybirdRecommeder hybridRecommender = new HybirdRecommeder(input, output, taskId, startTime);
		hybridRecommender.init();
		hybridRecommender.run();
	}

	private static void contentBase(String input, String output, String taskId, long startTime) {

		CB cb = new CB(input, output, taskId, false, startTime);
		try {
			cb.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
