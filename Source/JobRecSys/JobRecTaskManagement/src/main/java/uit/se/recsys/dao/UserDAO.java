package uit.se.recsys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import uit.se.recsys.bean.UserBean;

@Repository
public class UserDAO {
    @Autowired
    DataSource dataSource;
    
    Connection connection = null;

    public boolean addUser(UserBean user) {
	String sql = "insert into user (UserName, Email, Password) values (?, ?, ?)";
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(sql);
	    stm.setString(1, user.getUserName());
	    stm.setString(2, user.getEmail());
	    stm.setString(3, user.getPassword());
	    stm.executeUpdate();
	    stm.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public UserBean getUserByUserName(String userName) {
	UserBean user = null;
	String sql = "select * from user where UserName = ?";
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(sql);
	    stm.setString(1, userName);
	    ResultSet rs = stm.executeQuery();
	    if (rs.next()) {
		user = new UserBean();
		user.setUserName(rs.getString("UserName"));
		user.setEmail(rs.getString("Email"));
		user.setPassword(rs.getString("Password"));
		user.setUserId(rs.getInt("UserId"));
		user.setChangePasswordCode(rs.getInt("ChangePasswordCode"));
	    }
	    rs.close();
	    stm.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return user;
    }

    public UserBean getUserByEmail(String email) {
	String sql = "select * from user where email = ?";
	UserBean user = null;
	try {
	    connection = dataSource.getConnection();
	    PreparedStatement stm = connection.prepareStatement(sql);
	    stm.setString(1, email);
	    ResultSet rs = stm.executeQuery();
	    if (rs.next()) {
		user = new UserBean();
		user.setUserName(rs.getString("UserName"));
		user.setEmail(rs.getString("Email"));
		user.setPassword(rs.getString("Password"));
		user.setUserId(rs.getInt("UserId"));
		user.setChangePasswordCode(rs.getInt("ChangePasswordCode"));
	    }
	    rs.close();
	    stm.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return user;
    }
}
