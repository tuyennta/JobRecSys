package uit.se.recsys.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uit.se.recsys.bean.UserBean;
import uit.se.recsys.bo.UserBO;
import uit.se.recsys.utils.EmailUtil;

@Controller
public class ChangePasswordController {
    String host = "smtp.gmail.com";
    String port = "587";
    String email = "uit.recsys@gmail.com";
    String pass = "l2t_recsys";
    String website = "sdlab.uit.edu.vn";

    @Autowired
    UserBO userBO;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
	binder.setAllowedFields(new String[] { "email", "password", "rpassword",
			"changePasswordCode" });
    }

    @RequestMapping(value = "/quen-mat-khau", method = RequestMethod.GET)
    public String init(Model model) {
	model.addAttribute("user", new UserBean());
	return "forgotPassword";
    }

    @RequestMapping(value = "/doi-mat-khau.getCode", method = RequestMethod.POST)
    public String getChangePasswordCode(@ModelAttribute("user") UserBean user,
					Model model) {
	Random rand = new Random();
	int code = rand.nextInt(99999);
	userBO.updateChangePasswordCode(code, user.getEmail());
	sendEmail(user.getEmail(), code);
	user.setChangePasswordCode(12345);
	model.addAttribute("user", user);
	return "forgotPassword";
    }

    @RequestMapping(value = "/doi-mat-khau.changePass", method = RequestMethod.POST)
    public String changePassword(@ModelAttribute("user") UserBean user,
				 Model model) {
	String mess = "Đổi mật khẩu thành công! Vui lòng đăng nhập lại để sử dụng hệ thống!";
	if (user.getPassword().equals(user.getRpassword())){
	    if(!userBO.updatePassword(user.getChangePasswordCode(),
			    user.getEmail(), user.getPassword()))
		mess = "Mã đổi mật khẩu không đúng!";
	    
	}else{
	    mess = "Mật khẩu không trùng khớp!";
	}
	    
	model.addAttribute("user", user);
	model.addAttribute("mess", mess);
	return "forgotPassword";
    }

    private String sendEmail(String recipient, int code) {
	String subject = "Mã đổi mật khẩu tài khoản tại " + website;

	String content = "Xin chào " + recipient
			+ "! <br>Đây là mã xác thực đổi mật khẩu của bạn tại "
			+ website + ". <br>Mã: " + code
			+ "<br> Vui lòng sử dụng mã trên nhập vào trang đổi mật khẩu để tiến hành đổi mật khẩu!";
	String resultMessage = "";
	try {
	    EmailUtil.sendEmail(host, port, email, pass, recipient, subject,
			    content);
	    resultMessage = "Chúng tôi đã gửi mã đổi mật khẩu qua email cho bạn, vui lòng kiểm tra email!";
	} catch (Exception ex) {
	    ex.printStackTrace();
	    resultMessage = "Lỗi gửi mail. Sorry!!!" + ex.getMessage();
	}
	return resultMessage;
    }
}
