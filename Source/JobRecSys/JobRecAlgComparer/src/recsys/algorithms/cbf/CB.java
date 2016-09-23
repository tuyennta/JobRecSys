package recsys.algorithms.cbf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;
import recsys.algorithms.RecommendationAlgorithm;
import recsys.datapreparer.DataSetReader;
import recsys.datapreparer.DataSetType;

public class CB extends RecommendationAlgorithm {

	private DataSetReader dataSetReader = null;
	private DocumentProcesser memDocProcessor = new DocumentProcesser();

	public CB() {

	}

	private void readDataSet() {
		dataSetReader = new DataSetReader(this.inputDirectory);
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
		dataSetReader = new DataSetReader(this.inputDirectory);
		dataSetReader.open(DataSetType.Job);

		System.out.println("Build item profile");
		JobDTO dto = null;
		int count = 0;
		while ((dto = dataSetReader.nextJob()) != null) {
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

		dataSetReader = new DataSetReader(this.inputDirectory);
		dataSetReader.open(DataSetType.Job);

		System.out.println("Build item profile");
		ScoreDTO sdto = null;
		while ((sdto = dataSetReader.nextScore()) != null) {
			if (sdto.getScore() > 3) {
				memDocProcessor.addRating(sdto.getUserId() + "", sdto.getJobId() + "");
			}
		}
	}

	public void run() throws IOException, InterruptedException {

		if (memDocProcessor.open()) {
			readDataSet();
			memDocProcessor.close();
			Collection<Future<Object>> futures = new LinkedList<Future<Object>>();
			int topN = 10;
			//ExecutorService executor = Executors.newFixedThreadPool(3);
			//ArrayList<Callable<Object>> todo = new ArrayList<Callable<Object>>(
			//this.memDocProcessor.getListUsers().size());
			memDocProcessor.openReader();
			for (String i : this.memDocProcessor.getListUsers()) {
				//SmallTask sm = new SmallTask();
				//sm.user_sm = i;
				//sm.topN_sm = topN;
				//todo.add(Executors.callable(sm));				
				memDocProcessor.recommend(i, topN);
			}
			//System.out.println(todo.size());
			//futures.add((Future<Object>) executor.invokeAll(todo));
			memDocProcessor.closeReader();
			memDocProcessor.writeFile(outputDirectory);
		}

	}

	public void recommend() {
		try {
			
			if (memDocProcessor.open()) {
				readDataSet();
				memDocProcessor.close();
				memDocProcessor.openReader();
				memDocProcessor.recommend(outputDirectory);
				memDocProcessor.closeReader();
				memDocProcessor.writeFile(outputDirectory);
			}
			
			this.memDocProcessor.recommend(outputDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
