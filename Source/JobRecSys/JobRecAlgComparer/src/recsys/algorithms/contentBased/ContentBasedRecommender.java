package recsys.algorithms.contentBased;

import java.io.FileWriter;
import java.io.IOException;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;
import recsys.algorithms.RecommendationAlgorithm;
import recsys.datapreparer.DataSetReader;
import recsys.datapreparer.DataSetType;

public class ContentBasedRecommender extends RecommendationAlgorithm {

	public ContentBasedRecommender() {
		super();
	}

	private DataSetReader dataSetReader = null;
	private MemoryDocumentProcessor memDocProcessor = new MemoryDocumentProcessor();
	public void test(String[] item, String[] profile) throws IOException {
		MemoryDocumentProcessor memDocProcessor = new MemoryDocumentProcessor();
		for(int i = 0 ; i < item.length; i++)
		{
			memDocProcessor.addItemContent("Item" + i, item[i]);	
		}
		for(int i = 0 ; i < profile.length; i++)
		{
			memDocProcessor.addUserProfile("User" + i, profile[i]);
		}		
		System.out.println("Calcualte idf");
		memDocProcessor.calculateIdfForAllTerm();
		System.out.println("Calcualte tf-idf");		
		memDocProcessor.tfIdfCalculatorForUserNormal();	
		System.out.println("Calcualte cosineSimilarity");
		memDocProcessor.getJaccardSimilarity("E:\\");
		System.out.println("");
		memDocProcessor.print();
		
	}

	private String arrToString(String[] dd)
	{
		StringBuilder sb = new StringBuilder();
		for(String i : dd)
		{
			sb.append(i + "\t");
		}
		return sb.toString();
	}
	
	private void readDataSet()
	{
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
			memDocProcessor.addUserProfile(itemId, content);
		}
		dataSetReader = new DataSetReader(this.inputDirectory);
		dataSetReader.open(DataSetType.Job);

		System.out.println("Build item profile");	
		JobDTO dto = null;		
		int job = 50;
		int count = 0;
		
		try {
			FileWriter out = new FileWriter("item.txt");
			while ((dto = dataSetReader.nextJob()) != null && job > 0) {
				//job--;
				System.out.print(count++ + " ");
				String itemId = dto.getJobId() + "";			
				String content = dto.getJobName() + ". ";
				content += dto.getRequirement() + ". ";
				content += dto.getLocation() + ". ";
				content += dto.getTags() + ". ";
				content += dto.getDescription() + ". ";
				content += dto.getCategory() + ". ";
				String[] data = memDocProcessor.addItemContent(itemId, content);
				if(data != null)
					out.write(itemId + "\t" + arrToString(data) + "\r\n");				
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void addUserPrederences()
	{
		dataSetReader = new DataSetReader(this.inputDirectory);
		dataSetReader.open(DataSetType.Score);
		ScoreDTO sdto = null;
		while ((sdto = dataSetReader.nextScore()) != null) {
			if (sdto.getScore() >= 3) {
				memDocProcessor.addUserPreference(sdto.getUserId() + "",sdto.getJobId()+"");
			}
		}
		dataSetReader.close();
	}
	public void run() throws IOException {		
		
		readDataSet();			
		System.out.println("Calcualte idf");
		memDocProcessor.calculateIdfForAllTerm();		
		System.out.println("Calcualte tf-idf for user");
		memDocProcessor.tfIdfCalculatorForUserNormal();		
		addUserPrederences();
		System.out.println("Calcualte cosineSimilarity");
		//memDocProcessor.getCosineSimilarity(this.outputDirectory);
		//memDocProcessor.getCosineSimilarity();
		memDocProcessor.getJaccardSimilarity(this.outputDirectory);
	}
}
