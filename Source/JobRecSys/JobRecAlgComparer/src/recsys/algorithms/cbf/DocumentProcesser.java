package recsys.algorithms.cbf;

import java.io.File;
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

	
	public Set<String> getListUsers()
	{
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

	public void addJob(String jobId, String content) {
		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
		try {
			IndexWriter writer = new IndexWriter(_directory, iwc);
			addDocument(writer, content);
			// job.add(currentIndex);
			jobs.put(jobId, currentIndex);
			currentIndex += 1;
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addCv(String userId, String content) {

		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
		try {
			IndexWriter writer = new IndexWriter(_directory, iwc);
			addDocument(writer, content);
			users.put(userId, currentIndex);
			currentIndex += 1;
			writer.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	
	public void recommend(String userid, int topN, String path) throws IOException {
		
		System.out.println("Reccomend for userid " + userid);
		double[] TopNscore = new double[topN];
		String[] TopNjob = new String[topN];
		double max_score = 0.0d;
		for(int i = 0 ; i < topN; i++)
		{
			TopNjob[i] = "";
			TopNscore[i] = 0;
		}
		
		int userDoc = users.get(userid);
		for (String jobid : jobs.keySet()) {
			int itemDoc = jobs.get(jobid);
			try {
				double val = getCosineSimilarityWithUserRating(userDoc, rating.get(userid), itemDoc);
				for(int i = 0 ; i < topN; i++)
				{
					if(val > TopNscore[i])
					{
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
		FileWriter fw = new FileWriter(path+"Score.txt",true);
		System.out.println("Start writing result!");
		for(int i = 0 ; i < topN; i++)
		{
			String rs = userid +"\t" + TopNjob[i] + "\t" + (1.0d + (TopNscore[i] * 4.0d / max_score));
			System.out.println(rs);
			fw.append(rs + "\r\n");
		}
		fw.close();
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
