package uit.se.recsys.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uit.se.recsys.bean.TaskBean;
import uit.se.recsys.bo.TaskBO;
import uit.se.recsys.utils.DatasetUtil;
import uit.se.recsys.utils.SecurityUtil;

@Controller
@PropertySource("classpath:/config/datasetLocation.properties")
public class ExperimentResult {

    @Autowired
    TaskBO taskBO;
    @Value("${Dataset.Location}")
    String ROOT_PATH;
    @Autowired
    DatasetUtil dsUtil;

    @RequestMapping(value = "/ket-qua-thuat-toan", method = RequestMethod.GET)
    public String bindingExperimentResult(HttpSession session, @RequestParam("taskid") int taskId,
		       Model model) {

	/* Check log in */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	/* Variables */
	TaskBean task = taskBO.getTaskById(taskId);

	/* Binding task into view */
	model.addAttribute("task", task);

	/* Biding result into view */
	model.addAttribute("recommendedItems", 
			dsUtil.getRecommendedItems(dsUtil.getOutputLocation(task)+ "Score.txt"));

	return "experimentResult";
    }
    
    @RequestMapping(value = "/ket-qua-danh-gia", method = RequestMethod.GET)
    public String bindingEvaluationResult(HttpSession session, @RequestParam("taskid") int taskId,
		       Model model) {

	/* Check log in */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	/* Variables */
	TaskBean task = taskBO.getTaskById(taskId);

	/* Binding task into view */
	model.addAttribute("task", task);

	return "evaluationResult";
    }

    @RequestMapping(value = "/ket-qua-thuat-toan.tai-ve", method = RequestMethod.GET)
    public String download(HttpServletResponse response, HttpSession session,
			   @RequestParam("task") int taskId) {

	/* Check log in */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	/* Find file location */
	TaskBean task = taskBO.getTaskById(taskId);
	File downloadFile = new File(dsUtil.getOutputLocation(task) + "Score.txt");
	try {
	    FileInputStream inputStream = new FileInputStream(downloadFile);

	    /* Set content attribute for response */
	    response.setContentType("application/octec-stream");
	    response.setContentLength((int) downloadFile.length());

	    /* Set header for response */
	    String headerKey = "Content-Disposition";
	    String headerValue = String.format("attachment; filename=\"%s\"",
			    downloadFile.getName());
	    response.setHeader(headerKey, headerValue);

	    /* Get output stream of the response */
	    OutputStream outputStream = response.getOutputStream();

	    /* Write file to output */
	    byte[] buffer = new byte[4069];
	    int byteRead = -1;
	    while ((byteRead = inputStream.read(buffer)) != -1) {
		outputStream.write(buffer, 0, byteRead);
	    }
	    inputStream.close();
	    outputStream.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return "experimentResult";
    }
}
