package recsys.algorithms;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class RecommendationAlgorithm {

	protected String inputDirectory;
	protected String outputDirectory;
	protected String testDirectory;
	protected String configDirectory;
	protected Properties config;

	public RecommendationAlgorithm() {
	}

	public void init() {
	}

	public RecommendationAlgorithm(String input, String output) {
		this.inputDirectory = input;
		this.outputDirectory = output;
		this.testDirectory = "";
		this.configDirectory = output;
		this.config = new Properties();
	}

	public RecommendationAlgorithm(String evaluationFolder, Properties config) {
		this.inputDirectory = evaluationFolder + "training\\";
		this.outputDirectory = evaluationFolder + "result\\";
		this.testDirectory = evaluationFolder + "testing\\";
		this.configDirectory = evaluationFolder;
		this.config = config;
	}

	public String getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	protected void readConfiguration(String fileLocation) {
		try {
			config.load(new FileInputStream(fileLocation + "config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
