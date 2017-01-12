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

import uit.se.recsys.bean.TaskBean;
import uit.se.recsys.bean.TaskCFBean;
import uit.se.recsys.bo.TaskBO;
import uit.se.recsys.bo.UserBO;
import uit.se.recsys.utils.DatasetUtil;
import uit.se.recsys.utils.SecurityUtil;
import uit.se.recsys.utils.StripAccentUtil;

@Controller
public class EvaluationController {

    @Value("${Dataset.Location}")
    String ROOT_PATH;
    @Value("${JobRecAlgComparer.Location}")
    String jRACLocation;

    @Autowired
    UserBO userService;
    @Autowired
    TaskBO taskBO;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
	binder.setAllowedFields(new String[] { "algorithm",
			"dataset", "evaluationType", "testSize", "testFold" });
    }

    @RequestMapping(value = "/danh-gia-thuat-toan", method = RequestMethod.GET)
    public String home(HttpSession session, Model model) {

	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	bindingData(model);
	return "evaluation";
    }

    @RequestMapping(value = {"danh-gia-thuat-toan" }, method = RequestMethod.POST)
    public String createTask(@ModelAttribute("task") TaskCFBean task,
			     @RequestParam("config") MultipartFile config,			     
			     @RequestParam("test") MultipartFile test,
			     BindingResult result, Model model,
			     HttpSession session) {

	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	/* Set task info and save it */	
	task.setStatus("Running");
	task.setType("eval");
	task.setTimeCreate(new Timestamp(new Date().getTime()));
	task.setTaskName(task.getAlgorithm() + "-" + task.getDataset() + "-" + task.getTimeCreate().getHours() + "-" + task.getTimeCreate().getMinutes());
	task.setUserId(SecurityUtil.getInstance().getUserId());

	switch (task.getEvaluationType()) {
	case "partitioning":
	    task.setEvaluationParam(task.getTestSize());
	    break;
	case "cross":
	    task.setEvaluationParam(task.getTestFold());
	    break;
	default:
	    break;
	}
	taskBO.addTask(task);

	/* Save config file */
	String path = ROOT_PATH + task.getUserId() + File.separator
			+ task.getDataset() + File.separator;
	int taskId = taskBO.generateId();
	String taskName = new StripAccentUtil()
			.convert(task.getTaskName().replaceAll(" ", "-"));
	saveUploadFile(path+ "evaluation\\" + taskId + "_" + taskName + File.separator, "config.properties", config);
	
	if(task.getEvaluationType().equals("custom"))
	    saveUploadFile(path+ "evaluation\\" + taskId + "_" + taskName + File.separator + "testing\\", "Score.txt", test);

	/* execute algorithm */
	 executeAlgorithm(task.getAlgorithm(), task.getEvaluationType(), task.getEvaluationParam(),
	 path + "input\\",
	 path + "evaluation\\" + taskId + "_"
	 + taskName + File.separator,
	 taskId);

	bindingData(model);
	return "evaluation";
    }
    
    @RequestMapping(value="danh-gia-thuat-toan/updateTask", method=RequestMethod.POST, produces="application/json")
    @ResponseBody
    public HashMap<Integer, String> updateTask(){
	return taskBO.getTaskStatus("eval");
    }
    
    @RequestMapping(value={"/xoa-danh-gia"}, method=RequestMethod.POST)    
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
			+ task.getDataset() + "\\evaluation\\" + taskId + "_" + task.getTaskName() + "\\";
	try {
	    FileUtils.forceDelete(new File(path));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	//delete task from database
	taskBO.deleteTask(taskId);
	
	//binding data again
	bindingData(model);
	
	return "evaluation";
    }
    
    private void saveUploadFile(String path, String name, MultipartFile file){
	if (!file.isEmpty()) {
	    try {
		byte[] cBytes = file.getBytes();
		File dir = new File(path);
		if (!dir.exists()) {
		    dir.mkdirs();
		}
		File fileConfig = new File(dir.getAbsolutePath()
				+ File.separator + name);
		BufferedOutputStream outStream = new BufferedOutputStream(
				new FileOutputStream(fileConfig));
		outStream.write(cBytes);
		outStream.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    private void bindingData(Model model) {

	String userId = String.valueOf(SecurityUtil.getInstance().getUserId());
	
	/* Binding new TaskBean and dataset to view */
	model.addAttribute("task", new TaskCFBean());
	model.addAttribute("datasets", new DatasetUtil().getDatasets(
			ROOT_PATH + userId));

	/* Binding list of task to view */
	model.addAttribute("listTask", taskBO.getAllEvaluationTasks(userId));	
    }

    private void executeAlgorithm(String algorithm, String evalType, int evalParam, String input, String output,
				  int taskId) {
	try {

	    /* Create directory to save output files */
	    File dOut = new File(output);
	    if (!dOut.exists()) {
		dOut.mkdirs();
	    }
	    
	    File dOut_test = new File(output + "testing");
	    if (!dOut_test.exists()) {
		dOut_test.mkdirs();
	    }
	    File dOut_train = new File(output + "training");
	    if (!dOut_train.exists()) {
		dOut_train.mkdirs();
	    }
	    File dOut_result = new File(output + "result");
	    if (!dOut_result.exists()) {
		dOut_result.mkdirs();
	    }

	    /* Create command file to execute .jar file */
	    File commandFile = new File(output + "command.bat");
	    if (!commandFile.exists()) {
		commandFile.createNewFile();
	    }
	    FileWriter fw = new FileWriter(commandFile.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write("java -jar " + jRACLocation + " eval " + algorithm + " "
			    + evalType + " " + evalParam + " " + input + " "
			    + output + " " + taskId);
	    bw.write("\n exit");
	    bw.close();
	    fw.close();

	    /* Run command file */
	    Runtime.getRuntime().exec("cmd /c start " + output + "command.bat");

	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }
}
