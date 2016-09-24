package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class MysqlDBConnection {
	private String mysqlHost = "";
	private String userName = "";
	private String password = "";
	private Connection connection = null;
	static Logger log = Logger.getLogger(MysqlDBConnection.class.getName());

	/**
	 * Initialize mysql connection by hard code parameter
	 * @param host
	 * @param user
	 * @param pass
	 */
	public MysqlDBConnection(String host, String user, String pass) {
		this.userName = user;
		this.password = pass;
		this.mysqlHost = host + "?useUnicode=true&characterEncoding=UTF-8";
	}

	/**
	 * Initialize mysql connection by configuration file
	 * @param conf
	 */
	public MysqlDBConnection(String file_name) {
		DbConfig conf = DbConfig.load(file_name);
		this.userName = conf.userName;
		this.password = conf.password;
		this.mysqlHost = conf.host + conf.database + "?useUnicode=true&characterEncoding=UTF-8";
	}
	
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Create a connection
	 * @return
	 */
	public Boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(mysqlHost, userName, password);
		} catch (ClassNotFoundException e) {
			log.error(e);
			return false;
		} catch (SQLException e) {
			log.error(e);
			return false;
		}
		return true;
	}

	/**
	 * Execute an query
	 * @param sql
	 * @return
	 */
	public ResultSet read(String sql) {
		ResultSet data = null;
		try {
			java.sql.Statement cmd = connection.createStatement();
			data = cmd.executeQuery(sql);

		} catch (SQLException e) {
			log.error(e);
		}
		return data;
	}

	/**
	 * Execute an insert, update, delete query
	 * @param sql
	 * @return
	 */
	public Boolean write(String sql) {
		try {
			java.sql.Statement cmd = connection.createStatement();
			return cmd.execute(sql);

		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Close current connection
	 */
	public void close() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		}
	}

}
