package uit.se.recsys.bean;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

@Component
public class TaskBean {
    int taskId;
    int userId;
    @NotNull
    @NotEmpty
    String taskName;
    Timestamp timeCreate;
    String status;
    @NotNull
    @NotEmpty
    String algorithm;
    @NotNull
    @NotEmpty
    String dataset;
    @NotNull
    @NotEmpty
    String type;
    List<MetricBean> metrics;
    int evaluationParam;
    @NotNull
    @NotEmpty
    String evaluationType;
    Properties config;
    int testSize;
    int testFold;

    public int getTaskId() {
	return taskId;
    }

    public void setTaskId(int taskId) {
	this.taskId = taskId;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public String getTaskName() {
	return taskName;
    }

    public void setTaskName(String taskName) {
	this.taskName = taskName;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getAlgorithm() {
	return algorithm;
    }

    public void setAlgorithm(String algorithm) {
	this.algorithm = algorithm;
    }

    public Timestamp getTimeCreate() {
	return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
	this.timeCreate = timeCreate;
    }

    public String getDataset() {
	return dataset;
    }

    public void setDataset(String dataset) {
	this.dataset = dataset;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public List<MetricBean> getMetrics() {
	return metrics;
    }

    public void setMetrics(List<MetricBean> metrics) {
	this.metrics = metrics;
    }

    public Properties getConfig() {
	return config;
    }

    public void setConfig(Properties config) {
	this.config = config;
    }

    public int getEvaluationParam() {
	return evaluationParam;
    }

    public void setEvaluationParam(int evaluationParam) {
	this.evaluationParam = evaluationParam;
    }

    public String getEvaluationType() {
	return evaluationType;
    }

    public void setEvaluationType(String evaluationType) {
	this.evaluationType = evaluationType;
    }

    public int getTestSize() {
        return testSize;
    }

    public void setTestSize(int testSize) {
        this.testSize = testSize;
    }

    public int getTestFold() {
        return testFold;
    }

    public void setTestFold(int testFold) {
        this.testFold = testFold;
    }

}
