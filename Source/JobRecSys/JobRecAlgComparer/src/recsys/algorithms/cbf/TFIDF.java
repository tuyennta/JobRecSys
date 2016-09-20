package recsys.algorithms.cbf;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author TinHuynh
 */
public class TFIDF {

	public static HashMap<Integer, Integer> _AuthorInstanceHM = new HashMap<>();
	public static HashMap<Integer, Integer> _InstanceAuthorHM = new HashMap<>();
	public static HashMap<Integer, String> _InstancePublicationHM = new HashMap<>();
	private static HashMap<Integer, HashMap<Integer, Float>> _tfidfHM = new HashMap<>();
	private CollectionDocument indexAllDocument;

	private void runTF(int inputAuthorID) {
		try {
			int currentAuthorID;
			System.out.println("CURRENT INSTANCE IS:" + inputAuthorID);
			int instanceID = getInstanceFromAuthorID(inputAuthorID);
			HashMap<Integer, Float> similarityHM = new HashMap<Integer, Float>();
			for (int otherInstanceID = 0; otherInstanceID < _InstancePublicationHM.size(); otherInstanceID++) {
				if (instanceID != otherInstanceID) {
					currentAuthorID = getAuthorIDFromInstanceID(otherInstanceID);
					SimilarityTF similarityUsingTF = new SimilarityTF();
					float simValue = (float) similarityUsingTF.getCosineSimilarityWhenIndexAllDocument(
							indexAllDocument.getTermWithAuthorID(instanceID),
							indexAllDocument.getTermWithAuthorID(otherInstanceID));
					similarityHM.put(currentAuthorID, simValue);
				}
			}
			_tfidfHM.put(inputAuthorID, similarityHM);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public HashMap<Integer, HashMap<Integer, Float>> process(String inputFile, HashMap<Integer, String> listAuthorID) {
		System.out.println("START PROCESSING TFIDF");
		try {
			//load bai bao
			loadInstancePublication(inputFile);
			String pathFile = (new File(inputFile)).getParent();
			//
			loadMappingInstanceIDAuthorID(pathFile + "/CRS-AuthorIDAndInstance.txt");
			indexAllDocument = new CollectionDocument();
			indexAllDocument.indexAllDocument(_InstancePublicationHM);
			indexAllDocument.openReader();

			// Runtime runtime = Runtime.getRuntime();
			// int numOfProcessors = runtime.availableProcessors();
			// ExecutorService executor =
			// Executors.newFixedThreadPool(numOfProcessors - 1);
			for (final int authorId : listAuthorID.keySet()) {
				// executor.submit(new Runnable() {
				// @Override
				// public void run() {
				runTF(authorId);
				// }
				// });
			}

			// executor.shutdown();
			// while (!executor.isTerminated()) {
			// }
			indexAllDocument.closeReader();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("FINISH PROCESSING TFIDF");
		return _tfidfHM;
	}

	private int getInstanceFromAuthorID(int authorID) {
		return _AuthorInstanceHM.get(authorID);
	}

	private int getAuthorIDFromInstanceID(int instanceID) {
		return _InstanceAuthorHM.get(instanceID);
	}

	public String getPublicationFromAuthorID(int authorID) {
		int instanceID = getInstanceFromAuthorID(authorID);
		return (_InstancePublicationHM.get(instanceID));
	}

	private void loadMappingInstanceIDAuthorID(String mapFile) {
		try {
			FileInputStream fis = new FileInputStream(mapFile);
			Reader reader = new InputStreamReader(fis, "UTF8");
			BufferedReader bufferReader = new BufferedReader(reader);
			bufferReader.readLine(); // skip the header line
			String line = null;
			String[] tokens;
			while ((line = bufferReader.readLine()) != null) {
				tokens = line.split("\t");
				if (tokens.length != 2) {
					continue;
				}				
				int authorID = Integer.parseInt(tokens[0]);
				int instanceID = Integer.parseInt(tokens[1]);
				_AuthorInstanceHM.put(authorID, instanceID);
				_InstanceAuthorHM.put(instanceID, authorID);
			}
			bufferReader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void loadInstancePublication(String inputFile) {
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			Reader reader = new InputStreamReader(fis, "UTF8");
			BufferedReader bufferReader = new BufferedReader(reader);
			String line = null;
			int instanceID = 0;
			while ((line = bufferReader.readLine()) != null) {
				_InstancePublicationHM.put(instanceID, line);
				instanceID++;
			}
			bufferReader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}