package uit.se.recsys.bean;

public class TaskHBBean extends TaskBean {

    @Override
    public String getDisplayName() {
	String displayName = "";
	try {
	    displayName = this.algorithm + " (alpha="
			    + this.config.getProperty("hb.alpha") + ")";
	} catch (Exception e) {
	    return String.valueOf(System.currentTimeMillis());
	}
	return displayName + " - " + this.dataset;
    }

    @Override
    public String getRowName() {
	String rowName = "";
	try {
	    rowName = this.algorithm + "_" + this.config.getProperty("hb.alpha")
			    + "_" + this.dataset + "_"
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
