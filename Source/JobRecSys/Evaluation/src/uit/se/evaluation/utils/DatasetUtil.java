package uit.se.evaluation.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import uit.se.evaluation.dtos.ScoreDTO;

/**
 * This utility class is using for binding dataset file into list objects
 * 
 * @author tuyen
 *
 */
public class DatasetUtil {

	/**
	 * get rank list from specific location
	 * 
	 * @param source
	 * @param truthRank
	 * @return
	 */
	public static HashMap<Integer, List<ScoreDTO>> getRankList(String source, int truthRank) {
		List<ScoreDTO> scoreDTOList = new ArrayList<ScoreDTO>();
		ScoreDTO dto = null;
		DatasetReaderUtil.openFile(source);
		while ((dto = DatasetReaderUtil.nextScore()) != null) {
			// convert rating score from numeric score into boolean score

			if (dto.getScore() <= truthRank) {
				dto.setRelevant(false);
			} else {
				dto.setRelevant(true);
			}

			scoreDTOList.add(dto);
		}
		return sort(scoreDTOList);
	}
	
	public static HashMap<Integer, List<ScoreDTO>> getGroundTruth(String source, int truthRank) {
		List<ScoreDTO> scoreDTOList = new ArrayList<ScoreDTO>();
		ScoreDTO dto = null;
		DatasetReaderUtil.openFile(source);
		while ((dto = DatasetReaderUtil.nextScore()) != null) {
			// convert rating score from numeric score into boolean score
			if (dto.getScore() <= truthRank) {
				continue;
			} else {
				dto.setRelevant(true);
			}

			scoreDTOList.add(dto);
		}
		return sort(scoreDTOList);
	}

	/**
	 * sort list score by user ascending and then score descending
	 * 
	 * @param scores
	 * @return
	 */
	private static HashMap<Integer, List<ScoreDTO>> sort(List<ScoreDTO> scores) {
		HashMap<Integer, List<ScoreDTO>> results = new HashMap<>();
		if (scores.size() == 0 || scores == null) {
			return results;
		}
		scores.sort(Comparator.comparing(ScoreDTO::getUserId));
		int currentUserId = scores.get(0).getUserId();
		List<ScoreDTO> tmp = new ArrayList<>();
		for (ScoreDTO dto : scores) {
			if (dto.getUserId() == currentUserId) {
				tmp.add(dto);
			} else {
				tmp.sort(Comparator.comparing(ScoreDTO::getScore).reversed());
				List<ScoreDTO> tmp1 = new ArrayList<>();
				tmp1.addAll(tmp);
				results.put(currentUserId, tmp1);
				tmp.clear();
				currentUserId = dto.getUserId();
				tmp.add(dto);
			}
		}
		tmp.sort(Comparator.comparing(ScoreDTO::getScore).reversed());
		results.put(currentUserId, tmp);
		return results;
	}

	
}