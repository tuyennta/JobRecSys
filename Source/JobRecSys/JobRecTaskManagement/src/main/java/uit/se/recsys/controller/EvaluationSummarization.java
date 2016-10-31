package uit.se.recsys.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uit.se.recsys.bean.RowInfoBean;
import uit.se.recsys.bean.TaskBean;
import uit.se.recsys.bo.TaskBO;
import uit.se.recsys.utils.SecurityUtil;

@Controller
public class EvaluationSummarization {
    
    @Autowired
    TaskBO taskBO;
    @Value("${Dataset.Location}")
    String ROOT_PATH;    
    
    @RequestMapping(value = "/tong-hop", method = RequestMethod.GET)
    public String init(Model model){
	HashMap<String, List<RowInfoBean>> maps = taskBO.getRowInfos();
	model.addAttribute("rowInfos", maps);
	createExcelFile(maps);
	return "evaluationSummarization";
    }
        
    private void createExcelFile(HashMap<String, List<RowInfoBean>> maps){	
	try {
	    File evaluationFile = new File(ROOT_PATH + "evaluation.xls");
	    if(evaluationFile.exists()){
		Files.delete(evaluationFile.toPath());
	    }
	    FileOutputStream fos = new FileOutputStream(evaluationFile);	    
	    Workbook workbook = new HSSFWorkbook();
	    
	    for(String sheetName : maps.keySet()){
		HSSFSheet workSheet = (HSSFSheet) workbook.createSheet(sheetName);
		int rowIndex = 0;
		for(RowInfoBean rowInfo : maps.get(sheetName)){
		    HSSFRow row = workSheet.createRow(rowIndex++);
		    int columnIndex = 0;
		    HSSFCell firstCell = row.createCell(columnIndex++);
		    firstCell.setCellValue(rowInfo.getDisplayName());
		    for(Float value : rowInfo.getScores()){
			HSSFCell cell = row.createCell(columnIndex++);
			if(!Float.isNaN(value))
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
//	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
//	    return "redirect:/dang-nhap";
//	}

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
