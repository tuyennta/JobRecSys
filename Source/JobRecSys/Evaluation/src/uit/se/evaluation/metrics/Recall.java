/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.se.evaluation.metrics;

import java.util.List;

/**
 * This class content methods for computing metric related to Recall. 
 * Ref:
 * 1. http://en.wikipedia.org/wiki/Precision_and_recall
 * 2. http://www.stanford.edu/class/cs276/handouts/EvaluationNew-handout-6-per.pdf
 * Method: 
 * - computeRecall
 */
public class Recall {

    // Prevent instantiation.
    private Recall() {
    }

    /**
     * This method computes recall based on relevant documents retrieved and
     * total relevant documents
     *
     * @param rankList
     * @param groundTruth
     * @return rec
     */
    public static double computeRecall(List rankList, List groundTruth) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty())) {
            return 0.0;
        }
        
        // true positive
        double tp = 0.0;
        
        for (int i = 0; i < rankList.size(); i++) {
            for(int j = 0; j < groundTruth.size(); j++){
            	if (groundTruth.get(j).equals(rankList.get(i))) {
                    tp++;
                }	
            }        	
        }
        
        // ground truth size = true positive + false negative.
        return (double) tp / groundTruth.size();
    }

    /**
     * 
     * @param rankList
     * @param groundTruth
     * @param topN
     * @return 
     */
    public static double computeRecallTopN(List rankList, List groundTruth, int topN) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty()) || (topN <= 0)) {
            return 0.0;
        }
        
        // true positive
        double tp = 0.0;

        // count to rank list size but divide by original top n.
        int nN = topN;
        if (nN > rankList.size()) {
            nN = rankList.size();
        }
        
        for (int i = 0; i < nN; i++) {
            for(int j = 0; j < groundTruth.size(); j++){
            	if (groundTruth.get(j).equals(rankList.get(i))) {
                    tp++;
                }	
            }        	
        }
        
        // ground truth size = true positive + false negative.
        return (double) tp / groundTruth.size();
    }
}
