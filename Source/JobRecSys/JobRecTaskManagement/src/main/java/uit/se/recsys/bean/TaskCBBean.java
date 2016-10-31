package uit.se.recsys.bean;

public class TaskCBBean extends TaskBean {

    @Override
    public String getDisplayName() {
	return this.algorithm + " - " + this.dataset;
    }

    @Override
    public String getRowName() {
	return this.algorithm + "_" + this.dataset + "_"
			+ this.config.getProperty("relevant.score");
    }

}
