package recsys.algorithms.contentBased;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;


import vn.hus.nlp.tokenizer.VietTokenizer;

public class MemoryDocumentProcessor {

	public MemoryDocumentProcessor() {
		strip_accent = new StripAccent();
	}

	// bien
	private VietTokenizer vietTokenizer = new VietTokenizer();
	private List<String> allTerms = new ArrayList<String>();
	private double[] idf_all_term = null;
	private List<String[]> documentSet = new ArrayList<String[]>();
	private HashMap<String, String[]> items = new HashMap<String, String[]>();
	private HashMap<String, String[]> users = new HashMap<String, String[]>();
	// private HashMap<String, double[]> itemsTermVector = new HashMap<String,
	// double[]>();
	private HashMap<String, double[]> usersTermVector = new HashMap<String, double[]>();
	private StripAccent strip_accent = new StripAccent();
	private TfIdf tfidfCal = new TfIdf();

	///////////////////////////////////////////////////
	// hàm

	public void createTokenizer() {
		this.vietTokenizer = new VietTokenizer();
	}

	private double[] addVectorN(double[] v1, double v2[]) {
		double[] v3 = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			v3[i] = v1[i] + v2[i];
		}
		return v3;
	}

	private String[] extractWords(String input) throws Exception {
		class ThreadTokenizer extends Thread {
			public String getDocument() {
				return document;
			}

			public boolean done = false;

			public void setDocument(String document) {
				this.document = document;
			}

			private String document = null;

			@Override
			public void run() {
				if (document == null)
					return;
				String[] rs = vietTokenizer.tokenize(document);
				String new_doc = "";
				for (String s : rs) {
					new_doc += s + "";
				}
				document = new_doc;
				done = true;
			}

		}
		String origin = input;

		long start = System.currentTimeMillis();
		input = StringUtils.replace(input, "c#", "csharp");
		input = StringUtils.replace(input, "c++", "cplus");
		input = StringUtils.replace(input, "c+", "cplus");
		input = StringUtils.replace(input, "asp.net", "aspdotnet");
		ThreadTokenizer token = new ThreadTokenizer();
		try {
			token.setDocument(input);
			token.start();
			token.join(500);
			if (token.isAlive()) {
				token.interrupt();
				token.stop();
			}
			if (!token.done) {
				System.err.print("time out \n");
				throw new Exception();
			}
			input = token.getDocument();
			token = null;
		} catch (Exception e) {
			input = null;
		}
		if (input == null)
			throw new Exception();
		input = input.toLowerCase();
		input = strip_accent.removeStopWord(input);
		while (input.indexOf("  ") != -1) {
			input = StringUtils.replace(input, "  ", " ");
		}
		input = strip_accent.removeComma(input);
		long time = System.currentTimeMillis() - start;
		System.out.print(" length: " + origin.length() + " took: " + (time) + "\n");
		return StringUtils.split(input, ' ');
	}

	public String[] addItemContent(String itemId, String itemContent) {
		System.out.print("Extract words for item : " + itemId +"\n");
		ArrayList<String> filter_copus = new ArrayList<String>();
		try {
			String[] copus = extractWords(itemContent.toString());
			for (int i = 0; i < copus.length; i++) {
				String word = copus[i].trim();
				if (word.length() < 2 || isNumber(word) || word.length() > 20)
					continue;
				filter_copus.add(word);
				if (!allTerms.contains(word)) { // avoid duplicate entry
					allTerms.add(word);
				}
			}
			copus = filter_copus.toArray(new String[filter_copus.size()]);
			items.put(itemId, copus);
			documentSet.add(copus);
			return copus;
		} catch (Exception e) {
			e = null;
			return null;
		}
	}

	/**
	 * Build user pro
	 * 
	 * @param userId
	 * @param userProfile
	 */
	public void addUserProfile(String userId, String userProfile) {
		System.out.print("Build user profile : " + userId +"\n");
		try {
			ArrayList<String> filter_copus = new ArrayList<String>();
			String[] copus = extractWords(userProfile.toString());
			for (int i = 0; i < copus.length; i++) {
				String word = copus[i].trim();
				if (word.length() < 2 || isNumber(word) || word.length() > 15)
					continue;
				filter_copus.add(word);
				if (!allTerms.contains(word)) { // avoid duplicate entry
					allTerms.add(word);
				}
			}
			copus = filter_copus.toArray(new String[filter_copus.size()]);
			users.put(userId, copus);
			documentSet.add(copus);

		} catch (Exception e) {
			e = null;
		}
	}

	private boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void calculateIdfForAllTerm() throws IOException {
		
		int size = allTerms.size();
		int i = 0;
		idf_all_term = new double[size];
		FileWriter out = new FileWriter("idf.txt");
		for (String term : allTerms) {
			idf_all_term[i++] = tfidfCal.idfCalculator(documentSet, term);
			System.out.println(
					"Calcualated idf for term " + term + " remain: " + (size--) + " val=" + idf_all_term[i - 1]);
			out.write(term+"\t" + idf_all_term[i - 1] + "\r\n");			
		}
		out.close();
		

	}

	/**
	 * Tính toán termvector theo TF-IDF Score.
	 */

	public void tfIdfCalculatorForUserNormal() {
		double tf = 0; // term frequency
		double idf = 0; // inverse document frequency
		int dimension = allTerms.size();
		// calcualate user tf-idf vector
		Set<String> keyset = users.keySet();
		for (String key : keyset) {
			double[] tfidfvectors = new double[dimension];
			int count = 0;
			String[] content = users.get(key);
			for (String terms : allTerms) {
				tf = tfidfCal.tfCalculator(content, terms);
				idf = this.idf_all_term[count];
				tfidfvectors[count] = tf * idf;
				count++;
			}
			usersTermVector.put(key, tfidfvectors); // storing document vectors;
			System.out.println("Calcualte tf-idf vector for user: " + key);
		}
	}

	public void addUserPreference(String user, String item) {
		double tf = 0;
		double idf = 0;
		double tfidf = 0;
		int dimension = allTerms.size();
		double[] tfidfvectors = new double[dimension];
		long start = System.currentTimeMillis();
		int count = 0;
		String[] content = items.get(item);
		if (content == null)
			return;
		for (String terms : allTerms) {
			tfidf = 0;
			tf = tfidfCal.tfCalculator(content, terms);
			idf = this.idf_all_term[count];
			tfidf = tf * idf;
			tfidfvectors[count] = tfidf;
			count++;
		}
		double[] v2 = usersTermVector.get(user.trim());
		usersTermVector.remove(user.trim());
		usersTermVector.put(user, addVectorN(v2, tfidfvectors));
		System.out.println(
				"Add user " + user + " preference job : " + item + " Time: " + ((System.currentTimeMillis() - start)));
	}

	public double[] getTfIdfVectorDoubleNormalize(String itemid) {
		double tf = 0;
		double idf = 0;
		int dimension = allTerms.size();
		double[] tfidfvectors = new double[dimension];
		double[] tfvectors = new double[dimension];
		double[] idfvectors = new double[dimension];
		double max_tf = 0.0f;
		int count = 0;
		String[] content = items.get(itemid);
		for (String terms : allTerms) {
			tf = tfidfCal.tfCalculator(content, terms);
			// System.out.println(itemid + "-" + terms + ": " + tf);
			if (tf > max_tf)
				max_tf = tf;
			idf = this.idf_all_term[count];
			tfvectors[count] = tf;
			idfvectors[count] = idf;
			count++;
		}
		for (int i = 0; i < dimension; i++) {
			tfidfvectors[i] = (tfvectors[i] / max_tf) * idfvectors[i];
		}
		return tfidfvectors;
	}

	public double[] getTfIdfVector(String itemid) {
		double tf = 0;
		double idf = 0;
		int dimension = allTerms.size();
		double[] tfidfvectors = new double[dimension];
		int count = 0;
		String[] content = items.get(itemid);
		for (String terms : allTerms) {
			tf = tfidfCal.tfCalculator(content, terms);
			// System.out.println(itemid + "-" + terms + ": " + tf);
			idf = this.idf_all_term[count];
			tfidfvectors[count] = tf * idf;
			count++;
		}
		return tfidfvectors;
	}

	/**
	 * Có thể là hàm recommend.
	 */
	public void getCosineSimilarity() {
		CosineSimilarity sim = new CosineSimilarity();
		Set<String> userKey = usersTermVector.keySet();
		Set<String> itemKey = items.keySet();
		try {
			System.out.println("start writing data");
			for (String userId : userKey) {
				double[] userVector = this.usersTermVector.get(userId);
				for (String itemId : itemKey) {

					double[] itemVector = getTfIdfVector(itemId);
					double rec = sim.cosineSimilarity(userVector, itemVector);
					rec = 1.0d + 4.0d * rec;
					System.out.println(itemId + "  " + userId + " = " + rec);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getJaccardSimilarity(String output) {
		CosineSimilarity sim = new CosineSimilarity();
		Set<String> userKey = usersTermVector.keySet();
		Set<String> itemKey = items.keySet();
		FileWriter fwr;
		try {
			File out = new File(output);
			if (!out.exists()) {
				out.mkdirs();
			}
			File fileOut = new File(out.getAbsolutePath() + File.separator + "Score.txt");
			if (!fileOut.exists()) {
				fileOut.createNewFile();
			}
			fwr = new FileWriter(fileOut, true);
			BufferedWriter wr = new BufferedWriter(fwr);
			System.out.println("start writing data");

			for (String userId : userKey) {
				double[] userVector = this.usersTermVector.get(userId);
				for (String itemId : itemKey) {

					double[] itemVector = getTfIdfVector(itemId);
					double rec = sim.jaccardSimilarity(userVector, itemVector);
					rec = 1.0d + 4.0d * rec;
					System.out.println("Jaccard between " + userId + " and " + itemId + "  =  " + rec);
					wr.write(userId + "\t" + itemId + "\t" + rec);
					wr.newLine();
				}
			}
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getCosineSimilarity(String output) {
		CosineSimilarity sim = new CosineSimilarity();
		Set<String> userKey = usersTermVector.keySet();
		Set<String> itemKey = items.keySet();
		FileWriter fwr;
		try {
			File out = new File(output);
			if (!out.exists()) {
				out.mkdirs();
			}
			File fileOut = new File(out.getAbsolutePath() + File.separator + "Score.txt");
			if (!fileOut.exists()) {
				fileOut.createNewFile();
			}
			fwr = new FileWriter(fileOut, true);
			BufferedWriter wr = new BufferedWriter(fwr);
			System.out.println("start writing data");

			for (String userId : userKey) {
				double[] userVector = this.usersTermVector.get(userId);				
				for (String itemId : itemKey) {

					double[] itemVector = getTfIdfVector(itemId);
					double rec = sim.cosineSimilarity(userVector, itemVector) * 4.0d +1.0d;					
					System.out.println("between " + userId + " and " + itemId + "  =  " + rec);
					wr.write(userId + "\t" + itemId + "\t" + rec);
					wr.newLine();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private String arrayToString(double[] arr) {
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		for (double i : arr) {
			buf.append(i + "\t");
		}
		buf.append("]");
		return buf.toString();
	}

	public void print() {
		Set<String> userKey = usersTermVector.keySet();
		Set<String> itemKey = items.keySet();
		for (String i : userKey) {
			System.out.println(i + ": " + arrayToString(usersTermVector.get(i)));
		}

		for (String i : itemKey) {
			System.out.println(i + ": " + arrayToString(getTfIdfVector(i)));
		}
	}
}