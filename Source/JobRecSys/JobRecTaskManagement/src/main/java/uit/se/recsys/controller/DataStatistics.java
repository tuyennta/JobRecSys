package uit.se.recsys.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uit.se.recsys.bean.UserBean;

@Controller
public class DataStatistics {
    @RequestMapping(value = "/thong-ke-du-lieu", method = RequestMethod.GET)
    public String init(HttpSession session) {
	UserBean user = (UserBean) session.getAttribute("user");
	if (user == null || user.getUserName() == null) {
	    return "redirect:/dang-nhap";
	}
	return "statistics";
    }
}
