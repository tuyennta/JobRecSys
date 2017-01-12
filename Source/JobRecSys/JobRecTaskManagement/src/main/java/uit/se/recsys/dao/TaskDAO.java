package uit.se.recsys.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import uit.se.recsys.bean.MetricBean;
import uit.se.recsys.bean.TaskBean;
import uit.se.recsys.bean.TaskCBBean;
import uit.se.recsys.bean.TaskCFBean;
import uit.se.recsys.bean.TaskHBBean;
import uit.se.recsys.utils.DatasetUtil;

@Repository
public class TaskDAO {
    @Autowired
    DataSource dataSource;
    @Autowired
    DatasetUtil datasetUtil;

    Connection connection = null;

    public boolean addTask(TaskBean task) {
	String sql = "insert into task (UserId, TaskName, TimeCreate, Status, Algorithm, Dataset, TaskType, EvaluationType, EvaluationParam, ExecutionTime) values (?,?,?,?,?,?,?,?,?,?)";
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement statement = connection.prepareStatement(sql);
	    statement.setInt(1, task.getUserId());
	    statement.setString(2, task.getTaskName());
	    statement.setTimestamp(3, task.getTimeCreate());
	    statement.setString(4, task.getStatus());
	    statement.setString(5, task.getAlgorithm());
	    statement.setString(6, task.getDataset());
	    statement.setString(7, task.getType());
	    statement.setString(8, task.getEvaluationType());
	    statement.setInt(9, task.getEvaluationParam());
	    statement.setString(10, "0");
	    if (statement.executeUpdate() > 0) {
		statement.close();
		connection.close();
		return true;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return false;
    }

    public List<TaskBean> getRecommendationTasks(String userId) {
	String sql = "select * from task where TaskType = 'rec' and UserId = ?";
	List<TaskBean> taskBeans = new ArrayList<TaskBean>();
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement statement = connection.prepareStatement(sql);
	    statement.setString(1, userId);
	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		TaskBean task = null;
		switch (rs.getString("Algorithm")) {
		case "cf":
		    task = new TaskCFBean();
		    break;
		case "cb":
		    task = new TaskCBBean();
		    break;
		case "hb":
		    task = new TaskHBBean();
		    break;
		default:
		    break;
		}
		task.setTaskId(rs.getInt("TaskId"));
		task.setUserId(rs.getInt("UserId"));
		task.setAlgorithm(rs.getString("Algorithm"));
		task.setDataset(rs.getString("Dataset"));
		task.setStatus(rs.getString("Status"));
		task.setExecutionTime(convertExecutionTime(
				rs.getString("ExecutionTime")));
		task.setTaskName(rs.getString("TaskName"));
		task.setTimeCreate(rs.getTimestamp("TimeCreate"));
		task.setType(rs.getString("TaskType"));
		task.setConfig(readConfig(task));
		taskBeans.add(task);
	    }
	    rs.close();
	    statement.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return taskBeans;
    }

    private Properties readConfig(TaskBean task) {
	Properties config = new Properties();
	try {
	    config.load(new FileInputStream(datasetUtil.getOutputLocation(task)
			    + "config.properties"));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return config;
    }

    public List<TaskBean> getEvaluationTasks(String userId) {
	String sql = "select * from task where TaskType = 'eval' and UserId = ?";

	List<TaskBean> taskBeans = new ArrayList<TaskBean>();
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement statement = connection.prepareStatement(sql);
	    statement.setString(1, userId);
	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		TaskBean task = null;
		switch (rs.getString("Algorithm")) {
		case "cf":
		    task = new TaskCFBean();
		    break;
		case "cb":
		    task = new TaskCBBean();
		    break;
		case "hb":
		    task = new TaskHBBean();
		    break;
		default:
		    break;
		}
		task.setTaskId(rs.getInt("TaskId"));
		task.setUserId(rs.getInt("UserId"));
		task.setAlgorithm(rs.getString("Algorithm"));
		task.setDataset(rs.getString("Dataset"));
		task.setStatus(rs.getString("Status"));
		task.setTaskName(rs.getString("TaskName"));
		task.setTimeCreate(rs.getTimestamp("TimeCreate"));
		task.setType(rs.getString("TaskType"));
		task.setExecutionTime(convertExecutionTime(
				rs.getString("ExecutionTime")));
		task.setEvaluationParam(Integer
				.parseInt(rs.getString("EvaluationParam")));
		task.setMetrics(getMetricOfTask(task.getTaskId()));
		task.setEvaluationType(rs.getString("EvaluationType"));
		task.setConfig(readConfig(task));
		taskBeans.add(task);
	    }
	    rs.close();
	    statement.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return taskBeans;
    }

