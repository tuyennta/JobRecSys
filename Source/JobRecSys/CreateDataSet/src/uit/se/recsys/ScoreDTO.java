package uit.se.recsys;

public class ScoreDTO {
	private int userId;
	private int jobId;
	private String rating;
	private String prediction;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getPrediction() {
		return prediction;
	}
	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}
	
}
