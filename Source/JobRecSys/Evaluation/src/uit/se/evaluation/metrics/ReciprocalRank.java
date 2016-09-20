/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.se.evaluation.metrics;

import java.util.List;

/**
 * This class content methods for computing metric related to ReciprocalRank.
 * Caller needs to get mean of reciprocal rank later.
 * Ref:
 * 1. https://en.wikipedia.org/wiki/Mean_reciprocal_rank
 * 2. http://www.stanford.edu/class/cs276/handouts/EvaluationNew-handout-6-per.pdf
 */
public class ReciprocalRank {

    // Prevent instantiation.
    private ReciprocalRank() {
    }

    /**
     * This method computes the reciprocal rank of 1 list.
     * @param rankList
     * @param groundTruth
     * @return reciprocal rank.
     */
    public static double computeRR(List rankList, List groundTruth) throws Exception {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty())) {
            return 0.0;
        }

        for (int i = 0; i < rankList.size(); i++) {
            for(int j = 0; j < groundTruth.size(); j++){
            	if (groundTruth.get(j).equals(rankList.get(i))) {
            		// Reciprocal of first relevant item position.
                    return (double) 1 / (i + 1);
                }	
            }        	
        }

        return 0.0;
    }
}
