package recsys.algorithms.cbf;

import java.util.ArrayList;

public class CbRecommededList {
	private ArrayList<String> jobIdList = new ArrayList<String>();
	private ArrayList<Double> scoreList = new ArrayList<Double>();
	public ArrayList<Double> getScoreList() {
		return scoreList;
	}

	public ArrayList<Double> getOriginalScoreList() {
		return originalScoreList;
	}

	public double getMax_score() {
		return max_score;
	}

	private ArrayList<Double> originalScoreList = new ArrayList<Double>();

	public ArrayList<String> getJobsArray() {
		return this.jobIdList;
	}

	public ArrayList<Double> getScoreArray() {
		return this.scoreList;
	}

	
	public void add(String jobIds, double original) {
		jobIdList.add(jobIds);
		scoreList.add(0.0d);
		originalScoreList.add(original);
	}

	public double max_score = 0.0; 
	
	public void update(String jobId, double score) {
		if(score > max_score)
		{			
			max_score = score;
		}
		for (int i = 0; i < this.jobIdList.size(); i++) {
			if (jobIdList.get(i).equals(jobId)) {
				scoreList.set(i, score);
			}
		}
	}

	
	public ArrayList<Double> getHybridValue(double anpha) {
		ArrayList<Double> rs = new ArrayList<Double>();
		for(int i = 0 ; i < this.scoreList.size(); i++)
		{
			double new_score = 1.0d + (this.scoreList.get(i) / this.max_score) * 4.0d;
			new_score =  originalScoreList.get(i) * anpha + (1.0d - anpha) * new_score;
			rs.add(new_score);
		}
		return rs;
	}
}
