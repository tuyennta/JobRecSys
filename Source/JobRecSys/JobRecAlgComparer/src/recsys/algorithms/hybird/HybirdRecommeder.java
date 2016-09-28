package recsys.algorithms.hybird;
import recsys.algorithms.RecommendationAlgorithm;
import recsys.algorithms.cbf.CB;
import recsys.algorithms.collaborativeFiltering.CollaborativeFiltering;

public class HybirdRecommeder extends RecommendationAlgorithm{
	
	public void HybirdRecommeder()
	{
		
	}
	
	
	private CB cbRecommender ;
	private CollaborativeFiltering cfRecommender ;
	
	public void init()
	{
		cbRecommender = new CB(false);
		cbRecommender.setInputDirectory(inputDirectory);
		cbRecommender.setOutputDirectory(outputDirectory);
		cbRecommender.setTaskId(taskId);
		cfRecommender = new CollaborativeFiltering(this.inputDirectory,this.outputDirectory,taskId);
	}
	
	public void run()
	{
		
	}
			
}
