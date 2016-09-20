package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlDBConnection {
	public String mysqlHost = "";
	public String userName = "";
	public String password = "";
	private Connection connection = null;

	public MysqlDBConnection() {

	}

	public MysqlDBConnection(String host, String user, String pass) {
		this.userName = user;
		this.password = pass;
		this.mysqlHost = host + "?useUnicode=true&characterEncoding=UTF-8";
	}

	public MysqlDBConnection(DbConfig conf) {
		this.userName = conf.userName;
		this.password = conf.password;
		this.mysqlHost = conf.host + conf.database + "?useUnicode=true&characterEncoding=UTF-8";
	}
	
	public Connection getConnection() {
		return connection;
	}

	public Boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(mysqlHost, userName, password);
		} catch (ClassNotFoundException e) {
			return false;
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public ResultSet read(String sql) {
		ResultSet data = null;
		try {
			java.sql.Statement cmd = connection.createStatement();
			data = cmd.executeQuery(sql);

		} catch (SQLException e) {
		}
		return data;
	}

	public Boolean write(String sql) {
		try {
			java.sql.Statement cmd = connection.createStatement();
			return cmd.execute(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public void close() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
