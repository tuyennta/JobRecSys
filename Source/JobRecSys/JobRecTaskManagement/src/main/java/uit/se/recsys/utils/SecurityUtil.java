package uit.se.recsys.utils;

import javax.servlet.http.HttpSession;

import uit.se.recsys.bean.UserBean;

public class SecurityUtil {
    private static SecurityUtil instance = new SecurityUtil();
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static SecurityUtil getInstance() {
        return instance;
    }

    public static void setInstance(SecurityUtil instance) {
        SecurityUtil.instance = instance;
    }    
    
    public boolean haveUserLoggedIn(HttpSession session){
	UserBean user = (UserBean) session.getAttribute("user");
	if (user == null || user.getUserName() == null) {
	    return false;
	}
	userId = user.getUserId();
	return true;
    }
}
