package recsys.algorithms.cbf;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class DocumentProcesser extends DocumentSimilarityTFIDF {
	private static Logger log = Logger.getLogger("Author: Luan");
	public DocumentProcesser() {
		_directory = new RAMDirectory();

	}

	private HashMap<String, ArrayList<Integer>> rating = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, Integer> users = new HashMap<String, Integer>();
	private HashMap<String, Integer> jobs = new HashMap<String, Integer>();
	private int currentIndex = 0;

	public Set<String> getListUsers() {
		return users.keySet();
	}

	public void addRating(String user, String job) {
		if(!jobs.containsKey(job)) 
		{
			return;
		}			
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

	
	public void buildTermModel()
	{
		for(String i : users.keySet())
		{
			addTermModel(users.get(i));
		}
		for(String i : jobs.keySet())
		{
			addTermModel(jobs.get(i));
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
			log.info("Recommend for user " + userid);
			for (String jobid : jobs.keySet()) {
				int itemDoc = jobs.get(jobid);
				try {
					double val = getCosineSimilarityWithUserRating(userDoc, rating.get(userid), itemDoc);
					TopNjob[i] = jobid;
					TopNscore[i] = val;
					max_score = val > max_score ? val : max_score;
					System.out.println("Cos between " + userid + " and " + jobid + " is " + val);
					i++;
				} catch (IOException e) {
					log.error(e);
				}
			}
			log.info("write file data " + userid + " Job count "  + TopNjob.length);
			writeFile(userid, TopNjob, TopNscore, max_score, path);
			log.info("write file data is done");
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

	
	public void recommend(String userid, int topN, int threadid) throws IOException {

		System.out.println("Thread "+threadid+" Reccomend for userid " + userid);
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
				System.out.println("Thread "+threadid+" Similarity between " + userDoc + " and " + jobid + " is:" + val);
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
			log.info("open file to write: " + path + "Score.txt");
			System.out.println("Start writing result!");
			for (int i = 0; i < topJobs.length; i++) {

				String _rs = user + "\t" + topJobs[i] + "\t"
						+ (1.0d + (topScore[i] * 4.0d / max));
				System.out.println(_rs);
				fw.append(_rs + "\r\n");
			}
			log.info("close file: " + path + "Score.txt");
			fw.close();
		} catch (Exception e) {
			log.error(e);
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
