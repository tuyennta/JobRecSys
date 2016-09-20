package uit.se.evaluation.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import uit.se.evaluation.dtos.ScoreDTO;

/**
 * This utility class is using for reading dataset
 * 
 * @author tuyen
 *
 */
public class DatasetReaderUtil {

	private static BufferedReader buf = null;
	private static Scanner scan;

	/**
	 * prevent instantiate
	 */
	private DatasetReaderUtil() {
	}

	/**
	 * open the dataset file
	 * 
	 * @param _file
	 */
	public static void openFile(String _file) {
		try {
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(_file), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			buf = null;
		} catch (FileNotFoundException e) {
			buf = null;
		}
	}

	/**
	 * read current line in dataset into ScoreDTO object
	 * 
	 * @return
	 */
	public static ScoreDTO nextScore() {
		ScoreDTO dto = new ScoreDTO();
		String data = null;
		try {
			data = buf.readLine();
		} catch (IOException e) {
			close();
			return null;
		}
		if (data == null) {
			return null;
		}
		scan = new Scanner(data);
		scan.useDelimiter("\t");
		dto.setUserId(toInt(scan.next()));
		dto.setItemId(toInt(scan.next()));
		String score = scan.next();
		if (typeIs(score).equals("Float")) {
			dto.setScore(toFloat(score));
		} else {
			dto.setRelevant(toBoolean(score));
		}
		return dto;
	}

	private static void close() {
		if (buf != null) {
			try {
				buf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				buf = null;
			}
			buf = null;
		}
	}

	private static String typeIs(String value) {
		if (value.matches("^([+-]?\\d*\\.?\\d*)$")) {
			return "Float";
		} else {
			return "Boolean";
		}
	}

	private static int toInt(String data) {
		try {
			return Integer.parseInt(data);
		} catch (Exception e) {
			return -1;
		}
	}

	private static float toFloat(String data) {
		try {
			return Float.parseFloat(data);
		} catch (Exception e) {
			return -1.0f;
		}
	}

	private static boolean toBoolean(String data) {
		try {
			return Boolean.parseBoolean(data);
		} catch (Exception e) {
			return false;
		}
	}
}
