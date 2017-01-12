package uit.se.recsys.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uit.se.recsys.bean.UserBean;
import uit.se.recsys.bo.UserBO;
import uit.se.recsys.utils.SecurityUtil;

@Controller
public class RegisterController {

    @Autowired
    UserBO userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
	binder.setAllowedFields(new String[] { "userName", "email", "password",
			"rpassword" });
    }

    @RequestMapping(value = "/dang-ky", method = RequestMethod.GET)
    public String init(Model model, HttpSession session) {
	
	/* Check logged in user */
	if (SecurityUtil.getInstance().haveUserLoggedIn(session)) {
	    return "redirect:/home";
	}
	
	model.addAttribute("user", new UserBean());
	return "register";
    }

    @RequestMapping(value = "/dang-ky", method = RequestMethod.POST)
    public String register(@ModelAttribute("user") @Valid UserBean user,
			   BindingResult result, Model model) {
	userService.validateRegister(user, result);
	if (result.hasErrors()) {
	    model.addAttribute("user", user);
	    return "register";
	} else {
	    if (userService.addUser(user, result))
		return "redirect:/dang-nhap";
	    else
		return "register";
	}
    }
}
