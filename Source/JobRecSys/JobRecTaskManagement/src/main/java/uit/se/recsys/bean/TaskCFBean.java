package uit.se.recsys.bean;

import org.springframework.stereotype.Component;

@Component
public class TaskCFBean extends TaskBean {

    @Override
    public String getDisplayName() {
	String displayName = "";
	try {
	    displayName = this.algorithm + ", "
			    + this.config.getProperty("cf.type") + ", "
			    + this.config.getProperty("cf.similarity");
	    if (this.config.getProperty("cf.type").equals("UserBased")) {
		if (this.config.getProperty("cf.neighborhood.type")
				.equals("NearestNUserNeighborhood")) {
		    displayName += ", top "
				    + this.config.getProperty(
						    "cf.neighborhood.param.topn")
				    + " user neighborhood";
		} else {
		    displayName += ", threshold "
				    + this.config.getProperty(
						    "cf.neighborhood.param.threshold")
				    + "user neighborhood";
		}
	    }
	} catch (Exception e) {
	    return String.valueOf(System.currentTimeMillis());
	}
	return displayName + " - " + this.dataset;
    }

    @Override
    public String getRowName() {
	String rowName = "";
	try {
	    rowName = this.algorithm + "_" + this.dataset + "_"
			    + this.config.getProperty("cf.type") + "_"
			    + this.config.getProperty("cf.similarity");
	    if (this.config.getProperty("cf.type").equals("UserBased")) {
		rowName += "_" + this.config.getProperty("cf.neighborhood.type")
				+ "_";
		if (this.config.getProperty("cf.neighborhood.type")
				.equals("NearestNUserNeighborhood")) {
		    rowName += this.config
				    .getProperty("cf.neighborhood.param.topn");
		} else {
		    rowName += this.config.getProperty(
				    "cf.neighborhood.param.threshold");
		}
	    }
	} catch (Exception e) {
	    return String.valueOf(System.currentTimeMillis());
	}

	return rowName + "_" + this.config.getProperty("relevant.score");
    }
}
