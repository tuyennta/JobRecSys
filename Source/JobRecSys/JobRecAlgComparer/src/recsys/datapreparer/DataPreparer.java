package recsys.datapreparer;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;
import utils.MysqlDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class DataPreparer {
	protected DataSetReader dataReader = null;
	static Logger log = Logger.getLogger(DataPreparer.class.getName());

	public DataPreparer(String dir) {
		dataReader = new DataSetReader(dir);
	}

	/**
	 * get list scores
	 * 
	 * @return
	 */
	public List<ScoreDTO> getAllScores() {
		List<ScoreDTO> scoreDTOList = new ArrayList<ScoreDTO>();
		ScoreDTO dto = null;
		while ((dto = dataReader.nextScore()) != null) {
			scoreDTOList.add(dto);
		}
		Collections.shuffle(scoreDTOList);
		return scoreDTOList;
	}

	public List<CvDTO> getAllCVs() {
		List<CvDTO> cvDTOList = new ArrayList<CvDTO>();
		CvDTO dto = null;
		while ((dto = dataReader.nextCv()) != null) {
			cvDTOList.add(dto);
		}
		return cvDTOList;
	}

	public List<JobDTO> getAllJobs() {
		List<JobDTO> jobDTOList = new ArrayList<JobDTO>();
		JobDTO dto = null;
		while ((dto = dataReader.nextJob()) != null) {
			jobDTOList.add(dto);
		}
		return jobDTOList;
	}

	HashMap<Integer, List<uit.se.evaluation.dtos.ScoreDTO>> groundTruth = new HashMap<>();
	HashMap<Integer, List<uit.se.evaluation.dtos.ScoreDTO>> rankList = new HashMap<>();

	public void readEvaluationDataFromDB(String agl, int truthRank) {
		MysqlDBConnection con = new MysqlDBConnection("recsys.properties");
		String sql = "select * from rankedlist where Algorithm = '" + agl + "' order by AccountId and Prediction";
		if (con.connect()) {
			ResultSet rs = con.read(sql);
			uit.se.evaluation.dtos.ScoreDTO score = null;
			List<uit.se.evaluation.dtos.ScoreDTO> scores = new ArrayList<>();
			uit.se.evaluation.dtos.ScoreDTO prediction = null;
			List<uit.se.evaluation.dtos.ScoreDTO> ranks = new ArrayList<>();
			int lastAccount = 0;
			try {
				while (rs.next()) {
					int accountID = rs.getInt("AccountId");
					int jobID = rs.getInt("JobId");
					score = new uit.se.evaluation.dtos.ScoreDTO();
					prediction = new uit.se.evaluation.dtos.ScoreDTO();

					if (lastAccount != 0 && lastAccount != accountID) {
						List<uit.se.evaluation.dtos.ScoreDTO> r = new ArrayList<>();
						r.addAll(ranks);
						r.sort(Comparator.comparing(uit.se.evaluation.dtos.ScoreDTO::getScore).reversed());
						rankList.put(lastAccount, r);
						ranks.clear();

						if (scores.size() != 0) {
							List<uit.se.evaluation.dtos.ScoreDTO> s = new ArrayList<>();
							s.addAll(scores);
							s.sort(Comparator.comparing(uit.se.evaluation.dtos.ScoreDTO::getScore).reversed());
							groundTruth.put(lastAccount, s);
							scores.clear();
						}
					} else {
						float rating = Float.valueOf(rs.getString("Rating") != null ? rs.getString("Rating") : "0");
						if (rating >= truthRank) {
							score.setUserId(accountID);
							score.setItemId(jobID);
							score.setScore(rating);
							scores.add(score);
						}

						prediction.setUserId(accountID);
						prediction.setItemId(jobID);
						prediction.setScore(Float.valueOf(rs.getString("Prediction")));
						ranks.add(prediction);
					}
					lastAccount = accountID;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con.close();
		}
	}

	public HashMap<Integer, List<uit.se.evaluation.dtos.ScoreDTO>> getGroundTruth() {
		return groundTruth;
	}

	public HashMap<Integer, List<uit.se.evaluation.dtos.ScoreDTO>> getRankList() {
		return rankList;
	}

}