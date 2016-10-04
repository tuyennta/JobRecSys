package recsys.algorithms;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import utils.MysqlDBConnection;

public abstract class RecommendationAlgorithm {

	protected String inputDirectory;
	protected String outputDirectory;
	protected String testDirectory;
	protected String configDirectory;
	protected Properties config;
	protected String taskId;
	protected long startTime;
	protected boolean isRunningEvaluation;
	protected int topn;
	
	public boolean isRunningEvaluation() {
		return isRunningEvaluation;
	}

	public void setRunningEvaluation(boolean isRunningEvaluation) {
		this.isRunningEvaluation = isRunningEvaluation;
	}

	protected static Logger log = Logger.getLogger(RecommendationAlgorithm.class.getName());

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public RecommendationAlgorithm() {
		config = new Properties();
	}

	public void init() {
	}

	public RecommendationAlgorithm(String input, String output, String taskId, long startTime) {
		this.inputDirectory = input;
		this.outputDirectory = output;
		this.testDirectory = "";
		this.configDirectory = output;
		this.startTime = startTime;
		this.config = new Properties();
		readConfiguration(configDirectory);
		topn = Integer.valueOf(config.getProperty("topn"));
		isRunningEvaluation = false;
		this.taskId = taskId;
	}

	public RecommendationAlgorithm(String evaluationFolder, Properties config, String taskId, long startTime) {
		this.inputDirectory = evaluationFolder + "training\\";
		this.outputDirectory = evaluationFolder + "result\\";
		this.testDirectory = evaluationFolder + "testing\\";
		this.configDirectory = evaluationFolder;		
		this.startTime = startTime;
		this.config = config;
		isRunningEvaluation = true;
		this.taskId = taskId;
		topn = Integer.valueOf(config.getProperty("topn"));
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

	private void readConfiguration(String fileLocation) {
		try {
			config.load(new FileInputStream(fileLocation + "config.properties"));
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}catch (NullPointerException ex){
			log.error(ex);
			ex.printStackTrace();
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
