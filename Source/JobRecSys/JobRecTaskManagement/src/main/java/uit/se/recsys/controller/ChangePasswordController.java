package uit.se.recsys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uit.se.recsys.bean.UserBean;

@Controller
public class ChangePasswordController {

	@InitBinder
	public void initBinder(WebDataBinder binder){
		binder.setAllowedFields(new String[]{
				"email", "password", "rpassword", "changePasswordCode"
		});
	}
	
	@RequestMapping(value="/quen-mat-khau", method = RequestMethod.GET)
	public String init(Model model){
		model.addAttribute("user", new UserBean());
		return "forgotPassword";
	}
	
	@RequestMapping(value="/doi-mat-khau.getCode", method = RequestMethod.POST)
	public String getChangePasswordCode(@ModelAttribute("user") UserBean user, Model model){
		user.setChangePasswordCode(23423);
		model.addAttribute("user", user);
		return "forgotPassword";
	}
	
	@RequestMapping(value="/doi-mat-khau.changePass", method = RequestMethod.POST)
	public String changePassword(@ModelAttribute("user") UserBean user, Model model){
		user.setChangePasswordCode(23423);
		model.addAttribute("user", user);
		return "redirect:dang-nhap";
	}
}
