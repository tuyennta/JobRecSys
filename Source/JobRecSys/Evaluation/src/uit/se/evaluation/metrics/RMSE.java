package uit.se.evaluation.metrics;

import java.util.List;

import uit.se.evaluation.dtos.ScoreDTO;

/**
 * This class is using for compute root mean square error (RMSE)
 * Ref:
 * 1. https://en.wikipedia.org/wiki/Root-mean-square_deviation
 * @author tuyen
 *
 */
public class RMSE {
	
	/**
	 * prevent instantiation
	 */
	private RMSE(){};
	
	public static double computeRMSE(List<ScoreDTO> rankList, List<ScoreDTO> groundTruth){
		if(rankList == null || rankList.size() == 0 || groundTruth == null || groundTruth.size() == 0){
			return 0;
		}
		
		double rmse = 0;		
		double n = 0;
		for(int i = 0; i < rankList.size(); i++){
			for(int j = 0; j < groundTruth.size(); j++){
				ScoreDTO s1 = rankList.get(i);
				ScoreDTO s2 = groundTruth.get(j);
				if(s1.equals(s2)){
					rmse += Math.pow((s1.getScore() - s2.getScore()), 2);
					n++;
				}
			}
		}
		if(n == 0){
			return 0;
		}
		rmse = Math.sqrt(rmse/n);
		return rmse;
	}
}
