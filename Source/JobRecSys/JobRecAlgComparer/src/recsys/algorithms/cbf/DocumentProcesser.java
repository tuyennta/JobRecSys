package recsys.algorithms.cbf;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class DocumentProcesser extends DocumentSimilarityTFIDF {
	private static Logger log = Logger.getLogger("Author: Luan");

	public DocumentProcesser() {
		_directory = new RAMDirectory();

	}

	private HashMap<String, ArrayList<Integer>> rating = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, Integer> users = new HashMap<String, Integer>();
	private HashMap<String, Integer> jobs = new HashMap<String, Integer>();

	public HashMap<String, Integer> getJobs() {
		return jobs;
	}

	public void setJobs(HashMap<String, Integer> jobs) {
		this.jobs = jobs;
	}

	private int currentIndex = 0;

	public Set<String> getListUsers() {
		return users.keySet();
	}

	public void addRating(String user, String job) {
		if (!jobs.containsKey(job)) {
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

	private int countTask = 0;

	public HashMap<String, CBTopNJobs> topNRecommendResult = new HashMap<String, CBTopNJobs>();

	public HashMap<String, CbRecommededList> recommendResult = new HashMap<String, CbRecommededList>();
	
	public void recommendForTopN(int topN) {

		Set<String> jobIds = jobs.keySet();
		double size = users.keySet().size() * jobIds.size();
		ArrayList<RealVector> userVectors = new ArrayList<RealVector>();
		class UserTask implements Runnable {

			public String userid = "";
			public String jobId = "";
			public RealVector userV;
			public RealVector jobV;
			public int threadId = 0;

			public UserTask() {
			}

			@Override
			public void run() {
				try {
					try {
						double val = getCosineSimilarityWithUserRating(userV, jobV);
						System.out.println("Thread  " + threadId + "-- Job " + userid + " and User " + jobId + " " + val
								+ " \t" + (countTask * 100.0d / size) + " %");
						topNRecommendResult.get(userid).add(jobId, val);
						countTask++;
					} catch (IOException e) {

					}
				} catch (Exception e) {
					log.error(e);
				}
			}

		}

		for (String j : users.keySet()) {
			CBTopNJobs cbTopN = new CBTopNJobs(topN);
			topNRecommendResult.put(j, cbTopN);
			try {
				Map<String, Double> v_user = getWieghts(reader, users.get(j));

				RealVector v = toRealVector(v_user);
				ArrayList<Integer> arrayList = rating.get(j);
				if (arrayList != null) {
					for (int i : arrayList) {
						Map<String, Double> p = getWieghts(reader, i);
						RealVector vlike = toRealVector(p);
						v = v.add(vlike);
					}
				}
				userVectors.add(v);
			} catch (IOException e) {

			}
		}
		Runtime runtime = Runtime.getRuntime();
		int numOfProcessors = runtime.availableProcessors();
		for (String i : jobIds) {
			try {
				Map<String, Double> v_job = getWieghts(reader, jobs.get(i));
				RealVector rvJob = toRealVector(v_job);
				int vi_user = 0;

				ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
				for (String j : users.keySet()) {
					UserTask rec = new UserTask();
					rec.jobId = i;
					rec.userid = j;
					rec.jobV = rvJob;
					rec.threadId = vi_user;
					rec.userV = userVectors.get(vi_user++);
					executor.submit(rec);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
				vi_user = 0;
			} catch (IOException e) {

			}
		}

	}
	
	
	public HashMap<String, CbRecommededList> getRecommendScoreForSpecificJobs(HashMap<String,List<RecommendedItem>> cfResult) {
		System.out.println("Start CB");
		Set<String> jobKeySet = jobs.keySet();
		double size = users.keySet().size() * jobKeySet.size();
		ArrayList<RealVector> userRealVectors = new ArrayList<RealVector>();
		
		class UserTaskRec implements Runnable {

			public String userid = "";
			public String jobId = "";
			public RealVector userV;
			public RealVector jobV;			

			public UserTaskRec() {
			}

			@Override
			public void run() {
				try {
					try {
						double val = getCosineSimilarityWithUserRating(userV, jobV);
						CbRecommededList rs = recommendResult.get(userid);
						 rs.update(jobId, val);
						 recommendResult.put(userid, rs);
						countTask++;
					} catch (IOException e) {

					}
				} catch (Exception e) {
					log.error(e);
				}
			}

		}
		//thiet lap cho CF
		System.out.println("Prepare CF data");

		for (String j : users.keySet()) {			
			CbRecommededList cbRec = new CbRecommededList();
			for (String i : jobKeySet) {				
				cbRec.add(i, 0.0d);
			}
			List<RecommendedItem> rec = cfResult.get(j);
			if(rec != null)
			{
				for(RecommendedItem r : rec)
				{
					cbRec.update(r.getItemID() + "", r.getValue());
					cbRec.max_score = 0;
				}
			}
			recommendResult.put(j, cbRec);
		}
		//tao user vector
		System.out.println("Prepare build user's profile vector");
		for (String j : users.keySet()) {

			try {
				Map<String, Double> v_user = getWieghts(reader, users.get(j));

				RealVector v = toRealVector(v_user);
				ArrayList<Integer> arrayList = rating.get(j);
				if (arrayList != null) {
					for (int i : arrayList) {
						Map<String, Double> p = getWieghts(reader, i);
						RealVector vlike = toRealVector(p);
						v = v.add(vlike);
					}
				}
				userRealVectors.add(v);
			} catch (IOException e) {

			}
		}
		//get CPU core
		System.out.println("Run cb");
		Runtime runtime = Runtime.getRuntime();
		int numOfProcessors = runtime.availableProcessors();
		for (String i : jobKeySet) {			
			try {
				Map<String, Double> v_job = getWieghts(reader, jobs.get(i));
				RealVector rvJob = toRealVector(v_job);
				int vi_user = 0;

				ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
				for (String j : users.keySet()) {
					UserTaskRec rec = new UserTaskRec();
					rec.jobId = i;
					rec.userid = j;
					rec.jobV = rvJob;					
					rec.userV = userRealVectors.get(vi_user++);
					executor.submit(rec);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
				vi_user = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Matching for jobid " + i + " \t" + (countTask * 100.0d / size) + " %");
		}
		System.out.println("done cb " + recommendResult.size());		 
		return  this.recommendResult;
	}

	public void buildTermCopus() {
		for (String i : users.keySet()) {
			addTermModel(users.get(i));			
		}
		for (String i : jobs.keySet()) {
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

	public void writeFile(String path, HashMap<String, CBTopNJobs> rss) {
		try {
			FileWriter fw = new FileWriter(path + "Score.txt", true);
			System.out.println("Start writing result!");
			for (String i : rss.keySet()) {
				double max = rss.get(i).max_score;
				int topN = rss.get(i).topN;
				double[] score = rss.get(i).TopNscore;
				String[] job = rss.get(i).TopNjob;
				for (int k = 0; k < topN; k++) {
					fw.append(i + "\t" + job[k] + "\t" + (1.0d + ((score[k] / max) * 4.0d)) + "\r\n");
				}

			}
			fw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	

}
