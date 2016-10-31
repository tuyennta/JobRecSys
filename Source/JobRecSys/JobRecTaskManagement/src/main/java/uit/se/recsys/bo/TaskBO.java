package uit.se.recsys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uit.se.recsys.bean.MetricBean;
import uit.se.recsys.bean.RowInfoBean;
import uit.se.recsys.bean.TaskBean;
import uit.se.recsys.dao.TaskDAO;

@Service
public class TaskBO {
    @Autowired
    TaskDAO taskDAO;

    public boolean addTask(TaskBean task) {
	return taskDAO.addTask(task);
    }

    public List<TaskBean> getAllRecommendationTasks() {
	return taskDAO.getRecommendationTasks();
    }

    public List<TaskBean> getAllEvaluationTasks() {
	return taskDAO.getEvaluationTasks();
    }

    public HashMap<Integer, String> getTaskStatus(String taskType) {
	return taskDAO.getTaskStatus(taskType);
    }

    public TaskBean getTaskById(int id) {
	return taskDAO.getTaskById(id);
    }

    public int generateId() {
	return taskDAO.generateId();
    }

    public HashMap<String, List<RowInfoBean>> getRowInfos() {
	List<TaskBean> tasks = taskDAO.getEvaluationTasks();
	HashMap<String, List<RowInfoBean>> maps = new HashMap<>();
	List<RowInfoBean> cFRows = new ArrayList<>();
	List<RowInfoBean> cBRows = new ArrayList<>();
	List<RowInfoBean> hBRows = new ArrayList<>();
	RowInfoBean row = null;
	HashMap<Integer, TaskBean> rowTask = null;
	List<Integer> ignoreTasks = new ArrayList<>();
	for (int i = 0; i < tasks.size(); i++) {
	    row = new RowInfoBean();
	    rowTask = new HashMap<>();
	    if (ignoreTasks.contains(i)) {
		continue;
	    }
	    TaskBean task = tasks.get(i);
	    ignoreTasks.add(i);
	    rowTask.put(task.getTopn(), task);
	    for (int j = 0; j < tasks.size(); j++) {
		if (i != j && tasks.get(j).getRowName()
				.equals(task.getRowName())) {
		    rowTask.put(tasks.get(j).getTopn(), tasks.get(j));
		    ignoreTasks.add(j);
		    if (rowTask.size() == 3)
			break;
		}
	    }
	    if (rowTask.size() != 3) {
		TaskBean tmp = null;
		tmp = rowTask.get(5);
		if (tmp == null) {
		    rowTask.put(5, tmp);
		}
		tmp = null;
		tmp = rowTask.get(10);
		if (tmp == null) {
		    rowTask.put(10, tmp);
		}
		tmp = null;
		tmp = rowTask.get(15);
		if (tmp == null) {
		    rowTask.put(15, tmp);
		}
	    }
	    row.setDisplayName(task.getDisplayName());
	    row.setSheetName(task.getAlgorithm().toUpperCase() + " - " + task.getDataset().toUpperCase());
	    row.setDataset(task.getDataset());
	    List<Float> scores = new ArrayList<>();

	    addScore(rowTask, scores, "P@");
	    addScore(rowTask, scores, "R@");
	    addScore(rowTask, scores, "F1");
	    addScore(rowTask, scores, "NDCG@");
	    addScore(rowTask, scores, "RMSE");
	    addScore(rowTask, scores, "MRR");
	    addScore(rowTask, scores, "MAP");

	    row.setScores(scores);
	    switch (task.getAlgorithm()) {
	    case "cf":
		cFRows.add(row);
		break;
	    case "cb":
		cBRows.add(row);
		break;
	    case "hb":
		hBRows.add(row);
		break;
	    default:
		break;
	    }
	}
	if(cFRows.size() > 0)
	maps.put(cFRows.get(0).getSheetName(), cFRows);
	if(cBRows.size() > 0)
	maps.put(cBRows.get(0).getSheetName(), cBRows);
	if(hBRows.size() > 0)
	maps.put(hBRows.get(0).getSheetName(), hBRows);
	return maps;

    }

    private List<Float> addScore(HashMap<Integer, TaskBean> rowTask,
				 List<Float> scores, String name) {
	TaskBean t5 = rowTask.get(5);
	TaskBean t10 = rowTask.get(10);
	TaskBean t15 = rowTask.get(15);

	if (t5 == null) {
	    scores.add(Float.NaN);
	} else
	    for (MetricBean m : t5.getMetrics()) {
		if (m.getName().contains(name)) {
		    scores.add(m.getScore());
		    break;
		}
	    }
	if (t10 == null) {
	    scores.add(Float.NaN);
	} else
	    for (MetricBean m : t10.getMetrics()) {
		if (m.getName().contains(name)) {
		    scores.add(m.getScore());
		    break;
		}
	    }
	if (t15 == null) {
	    scores.add(Float.NaN);
	} else
	    for (MetricBean m : t15.getMetrics()) {
		if (m.getName().contains(name)) {
		    scores.add(m.getScore());
		    break;
		}
	    }
	return scores;
    }
}
