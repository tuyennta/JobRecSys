package uit.se.recsys.bean;

import java.util.List;

public class RowInfoBean {
    String displayName;
    String sheetName;
    String dataset;
    List<Float> scores;

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    public String getDataset() {
	return dataset;
    }

    public void setDataset(String dataset) {
	this.dataset = dataset;
    }

    public List<Float> getScores() {
	return scores;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setScores(List<Float> scores) {
	this.scores = scores;
    }

}
