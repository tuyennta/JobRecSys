package recsys.datapreparer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import dto.ScoreDTO;

public class CollaborativeFilteringDataPreparer extends DataPreparer {

	List<Integer> listUserIds;

	public CollaborativeFilteringDataPreparer(String dir) {
		super(dir);		
	}

	public List<Integer> getListUserId() {
		listUserIds = new ArrayList<Integer>();
		dataReader.open(DataSetType.Score);
		ScoreDTO score_dto = null;
		while ((score_dto = dataReader.nextScore()) != null) {
			int userId = score_dto.getUserId();
			if (!isOverlap(userId)) {
				listUserIds.add(userId);
			}
		}
		listUserIds.sort(Comparator.comparing(Integer::intValue));
		return listUserIds;
	}

	private boolean isOverlap(int userId) {
		for (Integer i : listUserIds) {
			if (userId == i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * split dataset by proportion of test set
	 * @param proportionOfTest
	 * @param outputDir
	 */
	public void splitDataSet(int proportionOfTest, String outputDir) {

		dataReader.open(DataSetType.Score);
		List<ScoreDTO> scoreDataSet = getAllScores();
		List<ScoreDTO> scoreTestingSet = new ArrayList<ScoreDTO>();
		List<ScoreDTO> scoreTrainingSet = new ArrayList<ScoreDTO>();
		int fullSize = scoreDataSet.size();
		int testingSize = 0;
		if (proportionOfTest * fullSize % 100 > 5)
			testingSize = (int) (proportionOfTest * fullSize / 100 + 1);
		else
			testingSize = (int) (proportionOfTest * fullSize / 100);

		for (int i = 0; i < testingSize; i++) {			
			ScoreDTO dto = getAnyScore(fullSize, scoreDataSet);
			while (scoreTestingSet.contains(dto)) {
				dto = getAnyScore(fullSize, scoreDataSet);
			}
			scoreTestingSet.add(dto);
		}
		if (scoreDataSet.removeAll(scoreTestingSet)) {
			scoreTrainingSet = scoreDataSet;
		}

		writeScore(outputDir + "training\\", "Score.txt", scoreTrainingSet);
		writeScore(outputDir + "testing\\", "Score.txt", scoreTestingSet);
	}
	
	private ScoreDTO getAnyScore(int maxRange, List<ScoreDTO> fullSet) {
		int index = new Random().nextInt(maxRange);
		return fullSet.get(index);
	}
	
	/**
	 * split dataset by number of fold
	 * @param foldIndex
	 * @param totalFold
	 * @param inputDir
	 * @param evalDir
	 */
	public void splitDataSet(int foldIndex, int totalFold, String inputDir, String evalDir){
		dataReader.setSource(inputDir);
		dataReader.open(DataSetType.Score);
		List<ScoreDTO> scoreDataSet = getAllScores();
		List<ScoreDTO> scoreTestingSet = new ArrayList<ScoreDTO>();
		List<ScoreDTO> scoreTrainingSet = new ArrayList<ScoreDTO>();
		
		int foldSize = scoreDataSet.size()/totalFold;
		int startIndex = foldIndex*foldSize;
		int endIndex = startIndex + foldSize;
		for (int i = startIndex; i < endIndex; i++) {			
			ScoreDTO dto = getAnyScore(scoreDataSet.size(), scoreDataSet);
			while (scoreTestingSet.contains(dto)) {
				dto = getAnyScore(scoreDataSet.size(), scoreDataSet);
			}
			scoreTestingSet.add(dto);
		}
		if (scoreDataSet.removeAll(scoreTestingSet)) {
			scoreTrainingSet = scoreDataSet;
		}
		
		writeScore(evalDir + "training\\", "Score.txt", scoreTrainingSet);
		writeScore(evalDir + "testing\\", "Score.txt", scoreTestingSet);
	}
	
	public void copyFileTo(String input, String output){		
		try {
			File srcFile = new File(input + "Score.txt");
			File destFile = new File(output + "Score.txt");
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	private void writeScore(String destination, String fileName, List<ScoreDTO> dataSet) {
		FileWriter fwr;
		try {
			File out = new File(destination);
			if (!out.exists()) {
				out.mkdirs();
			}
			File fileOut = new File(out.getAbsolutePath() + File.separator + fileName);
			PrintWriter pw = new PrintWriter(fileOut);
			pw.print("");
			pw.close();
			fwr = new FileWriter(fileOut, true);
			fwr.write("");
			BufferedWriter wr = new BufferedWriter(fwr);

			for (ScoreDTO dto : dataSet) {
				wr.write(dto.getUserId() + "\t" + dto.getJobId() + "\t" + dto.getScore());
				wr.newLine();
			}

			wr.close();
			fwr.close();
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
	}
}
