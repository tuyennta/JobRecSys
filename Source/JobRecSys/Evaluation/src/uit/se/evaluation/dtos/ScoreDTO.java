package uit.se.evaluation.dtos;

public class ScoreDTO {

	private int itemId;
	private int userId;
	private float score;
	private boolean relevant;

	public boolean isRelevant() {
		return relevant;
	}

	public void setRelevant(boolean relevant) {
		this.relevant = relevant;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int jobId) {
		this.itemId = jobId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public boolean compare(ScoreDTO dto) {
		if (this.userId == dto.getUserId() && this.itemId == dto.getItemId()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this.userId == ((ScoreDTO) obj).getUserId() && this.itemId == ((ScoreDTO) obj).getItemId()) {
			return true;
		} else {
			return false;
		}
	}

}
