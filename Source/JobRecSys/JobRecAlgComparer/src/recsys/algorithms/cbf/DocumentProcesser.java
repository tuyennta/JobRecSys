package recsys.algorithms.cbf;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class DocumentProcesser extends DocumentSimilarityTFIDF {

	public DocumentProcesser() {
		_directory = new RAMDirectory();

	}

	private HashMap<String, ArrayList<Integer>> rating = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, Integer> users = new HashMap<String, Integer>();
	private HashMap<String, Integer> jobs = new HashMap<String, Integer>();
	private int currentIndex = 0;
	private Object sync = new Object();

	public Set<String> getListUsers() {
		return users.keySet();
	}

	public void addRating(String user, String job) {
		if (rating.containsKey(user)) {
			ArrayList<Integer> userLike = rating.get(user);
			userLike.add(jobs.get(job));
			rating.put(user, userLike);
		} else {
			ArrayList<Integer> userLike = new ArrayList<Integer>();
			userLike.add(jobs.get(job));
			rating.put(user, userLike);
		}
	}

	private Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
	private IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
	private IndexWriter writer;

	public boolean open() {
		try {
			writer = new IndexWriter(_directory, iwc);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (Exception e) {

		}
	}

	public void addJob(String jobId, String content) {

		try {
			addDocument(writer, content);
			// job.add(currentIndex);
			jobs.put(jobId, currentIndex);
			currentIndex += 1;
			N += 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addCv(String userId, String content) {

		try {
			addDocument(writer, content);
			users.put(userId, currentIndex);
			currentIndex += 1;
			N += 1;
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private ArrayList<CbResults> result = new ArrayList<CbResults>();

	public void recommend(String path) throws IOException {

		double[] TopNscore = new double[jobs.size()];
		String[] TopNjob = new String[jobs.size()];
		double max_score = 0.0d;
		for (int i = 0; i < jobs.size(); i++) {
			TopNjob[i] = "";
			TopNscore[i] = 0;
		}

		for (String userid : users.keySet()) {
			int userDoc = users.get(userid);
			int i = 0;
			for (String jobid : jobs.keySet()) {
				int itemDoc = jobs.get(jobid);
				try {
					double val = getCosineSimilarityWithUserRating(userDoc, rating.get(userid), itemDoc);
					TopNjob[i] = jobid;
					TopNscore[i] = val;
					max_score = val > max_score ? val : max_score;
					System.out.println("Cos between " + userid + " and " + jobid + " is " + val);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			writeFile(userid, TopNjob, TopNscore, max_score, path);
		}
	}

	public void recommend(String userid, int topN) throws IOException {

		System.out.println("Reccomend for userid " + userid);
		double[] TopNscore = new double[topN];
		String[] TopNjob = new String[topN];
		double max_score = 0.0d;
		for (int i = 0; i < topN; i++) {
			TopNjob[i] = "";
			TopNscore[i] = 0;
		}

		int userDoc = users.get(userid);
		for (String jobid : jobs.keySet()) {
			int itemDoc = jobs.get(jobid);
			try {
				double val = getCosineSimilarityWithUserRating(userDoc, rating.get(userid), itemDoc);
				System.out.println("Similarity between " + userDoc + " and " + jobid + " is:" + val);
				for (int i = 0; i < topN; i++) {
					if (val > TopNscore[i]) {
						TopNjob[i] = jobid;
						TopNscore[i] = val;
						max_score = val > max_score ? val : max_score;
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		CbResults tmp = new CbResults();
		tmp.setTopJob(TopNjob);
		tmp.setTopScore(TopNscore);
		tmp.setUserId(userid);
		tmp.setMax_score(max_score);
		result.add(tmp);
	}

	public void writeFile(String user, String[] topJobs, double[] topScore, double max, String path) {
		try {
			FileWriter fw = new FileWriter(path + "Score.txt", true);
			System.out.println("Start writing result!");
			for (int i = 0; i < topJobs.length; i++) {

				String _rs = user + "\t" + topJobs[i] + "\t"
						+ (1.0d + (topScore[i] * 4.0d / max));
				System.out.println(_rs);
				fw.append(_rs + "\r\n");
			}
			fw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void writeFile(String path) {
		try {
			FileWriter fw = new FileWriter(path + "Score.txt", true);
			System.out.println("Start writing result!");
			for (CbResults rs : result) {
				int topN = rs.getTopJob().length;
				String[] TopNjob = rs.getTopJob();
				double[] TopNscore = rs.getTopScore();
				for (int i = 0; i < topN; i++) {
					String _rs = rs.getUserId() + "\t" + TopNjob[i] + "\t"
							+ (1.0d + (TopNscore[i] * 4.0d / rs.getMax_score()));
					System.out.println(_rs);
					fw.append(_rs + "\r\n");
				}
			}
			fw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void calculateSimilarity() throws IOException {
		FileWriter fw = new FileWriter("rs.txt");
		for (String userId : users.keySet()) {
			int userDoc = users.get(userId);
			for (String jobid : jobs.keySet()) {
				int itemDoc = jobs.get(jobid);
				try {
					double val = getCosineSimilarityWithUserRating(userDoc, rating.get(userId), itemDoc);
					System.out.println("UserId: " + userId + " , JobId " + jobid + " = " + (1.0d + val * 4.0d));
					fw.write(userId + "\t" + jobid + "\t" + val + "\r\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			fw.close();
		}
	}
}
