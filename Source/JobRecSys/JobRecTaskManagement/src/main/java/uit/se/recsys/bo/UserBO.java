package uit.se.recsys.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import uit.se.recsys.bean.UserBean;
import uit.se.recsys.dao.UserDAO;

@Service
public class UserBO {
    @Autowired
    UserDAO userDAO;

    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(11);

    public void validateRegister(UserBean user, BindingResult error) {
	if (userDAO.getUserByUserName(user.getUserName()) != null)
	    error.rejectValue("userName", "duplicate.user.username");
	if (userDAO.getUserByEmail(user.getEmail()) != null)
	    error.rejectValue("email", "duplicate.user.email");
	if (!user.getPassword().equals(user.getRpassword()))
	    error.rejectValue("password", "error.mismatch.user.password");
    }

    public UserBean validateLogin(UserBean user, BindingResult error) {
	UserBean userDB = userDAO.getUserByEmail(user.getEmail());
	if (userDB == null)
	    error.rejectValue("email", "error.user.email.notExist");
	else {
	    if (!bcrypt.matches(user.getPassword(), userDB.getPassword()))
		error.rejectValue("password", "error.mismatch.user.password");
	}
	return userDB;
    }

    public boolean addUser(UserBean user, BindingResult error) {
	user.setPassword(bcrypt.encode(user.getPassword()));
	if (userDAO.addUser(user)) {
	    return true;
	} else {
	    error.reject("error.mysql.exception");
	    return false;
	}
    }

    public boolean updateChangePasswordCode(int code, String email) {
	return userDAO.updateChangePasswordCode(email, code);
    }
    

    public boolean updatePassword(int code, String email, String newPass) {
	return userDAO.updatePassword(email, code, newPass);
    }
}
