package recsys.algorithms.cbf;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;
import recsys.algorithms.RecommendationAlgorithm;
import recsys.datapreparer.DataSetReader;
import recsys.datapreparer.DataSetType;

public class CB extends RecommendationAlgorithm {
	static Logger log = Logger.getLogger("Author: Luan");
	private DataSetReader dataSetReader = null;
	private DocumentProcesser memDocProcessor = new DocumentProcesser();
	private boolean trainMode = false; 
	public CB(String input, String output, String taskId, boolean _trainMode) {	
		super(input, output, taskId);
		trainMode = _trainMode;		
	}
	public CB( boolean _trainMode) {			
		trainMode = _trainMode;		
	}

	public void trainModel()
	{
		try {
			log.info("Start training model");
			dataSetReader = new DataSetReader(outputDirectory + "training\\");
			dataSetReader.open(DataSetType.Score);
			log.info("Read labeled data");
			ScoreDTO sdto = null;
			while ((sdto = dataSetReader.nextScore()) != null) {
				if (sdto.getScore() > 3) {
					memDocProcessor.addRating(sdto.getUserId() + "", sdto.getJobId() + "");
				}
			}
			log.info("Train model end");
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private void createDataModel() {
		log.info("create dataset reader");
		dataSetReader = new DataSetReader(this.inputDirectory);
		log.info("read cv from dataset");
		dataSetReader.open(DataSetType.Cv);
		CvDTO cvdto = null;
		System.out.println("Build user profile");
		while ((cvdto = dataSetReader.nextCv()) != null) {
			String itemId = cvdto.getAccountId() + "";
			String content = cvdto.getAddress() + ". ";
			content += cvdto.getCategory() + ". ";
			content += cvdto.getEducation() + ". ";
			content += cvdto.getObjective() + ". ";
			content += cvdto.getSkill() + ". ";
			content += cvdto.getLanguage() + ". ";
			memDocProcessor.addCv(itemId, content);
		}
		log.info("read cv done");
		dataSetReader = new DataSetReader(this.inputDirectory);
		log.info("read jobs from dataset");
		dataSetReader.open(DataSetType.Job);
		log.info("Building item profile");		
		JobDTO dto = null;
		int count = 0;
		while ((dto = dataSetReader.nextJob()) != null) {
			if(count > 1000) break;
			System.out.println("ItemId: " + count++);
			String itemId = dto.getJobId() + "";
			String content = dto.getJobName() + ". ";
			content += dto.getRequirement() + ". ";
			content += dto.getLocation() + ". ";
			content += dto.getTags() + ". ";
			content += dto.getDescription() + ". ";
			content += dto.getCategory() + ". ";
			memDocProcessor.addJob(itemId, content);
		}
		log.info("Building item done");		
		dataSetReader = new DataSetReader(this.inputDirectory);
		dataSetReader.open(DataSetType.Score);

		if(trainMode)
		{
			
			trainModel();
		}
		else
		{
			log.info("Read labeled data");
			ScoreDTO sdto = null;
			while ((sdto = dataSetReader.nextScore()) != null) {
				if (sdto.getScore() > 3) {
					memDocProcessor.addRating(sdto.getUserId() + "", sdto.getJobId() + "");
				}
			}
			log.info("Read labeled data is done");
		}
						
	}

	public void run() throws IOException, InterruptedException {

		class RecTask implements Runnable{
			
			public RecTask(String _userid, int _topN, int theadId)
			{
				userId = _userid;
				topN = _topN;
				id =theadId;
			}
			private String userId;
			private int topN;
			private int id;
			private boolean isRun = false;
			@Override
			public void run() {
				
				try {
					isRun = true;
					log.info("Thread "+ id +" for user " + userId + " start at " + new Date().toString());
					memDocProcessor.recommend(userId, topN,id);
					log.info("Thread " + id + " for user " + userId + " terminate at " + new Date().toString());
					isRun = false;
				} catch (IOException e) {
					log.error(e);
					log.error("Thread for user " + userId + " terminate at " + new Date().toString());
				}
				
				
			}
			
		}
		
		log.info("open Lucene writer");
		if (memDocProcessor.open()) {
			log.info("open Lucene writer successful");
			createDataModel();
			memDocProcessor.close();
			log.info("Close lucene writer");
			memDocProcessor.openReader();
			log.info("Build term model");
			memDocProcessor.buildTermModel();
			log.info("Calculate df");
			memDocProcessor.CalculateIdf(); 
			log.info("Open lucene reader");
			int threadid = 0;
			int topN = Integer.valueOf(config.getProperty("topn"));
			 Runtime runtime = Runtime.getRuntime();
			 int numOfProcessors = runtime.availableProcessors();
	        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
			for (String i : this.memDocProcessor.getListUsers()) {
				RecTask rec = new RecTask(i, topN, threadid++);		
				executor.submit(rec);
			}
			
	        executor.shutdown();
	        while (!executor.isTerminated()) {
	        }
			

			
			memDocProcessor.closeReader();
			log.info("Close lucene reader");
			if(trainMode)
			{
				memDocProcessor.writeFile(outputDirectory + "result\\");
			}
			else
			{
				memDocProcessor.writeFile(outputDirectory );					
			}
			updateDB("update task set Status = 'Done' where TaskId = " + taskId);
			log.info("Finish CB");
		}

	}

	public void recommend() {
		try {
			
			if (memDocProcessor.open()) {
				log.info("open lucene writer");
				log.info("Start to read and index data");
				createDataModel();
				log.info("Read and index data is done");
				memDocProcessor.close();
				log.info("open lucene writer");
				log.info("Open lucene reader");
				memDocProcessor.openReader();
				memDocProcessor.buildTermModel();
				memDocProcessor.recommend(outputDirectory);
				memDocProcessor.closeReader();
				log.info("Close lucene reader");
				//memDocProcessor.writeFile(outputDirectory);
				log.info("Write file");
			}
			
			this.memDocProcessor.recommend(outputDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
