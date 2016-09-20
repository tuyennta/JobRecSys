package uit.se.recsys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
			properties.load(new FileInputStream(file_name));
			conf.database = properties.getProperty("DATABASE");
			conf.host =  properties.getProperty("HOST");
			conf.userName = properties.getProperty("USER");
			conf.password = properties.getProperty("PASS");
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return conf;

	}

}
