package uit.se.recsys.bean;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component
public class MetricBean {
    @NotNull
    private int id;
    @NotNull
    private String name;
    private float score;
    
    public float getScore() {
        return score;
    }
    public void setScore(float score) {
        this.score = score;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
