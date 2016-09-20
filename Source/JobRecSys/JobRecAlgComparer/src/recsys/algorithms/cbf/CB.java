package recsys.algorithms.cbf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;
import recsys.algorithms.RecommendationAlgorithm;
import recsys.datapreparer.DataSetReader;
import recsys.datapreparer.DataSetType;

public class CB extends RecommendationAlgorithm {

	private DataSetReader dataSetReader = null;
	private DocumentProcesser memDocProcessor = new DocumentProcesser();
	
	 
	public CB()
	{
		
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
			memDocProcessor.addCv(itemId,content);
		}
		dataSetReader = new DataSetReader(this.inputDirectory);
		dataSetReader.open(DataSetType.Job);

		System.out.println("Build item profile");
		JobDTO dto = null;
		int count = 0;
		int xx = 0;
		while ((dto = dataSetReader.nextJob()) != null) {			
			if(xx++ > 1000)
			{
				break;
			}
			System.out.println("ItemId: " +count++ );
			String itemId = dto.getJobId() + "";
			String content = dto.getJobName() + ". ";
			content += dto.getRequirement() + ". ";
			content += dto.getLocation() + ". ";
			content += dto.getTags() + ". ";
			content += dto.getDescription() + ". ";
			content += dto.getCategory() + ". ";
			memDocProcessor.addJob(itemId,content);
		}
		
		dataSetReader = new DataSetReader(this.inputDirectory);
		dataSetReader.open(DataSetType.Job);

		System.out.println("Build item profile");
		ScoreDTO sdto = null;	
		while ((sdto = dataSetReader.nextScore()) != null) {		
			if(sdto.getScore() > 3)
			{
				memDocProcessor.addRating(sdto.getUserId()+"", sdto.getJobId()+"");				
			}
		}		
	}

	
	
	public void run() throws IOException {

		readDataSet();
		int topN = 10;			
		for(String i : this.memDocProcessor.getListUsers())
		{
			System.out.println("Calcualte cosineSimilarity for user " + i);
			memDocProcessor.recommend(i, 10,outputDirectory);	
		}		
	}

}
