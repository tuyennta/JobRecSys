package uit.se.recsys.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uit.se.recsys.bean.UserBean;
import uit.se.recsys.bo.UserBO;

@Controller
@SessionAttributes("user")
public class LoginController {

    @Autowired
    UserBO userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
	binder.setAllowedFields(
			new String[] { "email", "password", "userName" });
    }

    @RequestMapping(value = { "/dang-nhap" }, method = RequestMethod.GET)
    public String init(Model model, HttpSession session) {
	UserBean user = (UserBean) session.getAttribute("user");
	if (user == null || user.getUserName() == null) {
	    user = new UserBean();
	    model.addAttribute("user", user);
	    model.addAttribute("loginError",
			    "Bạn chưa đăng nhập, vui lòng đăng nhập để sử dụng hệ thống!");
	    return "login";
	}
	return "redirect:/";
    }

    @RequestMapping(value = "/dang-nhap", method = RequestMethod.POST)
    public ModelAndView login(@ModelAttribute("user") UserBean user,
			      BindingResult result, HttpSession session) {
	ModelAndView model = new ModelAndView();
	UserBean validatedUser = userService.validateLogin(user, result);
	if (validatedUser != null) {
	    session.setMaxInactiveInterval(60 * 60);
	    user.setUserId(validatedUser.getUserId());
	    user.setUserName(validatedUser.getUserName());
	    user.setPassword("");
	    model.setViewName("redirect:/");
	} else {
	    model.setViewName("login");
	}
	model.addObject("user", user);
	return model;
    }

    @RequestMapping(value = "/dang-xuat", method = RequestMethod.GET)
    public String logout(SessionStatus sessionStatus) {
	sessionStatus.setComplete();
	return "login";
    }
}
