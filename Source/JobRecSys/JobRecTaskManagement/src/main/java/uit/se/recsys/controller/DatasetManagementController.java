package uit.se.recsys.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import uit.se.recsys.bo.TaskBO;
import uit.se.recsys.utils.DatasetUtil;
import uit.se.recsys.utils.SecurityUtil;

@Controller
public class DatasetManagementController {
    @Value("${Dataset.Location}")
    private String ROOT_PATH;

    @Autowired
    private TaskBO taskBO;

    @RequestMapping(value = "/quan-ly-dataset", method = RequestMethod.GET)
    public String init(Model model, HttpSession session) {
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	} else {
	    model.addAttribute("datasets",
			    new DatasetUtil().getDatasets(ROOT_PATH
					    + SecurityUtil.getInstance()
							    .getUserId()));
	    return "datasetManagement";
	}
    }

    @RequestMapping(value = "/quan-ly-dataset", method = RequestMethod.POST)
    public String uploadDataset(HttpSession session,
				@RequestParam("files") MultipartFile[] files,
				@RequestParam("dataset") String datasetName,
				Model model) {
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	} else {
	    model.addAttribute("message", saveDataset(files, datasetName));
	    model.addAttribute("datasets",
			    new DatasetUtil().getDatasets(ROOT_PATH
					    + SecurityUtil.getInstance()
							    .getUserId()));
	    return "datasetManagement";
	}
    }

    @RequestMapping(value = "/xoa-dataset", method = RequestMethod.POST)
    @ResponseBody
    public String deleteDataset(HttpSession session, Model model,
				@RequestBody String dsname) {
	if (!SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/dang-nhap";
	}

	dsname = dsname.replace("dsname=", "");
	// check if dataset is using
	String noti = "";
	if (taskBO.checkIfDatasetIsUsing(dsname)) {
	    noti = "Dataset này đang được sử dụng không thể xóa, vui lòng xóa các task sử dụng dataset này trước!";
	} else {

	    // delete
	    try {
		FileUtils.forceDelete(new File(ROOT_PATH
				+ SecurityUtil.getInstance().getUserId() + "\\"
				+ dsname));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	return noti;
    }

    private String saveDataset(MultipartFile[] files, String datasetName) {
	String message = ""; /* Message to notify the user */

	if (files.length == 3) {

	    /* Create directory to save input files */
	    File dIn = new File(
			    ROOT_PATH + SecurityUtil.getInstance().getUserId()
					    + File.separator + datasetName
					    + File.separator + "input");
	    if (!dIn.exists()) {
		dIn.mkdirs();
	    }

	    /* Loop through files and save it */
	    for (int i = 0; i < 3; i++) {
		MultipartFile file = files[i];
		try {
		    byte[] bytes = file.getBytes();
		    String fileName = "Job.txt";
		    if (i == 1) {
			fileName = "CV.txt";
		    } else {
			if (i == 2) {
			    fileName = "Score.txt";
			}
		    }
		    File serverFile = new File(dIn.getAbsolutePath()
				    + File.separator + fileName);
		    BufferedOutputStream buffer = new BufferedOutputStream(
				    new FileOutputStream(serverFile));
		    buffer.write(bytes);
		    buffer.close();
		} catch (IOException e) {
		    message = "Lỗi upload file. -- " + e.getMessage();
		    e.printStackTrace();
		}
	    }
	    message = "Upload dataset thành công!";
	} else {
	    message = "Lỗi upload file, vui lòng upload đủ 3 file dataset";
	}
	return message;
    }
}
