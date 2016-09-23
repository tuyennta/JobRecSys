package recsys.algorithms.cbf;

public class CbResults {

	private double max_score = 0.0d;
	public double getMax_score() {
		return max_score;
	}
	public void setMax_score(double max_score) {
		this.max_score = max_score;
	}
	private String userId = "";
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String[] getTopJob() {
		return topJob;
	}
	public void setTopJob(String[] topJob) {
		this.topJob = topJob;
	}
	public double[] getTopScore() {
		return topScore;
	}
	public void setTopScore(double[] topScore) {
		this.topScore = topScore;
	}
	private String[] topJob = null;
	private double[] topScore = null;
}
