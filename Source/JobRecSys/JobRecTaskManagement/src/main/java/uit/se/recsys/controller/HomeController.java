package uit.se.recsys.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import uit.se.recsys.bean.TaskBean;
import uit.se.recsys.bean.TaskCFBean;
import uit.se.recsys.bo.TaskBO;
import uit.se.recsys.bo.UserBO;
import uit.se.recsys.utils.DatasetUtil;
import uit.se.recsys.utils.SecurityUtil;
import uit.se.recsys.utils.StripAccentUtil;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

    @Value("${Dataset.Location}")
    String ROOT_PATH;    
    @Value("${JobRecAlgComparer.Location}")    
    String jRACLocation;

    @Autowired
    UserBO userService;
    @Autowired
    TaskBO taskBO;
    @Autowired    
    DatasetUtil dsUtil;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
	binder.setAllowedFields(
			new String[] { "taskName", "algorithm", "dataset" });
    }

    @RequestMapping(value = { "/", "/trang-chu" }, method = RequestMethod.GET)
    public String home(HttpSession session, Model model) {

	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	bindingData(model);
	return "home";
    }

    @RequestMapping(value = { "/", "trang-chu" }, method = RequestMethod.POST)
    public String createTask(@ModelAttribute("task") TaskCFBean task,@RequestParam("config") MultipartFile config,
			     BindingResult result, Model model,
			     HttpSession session) {

	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	/* Set task info and save it */
	task.setStatus("Running");
	task.setType("rec");
	task.setTimeCreate(new Timestamp(new Date().getTime()));
	task.setTaskName(task.getAlgorithm() + "-" + task.getDataset() + "-" + task.getTimeCreate().getHours() + "-" + task.getTimeCreate().getMinutes());
	task.setUserId(SecurityUtil.getInstance().getUserId());
	taskBO.addTask(task);		

	/* Save config file*/
	String path = ROOT_PATH + task.getUserId() + File.separator
			+ task.getDataset() + File.separator;
	int taskId = taskBO.generateId();
	String taskName = new StripAccentUtil().convert(task.getTaskName().replaceAll(" ", "-"));
	if(!config.isEmpty()){
	    try {
		byte[] cBytes = config.getBytes();
		File dir = new File(path + "output\\" + taskId + "_" + taskName + File.separator + task.getAlgorithm());
		if(!dir.exists()){
		    dir.mkdirs();
		}
		File fileConfig = new File(dir.getAbsolutePath() + File.separator + "config.properties");
		BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileConfig));
		outStream.write(cBytes);
		outStream.close();		
	    } catch (IOException e) {
		e.printStackTrace();
	    }	    
	}
	
	/* execute algorithm */
	executeAlgorithm(task.getAlgorithm(),
			path + "input\\",
			path + "output\\" + taskId + "_" + taskName
			+ File.separator + task.getAlgorithm() + "\\", 
			taskId);

	bindingData(model);
	return "home";
    }

    private void bindingData(Model model) {

	/* Binding new TaskBean and dataset to view */
	String userId = String.valueOf(SecurityUtil.getInstance().getUserId());
	model.addAttribute("task", new TaskCFBean());
	model.addAttribute("datasets",
			dsUtil.getDatasets(ROOT_PATH
					+ userId));

	/* Binding list of task to view */
	model.addAttribute("listTask", taskBO.getAllRecommendationTasks(userId));
    }

    private void executeAlgorithm(String algorithm, String input,
				  String output, int taskId) {
	try {
	    
	    /* Create directory to save output files */
	    File dOut = new File(output);
	    if (!dOut.exists()) {
		dOut.mkdirs();
	    }
	    
	    /* Create command file to execute .jar file */
	    File commandFile = new File(output + "command.bat");
	    if (!commandFile.exists()) {
		commandFile.createNewFile();
	    }
	    FileWriter fw = new FileWriter(commandFile.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write("java -jar " + jRACLocation + " rec " + algorithm + " " 
			    	  + input + " " + output + " " + taskId);
	    bw.write("\n exit");
	    bw.close();
	    fw.close();

	    /* Run command file */
	    Runtime.getRuntime().exec("cmd /c start " + output + "command.bat");

	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }
    
    @RequestMapping(value="trang-chu/updateTask", method=RequestMethod.POST, produces="application/json")
    @ResponseBody
    public HashMap<Integer, String> updateTask(HttpSession session, ModelAndView model){
	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    model.setViewName("redirect:/dang-nhap");
	}
	
	return taskBO.getTaskStatus("rec");
    }
    
    @RequestMapping(value={"/xoa-task"}, method=RequestMethod.POST)   
    @ResponseBody
    public String deleteTask(HttpSession session, Model model, @RequestBody String taskId){
	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}	
	taskId = taskId.replace("taskId=", "");
	//delete task from files
	TaskBean task = taskBO.getTaskById(Integer.valueOf(taskId));
	String path = ROOT_PATH + task.getUserId() + File.separator
			+ task.getDataset() + "\\output\\" + taskId + "_" + task.getTaskName() + "\\";
	try {
	    FileUtils.forceDelete(new File(path));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	//delete task from database
	taskBO.deleteTask(taskId);
	
	//binding data again
	bindingData(model);
	
	return "home";
    }
}
