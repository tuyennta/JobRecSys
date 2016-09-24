package recsys.algorithms.hybird;

import recsys.algorithms.RecommendationAlgorithm;
import recsys.algorithms.cbf.CB;

public class HybirdRecommeder extends RecommendationAlgorithm{
	public void hibridRecommend()
	{
		CB cb = new CB();
		cb.setInputDirectory(inputDirectory);
		cb.setOutputDirectory(outputDirectory);
		
		try
		{
			cb.recommend();
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
