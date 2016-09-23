package uit.se.recsys.bo;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    public HashMap<Integer, String> getTaskStatus(String taskType){
	return taskDAO.getTaskStatus(taskType);
    }

    public TaskBean getTaskById(int id) {
	return taskDAO.getTaskById(id);
    }
    
    public int generateId(){
	return taskDAO.generateId();
    }
}
