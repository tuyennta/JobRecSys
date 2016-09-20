package uit.se.recsys.bean;

import org.springframework.stereotype.Component;

@Component
public class RecommendedItem {
    int userId;
    int itemId;
    float score;
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public float getScore() {
        return score;
    }
    public void setScore(float score) {
        this.score = score;
    }
}
