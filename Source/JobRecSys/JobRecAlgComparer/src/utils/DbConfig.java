package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DbConfig {

	public String host = "";
	public String userName = "";
	public String password = "";
	public String database = "";
	static Logger log = Logger.getLogger(DbConfig.class.getName());

	/**
	 * Load database configuration file, properties file
	 * @param file_name
	 * @return
	 */
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
			log.error(e);
			return null;
		} catch (IOException e) {
			System.out.println("IO exception");
			log.error(e);
			return null;
		}
		return conf;
	}

}
