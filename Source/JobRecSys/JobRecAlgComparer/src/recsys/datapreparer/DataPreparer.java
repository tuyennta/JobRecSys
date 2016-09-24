package recsys.datapreparer;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;

import java.util.ArrayList;
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
	 * @return
	 */
	public List<ScoreDTO> getAllScores() {
		List<ScoreDTO> scoreDTOList = new ArrayList<ScoreDTO>();
		ScoreDTO dto = null;
		while ((dto = dataReader.nextScore()) != null) {
			scoreDTOList.add(dto);
		}
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
}