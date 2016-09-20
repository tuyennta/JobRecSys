package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbConfig {

	public String host = "";
	public String userName = "";
	public String password = "";
	public String database = "";

	public static DbConfig load(String file_name) {
		DbConfig conf = new DbConfig();
		Properties properties = new Properties();
		try {
			properties.load(DbConfig.class.getClassLoader().getResourceAsStream(file_name));
			conf.database = properties.getProperty("DATABASE");
			conf.host = properties.getProperty("HOST");
			conf.userName = properties.getProperty("USER");
			conf.password = properties.getProperty("PASS");
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			return null;
		} catch (IOException e) {
			System.out.println("IO exception");
			return null;
		}
		return conf;
	}

}
