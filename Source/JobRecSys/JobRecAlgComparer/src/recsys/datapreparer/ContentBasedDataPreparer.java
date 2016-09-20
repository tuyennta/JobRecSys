package recsys.datapreparer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import dto.CvDTO;
import dto.JobDTO;
import dto.ScoreDTO;

public class ContentBasedDataPreparer extends DataPreparer {
	ArrayList<ScoreDTO> listScore = new ArrayList<ScoreDTO>();

	public ContentBasedDataPreparer(String dir) {
		super(dir);
	}

	public ArrayList<ScoreDTO> getListScore() {
		return listScore;
	}

	public void init() {
		System.out.println("Preparing to create index data!");
		String dir = this.dataReader.getSource();
		File file = new File(dir + "INDEX_DATA");
		if (file.exists()) {
			file.delete();
		}
		file.mkdir();

		this.dataReader.open(DataSetType.Score); // <~ check data type
		ScoreDTO score_dto = null;
		while ((score_dto = dataReader.nextScore()) != null) {
			listScore.add(score_dto);
		}
		dataReader.close();

		System.out.println("Done!");
	}

	public void createDataIndex(int accountId) throws IOException {
		init();
		System.out.println("Starting index data!");
		this.dataReader.open(DataSetType.Job);
		JobDTO job = null;
		String str_dir = this.dataReader.getSource();
		File file = new File(str_dir + "INDEX_DATA" + accountId);
		Directory dir = FSDirectory.open(file);

		@SuppressWarnings("deprecation")
		IndexWriterConfig iwr_config = new IndexWriterConfig(Version.LUCENE_45,
				new StandardAnalyzer(Version.LUCENE_45));
		iwr_config.setOpenMode(OpenMode.CREATE);
		iwr_config.setSimilarity(new DefaultSimilarity());
		IndexWriter index_wr = new IndexWriter(dir, iwr_config);

		// add rated jobs to this user's index
		while ((job = dataReader.nextJob()) != null) {
			for (ScoreDTO sc : listScore) {
				if (sc.getUserId() == accountId && sc.getJobId() == job.getJobId() && sc.getScore() >= 3) {
					String data = "";
					FieldType type = new FieldType();
					type.setStoreTermVectors(true);
					type.setStored(true);
					type.setTokenized(true);
					type.setOmitNorms(false);
					type.setStoreTermVectorPositions(true);
					type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
					type.setIndexed(true);
					Document doc = new Document();
					data = job.getJobId() + "";
					Field field = new Field("JobId", data, type);
					doc.add(field);
					data = job.getJobName();
					System.out.println("Index job name " + job.getJobName());
					field = new Field("JobName", data, type);
					doc.add(field);
					data = job.getLocation();
					field = new Field("Location", data, type);
					doc.add(field);
					data = job.getSalary();
					field = new Field("Salary", data, type);
					doc.add(field);
					data = job.getDescription();
					field = new Field("Description", data, type);
					doc.add(field);
					data = job.getRequirement();
					field = new Field("Requirement", data, type);
					doc.add(field);
					data = job.getTags();
					field = new Field("Tags", data, type);
					doc.add(field);
					data = job.getCategory();
					field = new Field("Category", data, type);
					doc.add(field);
					index_wr.addDocument(doc);
				}
			}
		}
		index_wr.close();

		this.dataReader.close();
	}

	public void splitDataSet(String evalDir) {

		dataReader.open(DataSetType.Job);
		List<JobDTO> jobDataSet = getAllJobs();
		List<JobDTO> jobTestingDataSet = new ArrayList<JobDTO>();

		dataReader.open(DataSetType.Cv);
		List<CvDTO> cvDataSet = getAllCVs();
		List<CvDTO> cvTestingDataSet = new ArrayList<CvDTO>();

		dataReader.setSource(evalDir + "testing\\");
		dataReader.open(DataSetType.Score);
		List<ScoreDTO> scoreDataSet = getAllScores();
		List<Integer> users = getAllUsers(scoreDataSet);
		List<Integer> jobs = getAllJobs(scoreDataSet);

		JobDTO removeJob = null;
		CvDTO removeCv = null;
		for (Integer jobID : jobs) {
			for (JobDTO job : jobDataSet) {
				if (jobID == job.getJobId()) {
					jobTestingDataSet.add(job);
					removeJob = job;
					break;
				}
			}
			jobDataSet.remove(removeJob);
		}
		for (Integer userID : users) {
			for (CvDTO cv : cvDataSet)
				if (userID == cv.getAccountId()) {
					cvTestingDataSet.add(cv);
					removeCv = cv;
					break;
				}
			cvDataSet.remove(removeCv);
		}

		writeJob(evalDir + "training\\", "job.txt", jobDataSet);
		writeJob(evalDir + "testing\\", "job.txt", jobTestingDataSet);
		writeCv(evalDir + "training\\", "cv.txt", cvDataSet);
		writeCv(evalDir + "testing\\", "cv.txt", cvTestingDataSet);
	}

	private List<Integer> getAllUsers(List<ScoreDTO> listScore) {
		List<Integer> users = new ArrayList<Integer>();
		for (ScoreDTO score : listScore) {
			if (!isOverlap(users, score.getUserId())) {
				users.add(score.getUserId());
			}
		}
		return users;
	}

	public void copyFileTo(String inputDir, String outputDir){
		try {
			File srcJobFile = new File(inputDir + "Job.txt");
			File destJobFile = new File(outputDir + "Job.txt");
			File srcCvFile = new File(inputDir + "Cv.txt");
			File destCvFile = new File(outputDir + "Cv.txt");
			FileUtils.copyFile(srcJobFile, destJobFile);
			FileUtils.copyFile(srcCvFile, destCvFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<Integer> getAllJobs(List<ScoreDTO> listScore) {
		List<Integer> jobs = new ArrayList<Integer>();
		for (ScoreDTO score : listScore) {
			if (!isOverlap(jobs, score.getJobId())) {
				jobs.add(score.getJobId());
			}
		}
		return jobs;
	}

	private boolean isOverlap(List<Integer> list, int id) {
		for (Integer i : list) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}

	private void writeJob(String destination, String fileName, List<JobDTO> dataSet) {
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

			for (JobDTO dto : dataSet) {
				wr.write(dto.getJobId() + "\t" + dto.getJobName() + "\t" + dto.getLocation() + "\t" + dto.getSalary()
						+ "\t" + dto.getCategory() + "\t" + dto.getRequirement() + "\t" + dto.getTags() + "\t"
						+ dto.getDescription());
				wr.newLine();
			}

			wr.close();
			fwr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeCv(String destination, String fileName, List<CvDTO> dataSet) {
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

			for (CvDTO dto : dataSet) {
				wr.write(dto.getAccountId() + "\t" + dto.getResumeId() + "\t" + dto.getUserName() + "\t"
						+ dto.getCvName() + "\t" + dto.getAddress() + "\t" + dto.getExpectedSalary() + "\t"
						+ dto.getCategory() + "\t" + dto.getEducation() + "\t" + dto.getLanguage() + "\t"
						+ dto.getSkill() + "\t" + dto.getObjective());
				wr.newLine();
			}

			wr.close();
			fwr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
