package recsys.algorithms;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import utils.MysqlDBConnection;

public abstract class RecommendationAlgorithm {
	private static Logger log = Logger.getLogger("Author:Luan");
	protected String inputDirectory;
	protected String outputDirectory;
	protected String testDirectory;
	protected String configDirectory;
	protected Properties config = new Properties();
	protected String taskId;

	public RecommendationAlgorithm() {
	}

	public void init() {
	}

	public RecommendationAlgorithm(String input, String output, String taskId) {
		this.inputDirectory = input;
		this.outputDirectory = output;
		this.testDirectory = "";
		this.configDirectory = output;
		this.config = new Properties();
		this.taskId = taskId;
	}

	public RecommendationAlgorithm(String evaluationFolder, Properties config, String taskId) {
		this.inputDirectory = evaluationFolder + "training\\";
		this.outputDirectory = evaluationFolder + "result\\";
		this.testDirectory = evaluationFolder + "testing\\";
		this.configDirectory = evaluationFolder;
		this.config = config;
		this.taskId = taskId;
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

	public void readConfiguration(String fileLocation) {
		try {
			config.load(new FileInputStream(fileLocation + "config.properties"));
		} catch (IOException e) {
			log.error(e.toString());
		}
	}
	
	protected void updateDB(String sql){		
		MysqlDBConnection mysql = new MysqlDBConnection("dbconfig.properties");		
		if(mysql.connect()){
			mysql.write(sql);
			mysql.close();
		}
	}
}
