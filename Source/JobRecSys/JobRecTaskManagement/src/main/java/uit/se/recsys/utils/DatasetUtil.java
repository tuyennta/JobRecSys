package uit.se.recsys.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import uit.se.recsys.bean.RecommendedItem;
import uit.se.recsys.bean.TaskBean;

@Configuration
public class DatasetUtil {

    public DatasetUtil() {

    }

    /**
     * Get the list dataset in specific location
     * 
     * @param path
     *            {@link String}
     * @return String[] directories
     */
    public String[] getDatasets(String path) {
	File dir = new File(path);
	String[] directories = dir.list(new FilenameFilter() {
	    @Override
	    public boolean accept(File current, String name) {
		return new File(current, name).isDirectory();
	    }
	});
	return directories;
    }

    /**
     * Get file result in specific location with a file name prefix
     * 
     * @param dirPath
     *            {@link String}
     * @param fileNamePrefix
     *            {@link String}
     * @return fileResult[] {@link File}
     */
    public File[] getResultFile(String dirPath, final String fileNamePrefix) {
	File dir = new File(dirPath);
	return dir.listFiles(new FilenameFilter() {

	    @Override
	    public boolean accept(File dir, String name) {
		if (name.startsWith(fileNamePrefix)) {
		    return true;
		}
		return false;
	    }
	});
    }

    /**
     * This function to read line by line in result file and return a list of
     * RecommendedItem
     * 
     * @param path
     *            {@link String}: Full path to result file
     * @return recommendedItems {@link List<RecommendedItem>}
     */
    public List<RecommendedItem> getRecommendedItems(String path) {
	List<RecommendedItem> recommendedItems = new ArrayList<RecommendedItem>();
	try {
	    BufferedReader br = new BufferedReader(new FileReader(path));
	    String currentLine = "";
	    while ((currentLine = br.readLine()) != null) {
		Scanner scanner = new Scanner(currentLine);
		scanner.useDelimiter("\t");
		RecommendedItem item = new RecommendedItem();
		item.setUserId(toInt(scanner.next()));
		item.setItemId(toInt(scanner.next()));
		item.setScore(toFloat(scanner.next()));
		recommendedItems.add(item);
		scanner.close();
	    }
	    br.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return recommendedItems;
    }

    /**
     * Parse an value of string type into integer type
     * 
     * @param data
     *            {@link String}
     * @return valueOfData {@link Integer}
     */
    public int toInt(String data) {
	try {
	    return Integer.parseInt(data);
	} catch (Exception e) {
	    return -1;
	}
    }

    /**
     * Parse an value of string type into float type
     * 
     * @param data
     *            {@link String}
     * @return valueOfData {@link Float}
     */
    public float toFloat(String data) {
	try {
	    return Float.parseFloat(data);
	} catch (Exception e) {
	    return -1.0f;
	}
    }

    @Value("${Dataset.Location}")
    String root;

    public String getOutputLocation(TaskBean task) {
	String path = root + task.getUserId() + File.separator
			+ task.getDataset() + File.separator;
	if (task.getType().equals("rec")) {
	    path += "output\\" + task.getTaskId() + "_"
			    + new StripAccentUtil().convert(task.getTaskName())
					    .replaceAll(" ", "-")
			    + File.separator + task.getAlgorithm()
			    + File.separator;
	} else {
	    path += "evaluation\\" + task.getTaskId() + "_"
		+ new StripAccentUtil().convert(task.getTaskName()).replaceAll(" ", "-") + File.separator;
	}
	return path;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
	return new PropertySourcesPlaceholderConfigurer();
    }
}