    public HashMap<Integer, String> getTaskStatus(String taskType) {
	String sql = "select TaskId, Status from task where TaskType = '"
			+ taskType + "'";
	HashMap<Integer, String> tasks = new HashMap<>();
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(sql);
	    ResultSet rs = stm.executeQuery();
	    while (rs.next()) {
		tasks.put(rs.getInt("TaskId"), rs.getString("Status"));
	    }
	    rs.close();
	    stm.close();
	    connection.close();
	    return tasks;
	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * function delete a task by taskid
     * 
     * @param taskId
     */
    public void deleteTask(String taskId) {
	String sql = "delete from task where TaskId = ?";

	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(sql);
	    stm.setString(1, taskId);
	    stm.executeUpdate();
	    stm.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * function delete a task by taskid
     * 
     * @param taskId
     */
    public void deleteEvaluationForTask(String taskId) {
	String sql = "delete from evaluation where TaskId = ?";

	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(sql);
	    stm.setString(1, taskId);
	    stm.executeUpdate();
	    stm.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public boolean checkIfDatasetIsUsing(String dsname) {
	String sql = "select * from task where Dataset= ?";
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(sql);
	    stm.setString(1, dsname);
	    ResultSet rs = stm.executeQuery();
	    if (rs.next()) {
		stm.close();
		connection.close();
		return true;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return false;
    }

    private List<MetricBean> getMetricOfTask(int taskId) {
	List<MetricBean> metrics = new ArrayList<MetricBean>();
	String sql = "select Metric, Score from evaluation, task where task.TaskId = evaluation.TaskId and task.TaskId = "
			+ taskId;
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement statement = connection.prepareStatement(sql);
	    ResultSet rs = statement.executeQuery();
	    MetricBean metric;
	    while (rs.next()) {
		metric = new MetricBean();
		metric.setName(rs.getString("Metric"));
		metric.setScore(rs.getFloat("Score"));
		metrics.add(metric);
	    }
	    rs.close();
	    statement.close();
	    connection.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return metrics;
    }

    public TaskBean getTaskById(int id) {
	String sql = "select * from task where TaskId = ?";
	TaskBean task = null;
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement statement = connection.prepareStatement(sql);
	    statement.setInt(1, id);
	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		switch (rs.getString("Algorithm")) {
		case "cf":
		    task = new TaskCFBean();
		    break;
		case "cb":
		    task = new TaskCBBean();
		    break;
		case "hb":
		    task = new TaskHBBean();
		    break;
		default:
		    break;
		}
		task.setTaskId(rs.getInt("TaskId"));
		task.setUserId(rs.getInt("UserId"));
		task.setAlgorithm(rs.getString("Algorithm"));
		task.setDataset(rs.getString("Dataset"));
		task.setStatus(rs.getString("Status"));
		task.setTaskName(rs.getString("TaskName"));
		task.setTimeCreate(rs.getTimestamp("TimeCreate"));
		task.setType(rs.getString("TaskType"));
		task.setExecutionTime(convertExecutionTime(
				rs.getString("ExecutionTime")));
		task.setConfig(readConfig(task));
		task.setMetrics(getMetricOfTask(task.getTaskId()));
	    }
	    rs.close();
	    statement.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return task;
    }

    private String convertExecutionTime(String time) {
	long _time = Long.valueOf(time);
	int min = 0;
	int sec = 0;
	int hour = 0;
	String converted = "0 giây";
	if (_time <= 60) {
	    converted = _time + " giây";
	} else {
	    min = (int) _time / 60;
	    sec = (int) _time % 60;
	    if (min <= 60) {
		converted = min + " phút, " + sec + " giây";
	    } else {
		hour = min / 60;
		min %= 60;
		converted = hour + " giờ, " + min + " phút, " + sec + " giây";
	    }
	}
	return converted;
    }

    public int generateId() {
	int maxId = 1;
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(
			    "select max(TaskId) as maxId from task");
	    ResultSet rs = stm.executeQuery();
	    if (rs.next()) {
		maxId = rs.getInt("maxId");
	    }
	    rs.close();
	    stm.close();
	    connection.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return maxId;
    }
}
