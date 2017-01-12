package uit.se.recsys.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uit.se.recsys.bean.RowInfoBean;
import uit.se.recsys.bo.TaskBO;
import uit.se.recsys.utils.SecurityUtil;

@Controller
public class EvaluationSummarization {

    @Autowired
    TaskBO taskBO;
    @Value("${Dataset.Location}")
    String ROOT_PATH;
    @Value("${JobRecAlgComparer.Location}")
    String jRACLocation;

    @RequestMapping(value = "/tong-hop-danh-gia-offline", method = RequestMethod.GET)
    public String offlineSummarize(Model model, HttpSession session) {
	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}
	
	String userId = String.valueOf(SecurityUtil.getInstance().getUserId());

	HashMap<String, List<RowInfoBean>> maps = taskBO.getRowInfosFromDB(userId);
	model.addAttribute("rowInfos", maps);
	model.addAttribute("type", "offline");
	model.addAttribute("display1", "block");
	model.addAttribute("display2", "none");
	createExcelFile(maps);
	return "evaluationSummarization";
    }

    @RequestMapping(value = "/tong-hop-danh-gia-online", method = RequestMethod.GET)
    public String onlineSummarize(Model model, HttpSession session) {
	/* Check logged in user */
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}
	if (SecurityUtil.getInstance().getUserId() != 1) {
	    model.addAttribute("noti",
			    "Bạn không có quyền xem chức năng này! Vui lòng liên hệ Admin");
	    return "evaluationSummarization";
	}

	String path = ROOT_PATH + "online_evaluation\\";
	File file = new File(path);

	// if never run evaluation then run it
	if (!file.exists() || file.list().length != 6) {
	    executeOnlineEvaluation("cf", 3, 15, path);
	    executeOnlineEvaluation("cb", 3, 15, path);
	    executeOnlineEvaluation("hb", 3, 15, path);
	}

	// check the result
	if(taskBO.checkIfHaveResult(path + "cf_online_evaluation.txt") && taskBO.checkIfHaveResult(path + "cb_online_evaluation.txt") && taskBO.checkIfHaveResult(path + "hb_online_evaluation.txt")){
	    HashMap<String, List<RowInfoBean>> maps = taskBO
				.getRowInfosFromFile(path);
		model.addAttribute("rowInfos", maps);
		createExcelFile(maps);   
		createExcelFile(maps);
	}else{
	    model.addAttribute("noti", "Đang chạy đánh giá, vui lòng đợi!");
	}
	
	model.addAttribute("type", "online");
	model.addAttribute("display1", "none");
	model.addAttribute("display2", "block");
	return "evaluationSummarization";
    }

    private void executeOnlineEvaluation(String algorithm, int truthRank,
					 int topN, String output) {
	try {

	    /* Create directory to save output files */
	    File dOut = new File(output);
	    if (!dOut.exists()) {
		dOut.mkdirs();
	    }

	    /* Create command file to execute .jar file */
	    File commandFile = new File(output + algorithm + "_command.bat");
	    if (!commandFile.exists()) {
		commandFile.createNewFile();
	    }
	    FileWriter fw = new FileWriter(commandFile.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write("java -jar " + jRACLocation + " online_eval " + algorithm
			    + " " + truthRank + " " + topN + " " + output
			    + algorithm + "_online_evaluation.txt");
	    bw.write("\n exit");
	    bw.close();
	    fw.close();

	    /* Run command file */
	    Runtime.getRuntime().exec("cmd /c start " + output + algorithm
			    + "_command.bat");

	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }

    private void createExcelFile(HashMap<String, List<RowInfoBean>> maps) {
	try {
	    File evaluationFile = new File(ROOT_PATH + "evaluation.xls");
	    if (evaluationFile.exists()) {
		Files.delete(evaluationFile.toPath());
	    }
	    FileOutputStream fos = new FileOutputStream(evaluationFile);
	    Workbook workbook = new HSSFWorkbook();

	    for (String sheetName : maps.keySet()) {
		HSSFSheet workSheet = (HSSFSheet) workbook
				.createSheet(sheetName);
		int rowIndex = 0;
		for (RowInfoBean rowInfo : maps.get(sheetName)) {
		    HSSFRow row = workSheet.createRow(rowIndex++);
		    int columnIndex = 0;
		    HSSFCell firstCell = row.createCell(columnIndex++);
		    firstCell.setCellValue(rowInfo.getDisplayName());
		    for (Float value : rowInfo.getScores()) {
			HSSFCell cell = row.createCell(columnIndex++);
			if (!Float.isNaN(value))
			    cell.setCellValue(value);
		    }
		}
	    }
	    workbook.write(fos);
	    fos.flush();
	    fos.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @RequestMapping(value = "/tong-hop.tai-ve", method = RequestMethod.GET)
    public String download(HttpServletResponse response, HttpSession session) {

	/* Check log in */
	// if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	// return "redirect:/dang-nhap";
	// }

	/* Find file location */

	File downloadFile = new File(ROOT_PATH + "evaluation.xls");
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
	return "evaluationSummarization";
    }
}
