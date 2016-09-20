package recsys.datapreparer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;

public class DataSetReader {

	private int toInt(String data) {
		try {
			return Integer.parseInt(data);
		} catch (Exception e) {
			return -1;
		}
	}

	private float toFloat(String data) {
		try {
			return Float.parseFloat(data);
		} catch (Exception e) {
			return -1.0f;
		}
	}

	private String source = "";

	public DataSetReader(String data_dir) {
		source = data_dir;
	}

	public DataSetReader() {
		source = null;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String newSource) {
		source = newSource;
	}

	BufferedReader buf = null;
	private Scanner scan;

	public void open(DataSetType _type) {
		String file = "Score.txt";
		switch (_type) {
		case Cv:
			file = "Cv.txt";
			break;
		case Job:
			file = "Job.txt";
			break;
		default:
			break;
		}
		try {
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(source + file), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			buf = null;
		} catch (FileNotFoundException e) {
			buf = null;
		}
	}

	public void openFile(String _file) {
		try {
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(_file), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			buf = null;
		} catch (FileNotFoundException e) {
			buf = null;
		}
	}

	public JobDTO nextJob() {
		if (buf == null) {
			return null;
		}
		JobDTO dto = new JobDTO();
		String data;
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
		dto.setJobId(toInt(scan.next()));
		dto.setJobName(scan.next());
		dto.setLocation(scan.next());
		dto.setSalary(scan.next());
		dto.setCategory(scan.next());
		dto.setRequirement(scan.next());
		dto.setTags(scan.next());
		dto.setDescription(scan.next());

		return dto;
	}

	public CvDTO nextCv() {
		CvDTO dto = new CvDTO();
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
		dto.setAccountId(toInt(scan.next()));
		dto.setResumeId(toInt(scan.next()));
		dto.setUserName(scan.next());
		dto.setCvName(scan.next());
		dto.setAddress(scan.next());
		dto.setExpectedSalary(scan.next());
		dto.setCategory(scan.next());
		dto.setEducation(scan.next());
		dto.setLanguage(scan.next());
		dto.setSkill(scan.next());

		return dto;
	}

	public ScoreDTO nextScore() {
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
		dto.setJobId(toInt(scan.next()));
		dto.setScore(toFloat(scan.next()));
		return dto;
	}

	public void close() {
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

}
