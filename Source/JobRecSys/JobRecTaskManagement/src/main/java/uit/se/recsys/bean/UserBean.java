package uit.se.recsys.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.stereotype.Component;

@Component
public class UserBean {
	int userId;
	String userName;
	String email;
	String password;
	String rpassword;
	int ChangePasswordCode;

	public int getChangePasswordCode() {
		return ChangePasswordCode;
	}

	public void setChangePasswordCode(int changePasswordCode) {
		ChangePasswordCode = changePasswordCode;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@NotNull
	@Size(max = 7)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@NotNull
	@Email
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotNull
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NotNull
	public String getRpassword() {
		return rpassword;
	}

	public void setRpassword(String rpassword) {
		this.rpassword = rpassword;
	}

}
