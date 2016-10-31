package uit.se.recsys;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class App {

	public static String getLanguage(String resumeId) throws Exception {
		String languages = "language: ";
		if (connection.connect()) {
			ResultSet rs = connection.read("select * from language where ResumeId = " + resumeId);
			try {
				while (rs.next()) {
					languages += rs.getString("Name") + " ";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.close();
		}
		return languages;
	}

	public static String getSkill(String resumeId) throws Exception {
		String skill = "skill: ";
		if (connection.connect()) {
			ResultSet rs = connection.read("select * from skill where ResumeId = " + resumeId);
			try {
				while (rs.next()) {
					skill += rs.getString("Name") + ", ";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.close();
		}
		return skill;
	}

	public static String getEducation(String resumeId) throws Exception {
		String data = "education:  ";
		if (connection.connect()) {
			ResultSet rs = connection.read("select * from education where ResumeId = " + resumeId);
			try {
				while (rs.next()) {
					data += rs.getString("EducationLevel") + " ";
					data += rs.getString("EducationMajor") + " ";
					data += rs.getString("EducationDescription") + ", ";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.close();
		}
		return data;
	}

	public static void WriteJobFile(String file_name) throws Exception {
		if (connection.connect()) {
			ResultSet rs = connection.read(
					"SELECT JobId,JobName,Location,Salary,job.Description,Tags, Requirement,Benifit, category.Description as Category FROM job, category WHERE job.CategoryId = category.CategoryId");
			String data = "";
			PrintWriter writer_en = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(file_name + "_EN.txt"), "UTF-8"));
			PrintWriter writer_vi = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(file_name + "_VI.txt"), "UTF-8"));

			while (rs.next()) {
				if (!jobIDs.contains(rs.getInt("JobId"))) {
					continue;
				}
				data = eliminate(rs.getString("JobId")) + "\t";
				data += eliminate(rs.getString("JobName")) + "\t";
				data += eliminate(rs.getString("Location")) + "\t";
				data += eliminate(rs.getString("Salary")) + "\t";
				data += eliminate(rs.getString("Category")) + "\t";
				data += eliminate(rs.getString("Requirement")) + "\t";
				data += eliminate(rs.getString("Tags")) + "\t";
				// data += eliminate(rs.getString("Benifit")) + "\t";
				data += eliminate(rs.getString("Description"));
				if (isVietnamese(data)) {
					writer_vi.println(data);
					jobIDs_VI.add(rs.getInt("JobId"));
				} else {
					jobIDs_EN.add(rs.getInt("JobId"));
					writer_en.println(data);
				}
			}
			writer_vi.close();
			writer_en.close();
			connection.close();
		}
	}

	static List<ScoreDTO> scores = new ArrayList<>();

	public static void ReadRating() throws Exception {
		String sql = "SELECT xxx.AccountId,JobId, Rating " + "FROM	JOB_RECOMMENDED," + "("
				+ "	select account.AccountId, count(JOB_RECOMMENDED.JobId) as rates"
				+ "    from account, job_recommended, resume"
				+ "    where account.AccountId = job_recommended.AccountId"
				+ "    and account.AccountId = resume.AccountId" + "    and job_recommended.Rating > 0"
				+ "    group by account.AccountId" + ") as xxx " + "WHERE job_recommended.AccountId = xxx.AccountId "
				+ "and xxx.rates > 50 " + "and Rating > 0";
		if (connection.connect()) {
			ResultSet rs = connection.read(sql);
			ScoreDTO score = null;
			while (rs.next()) {
				int accountID = rs.getInt("AccountId");
				int jobID = rs.getInt("JobId");
				score = new ScoreDTO();
				score.setUserId(accountID);
				score.setJobId(jobID);
				score.setRating(rs.getString("Rating"));
				scores.add(score);
				if (!userIDs.contains(accountID)) {
					userIDs.add(accountID);
				}
				if (!jobIDs.contains(jobID)) {
					jobIDs.add(jobID);
				}
			}
			connection.close();
		}
	}

	public static void WriteRatingFile(String file_name) {
		try {
			PrintWriter writer_en = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(file_name + "_EN.txt"), "UTF-8"));
			PrintWriter writer_vi = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(file_name + "_VI.txt"), "UTF-8"));
			for (ScoreDTO score : scores) {
				if (jobIDs_VI.contains(score.getJobId())) {
					writer_vi.println(score.getUserId() + "\t" + score.getJobId() + "\t" + score.getRating());
					userIDs_VI.add(score.getUserId());
				}
				if (jobIDs_EN.contains(score.getJobId())) {
					userIDs_EN.add(score.getUserId());
					writer_en.println(score.getUserId() + "\t" + score.getJobId() + "\t" + score.getRating());
				}
			}
			writer_en.close();
			writer_vi.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void WriteCVFile(String file_name) throws Exception {
		String sql = "SELECT    CV.AccountId, " + "               CV.ResumeId, " + "               CV.Title, CV.Name,  "
				+ "               CV.Address, " + "               CV.ExpectedSalary, "
				+ "               category.Description as Category,                 "
				+ "               CV.CareerObjective, " + "               CV.AccountId, "
				+ "               CV.AccountId " + "                 " + " " + "FROM " + "		( "
				+ "			SELECT 			resume.AccountId, " + "							resume.ResumeId, "
				+ "							resume.Title, Resume.Name, 										 "
				+ "							coalesce(DesireSalary,'Negotiate, thương lượng') as ExpectedSalary, "
				+ "							coalesce(career_objective.DesireWorkLocation,resume.Address,'Hồ chí minh') as Address, "
				+ "							coalesce(career_objective.CareerObjective,'Thăng tiến trong sự nghiệp') as CareerObjective "
				+ "			FROM 			resume " + "			LEFT JOIN		career_objective "
				+ "			ON 				resume.ResumeId = career_objective.ResumeId " + "        ) as CV, "
				+ "        care, " + "        category " + "         " + "WHERE	care.AccountId = CV.AccountId "
				+ "AND		care.CategoryId = category.CategoryId  ";

		if (connection.connect()) {
			PrintWriter writer_vi = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(file_name + "_VI.txt"), "UTF-8"));
			PrintWriter writer_en = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(file_name + "_EN.txt"), "UTF-8"));
			ResultSet rs = connection.read(sql);
			while (rs.next()) {
				if (!userIDs.contains(rs.getInt("AccountId"))) {
					continue;
				}
				String data = eliminate(rs.getString("AccountId")) + '\t';
				data += eliminate(rs.getString("ResumeId")) + '\t';
				data += eliminate(rs.getString("Name")) + '\t';
				data += eliminate(rs.getString("Title")) + '\t';
				data += eliminate(rs.getString("Address")) + '\t';
				data += eliminate(rs.getString("ExpectedSalary")) + '\t';
				data += eliminate(rs.getString("Category")) + '\t';
				data += eliminate(getEducation(rs.getString("ResumeId"))) + '\t';
				data += eliminate(getLanguage(rs.getString("ResumeId"))) + '\t';
				data += eliminate(getSkill(rs.getString("ResumeId"))) + '\t';
				// data += eliminate(getLanguage(rs.getString("ResumeId")))+
				// '\t';
				String objective = eliminate(rs.getString("CareerObjective")).trim();
				data += objective == "" ? "Thăng tiến trong sự nghiệp" + '\t' : objective + '\t';
				if (userIDs_VI.contains(rs.getInt("AccountId"))) {
					writer_vi.println(data);
				}
				if (userIDs_EN.contains(rs.getInt("AccountId"))) {
					writer_en.println(data);
				}
			}
			connection.close();
			writer_vi.close();
			writer_en.close();
		}
	}

	public static String eliminate(String str) {
		boolean next = false;
		String buf = "";
		char[] array = str.toCharArray();
		for (int i = 0; i < str.length(); i++) {
			if (array[i] == '\t' || array[i] == '\r' || array[i] == '\n' || array[i] == ' ') {
				next = true;
			} else {
				if (next) {
					buf += " ";
					next = false;
				}
				buf += array[i];
			}
		}
		return buf;
	}

	public static double count(String text) {
		text = text.toLowerCase();
		String unicode = "áàảãạâấầẫẩậăắằặẵẳóòỏõọôốồổỗộơớờởỡợíìĩỉịýỳỷỹỵêếềễểệéèẻẽẹđưứừửữựúùủũụ";
		double cnt = 0;
		for (char c : text.toCharArray()) {
			for (char u : unicode.toCharArray()) {
				if (c == u) {
					cnt++;
					break;
				}
			}
		}
		return cnt;
	}

	public static boolean isVietnamese(String sentence) {
		double val = count(sentence) / sentence.length();
		System.out.println(val);
		return count(sentence) / sentence.length() > 0.08d;
	}

	private static DbConfig conf = DbConfig.load("config/config.properties");
	private static MysqlDBConnection connection = new MysqlDBConnection(conf);
	private static List<Integer> userIDs = new ArrayList<>();
	private static List<Integer> jobIDs = new ArrayList<>();
	private static List<Integer> jobIDs_VI = new ArrayList<>();
	private static List<Integer> userIDs_VI = new ArrayList<>();
	private static List<Integer> jobIDs_EN = new ArrayList<>();
	private static List<Integer> userIDs_EN = new ArrayList<>();

	public static void main(String[] args) throws Exception {
//		ReadRating();
//		WriteJobFile("Job");
//		WriteRatingFile("Score");
//		WriteCVFile("Cvt");
		
		readRatingAndPrediction();
		writeRatingAndPrediction();
	}

	public static void readRatingAndPrediction() {
		String sql = "select * from rankedlist";
		if (connection.connect()) {
			ResultSet rs = connection.read(sql);
			ScoreDTO score = null;
			try {
				while (rs.next()) {
					int accountID = rs.getInt("AccountId");
					int jobID = rs.getInt("JobId");
					score = new ScoreDTO();
					score.setUserId(accountID);
					score.setJobId(jobID);
					score.setRating(rs.getString("Rating"));
					score.setPrediction(rs.getString("Prediction"));
					scores.add(score);
					if (!userIDs.contains(accountID)) {
						userIDs.add(accountID);
					}
					if (!jobIDs.contains(jobID)) {
						jobIDs.add(jobID);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.close();
		}
	}

	public static void writeRatingAndPrediction() {
		try {
			PrintWriter writer_rating = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream("rating.txt"), "UTF-8"));
			PrintWriter writer_prediction = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream("prediction.txt"), "UTF-8"));
			for (ScoreDTO score : scores) {
				writer_prediction.println(score.getUserId() + "\t" + score.getJobId() + "\t" + score.getPrediction());
				writer_rating.println(score.getUserId() + "\t" + score.getJobId() + "\t" + score.getRating());
			}
			writer_rating.close();
			writer_prediction.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
