package uit.se.recsys.bo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    
    public void deleteTask(String taskId) {
	taskDAO.deleteEvaluationForTask(taskId);
	taskDAO.deleteTask(taskId);
    }
    
    public boolean checkIfDatasetIsUsing(String dsname){
	return taskDAO.checkIfDatasetIsUsing(dsname);
    }

    public TaskBean getTaskById(int id) {
	return taskDAO.getTaskById(id);
    }

    public int generateId() {
	return taskDAO.generateId();
    }
    
    public HashMap<String, List<RowInfoBean>> getRowInfosFromFile(String fileLocation) {
	HashMap<String, List<RowInfoBean>> maps = new HashMap<>();
	maps.put("CF", getRowInfo(fileLocation + "cf_online_evaluation.txt", "CF"));
	maps.put("CB", getRowInfo(fileLocation + "cb_online_evaluation.txt", "CB"));
	maps.put("HB", getRowInfo(fileLocation + "hb_online_evaluation.txt", "HB"));
	
	return maps;
    }
    
    public boolean checkIfHaveResult(String fileName){
	File file = new File(fileName);
	if(file.exists())
	    return true;
	return false;
    }
    
    public List<RowInfoBean> getRowInfo(String fileName, String displayName){
	List<RowInfoBean> listRows = new ArrayList<>();
	
	File file = new File(fileName);
	try {
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    String currentLine = "";
	    RowInfoBean rb = new RowInfoBean();
	    List<Float> scores = new ArrayList<>();
	    while((currentLine = br.readLine())!=null){
		scores.add(Float.NaN);
		scores.add(Float.NaN);
		scores.add(Float.valueOf(currentLine.substring(currentLine.indexOf("\t") + 1)));
	    }
	    rb.setScores(scores);
	    rb.setDataset("job_vi");
	    rb.setDisplayName(displayName);
	    rb.setSheetName(displayName);
	    listRows.add(rb);
	    br.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	return listRows;
    }

    /**
     * function get all tasks prepared to binding into excel file
     * @return
     */
    public HashMap<String, List<RowInfoBean>> getRowInfosFromDB() {
	List<TaskBean> tasks = taskDAO.getEvaluationTasks();
	HashMap<String, List<RowInfoBean>> maps = new HashMap<>();
	List<RowInfoBean> cFRows = new ArrayList<>();
	List<RowInfoBean> cBRows = new ArrayList<>();
	List<RowInfoBean> hBRows = new ArrayList<>();
	RowInfoBean rowBean = null;
	HashMap<Integer, TaskBean> rowTask = null;
	List<Integer> ignoreTasks = new ArrayList<>();
	for (int i = 0; i < tasks.size(); i++) {
	    rowBean = new RowInfoBean();
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
	    rowBean.setDisplayName(task.getDisplayName());
	    rowBean.setSheetName(task.getAlgorithm().toUpperCase() + " - " + task.getDataset().toUpperCase());
	    rowBean.setDataset(task.getDataset());
	    List<Float> scores = new ArrayList<>();

	    addScore(rowTask, scores, "P@");
	    addScore(rowTask, scores, "R@");
	    addScore(rowTask, scores, "F1");
	    addScore(rowTask, scores, "NDCG@");
	    addScore(rowTask, scores, "RMSE");
	    addScore(rowTask, scores, "MRR");
	    addScore(rowTask, scores, "MAP");

	    rowBean.setScores(scores);
	    switch (task.getAlgorithm()) {
	    case "cf":
		cFRows.add(rowBean);
		break;
	    case "cb":
		cBRows.add(rowBean);
		break;
	    case "hb":
		hBRows.add(rowBean);
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
