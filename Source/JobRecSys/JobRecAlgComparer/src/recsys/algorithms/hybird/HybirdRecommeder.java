package recsys.algorithms.hybird;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;

import dto.CvDTO;
import dto.ScoreDTO;
import recsys.algorithms.RecommendationAlgorithm;
import recsys.datapreparer.DataSetReader;
import recsys.datapreparer.DataSetType;
import recsys.datapreparer.HybirdRecommenderDataPreparer;

public class HybirdRecommeder extends RecommendationAlgorithm {

	

	public HybirdRecommeder() {
		super();
		dataPreparation = null;
	}

	@Override
	public void init() {
		dataPreparation = new HybirdRecommenderDataPreparer(inputDirectory);
		
		super.init();
	}

	private boolean runCBIndexData()
	{
		try {
			System.out.println("Create data index");
			dataPreparation.createCBDataIndex();
			File f = new File(outputDirectory + "HYBIRD_REC.txt");
			if(f.exists())
			{
				f.delete();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Create data index fail");
			return false;
		}//Index du lieu
		return true;
		
	}
	
	private void runCF()
	{		
		//chay CF
		collaborativeFiltering(this.inputDirectory, this.outputDirectory);
	}
	
	private void runHyBird()
	{		
		try {			
			// create index reader
			System.out.println("Read index data");
			File index_data = new File(inputDirectory + "INDEX_DATA");
			Directory dir = FSDirectory.open(index_data);
			IndexReader r = DirectoryReader.open(dir);
			IndexSearcher iSeach = new IndexSearcher(r);
			iSeach.setSimilarity(new DefaultSimilarity());
			System.out.println("Create query builder");
			// TF-IDF calculate
			QueryBuilder queryBuilder = new QueryBuilder(new StandardAnalyzer(Version.LUCENE_45));
			System.out.println("Start recommending");			
			//read CF result
			ArrayList<ScoreDTO> listScore = new ArrayList<ScoreDTO>();															
			DataSetReader cf_datareader = new DataSetReader();
			cf_datareader.openFile(outputDirectory + "/CF_REC_ITEMS.txt");			
			ScoreDTO cf_score = null;		
			while((cf_score = cf_datareader.nextScore()) != null)
			{
				listScore.add(cf_score)	;		
			}
			cf_datareader.close();
			//read user profile
			DataSetReader reader = new DataSetReader(inputDirectory);			
			reader.open(DataSetType.Cv);
			CvDTO cv = new CvDTO();

			int numdoc = r.numDocs();

			while ((cv = reader.nextCv()) != null) {
				/////////////////////////////
				System.out.println("Start recommeding for user " + cv.getAccountId());
				float min = Float.MAX_VALUE;
				float max = Float.MIN_VALUE;
				float[] tf_idf = new float[numdoc];
				for (int i = 0; i < numdoc; i++) {					
					float score = 0.0f;											
					System.out.println("Calculate match score for user " + cv.getAccountId() + " and doc " + i + " --- JobId: " + iSeach.doc(i).get("JobId").trim());
					
					try {
//						Query query = queryBuilder.createBooleanQuery("JobName", cv.getCvName());
//						Explanation explain = iSeach.explain(query, i);
//						score += explain.getValue() ;
						
						Query query; //= queryBuilder.createBooleanQuery("JobName", cv.getCvName());
						Explanation explain;// = iSeach.explain(query, i);
						//score += explain.getValue() ;

						query = queryBuilder.createBooleanQuery("Location", cv.getAddress());
						explain = iSeach.explain(query, i);
						score += explain.getValue() ;

						query = queryBuilder.createBooleanQuery("Salary", cv.getExpectedSalary());
						explain = iSeach.explain(query, i);
						score += explain.getValue();

						query = queryBuilder.createBooleanQuery("Category", cv.getCategory());
						explain = iSeach.explain(query, i);
						score += explain.getValue() ;

						query = queryBuilder.createBooleanQuery("Requirement",
								cv.getSkill() + " " + cv.getEducation() + "" + cv.getLanguage());
						explain = iSeach.explain(query, i);
						score += explain.getValue() ;

						query = queryBuilder.createBooleanQuery("Description",
								cv.getSkill() + " " + cv.getEducation() + "" + cv.getLanguage());
						explain = iSeach.explain(query, i);
						score += explain.getValue() ;

						query = queryBuilder.createBooleanQuery("Tags", cv.getSkill() + "" + cv.getObjective());
						explain = iSeach.explain(query, i);
						score += explain.getValue() ;												
						score /= 6.0f;
						System.out.println("score for user " + cv.getAccountId() + " and doc " + i + " is " + score);
						if (score > max) {
							max = score;
						}
						if (score < min) {
							min = score;
						}
						tf_idf[i] = score;
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
						score = 0;
					}
				}
				FileWriter fwr = new FileWriter(new File(outputDirectory + "HIBIRD_REC.txt"), true);
				BufferedWriter wr = new BufferedWriter(fwr);
				System.out.println("start writing data");
				try {
					float based = max - min;
					if (based == 0.0f)
						continue;
					for (int i = 0; i < numdoc; i++) {
						// normalize to 0 - 1
						tf_idf[i] = ((tf_idf[i] - min)) / based;
						// normalize to 1 - 5
						tf_idf[i] = (tf_idf[i] * 4) + 1;						
						for (ScoreDTO sc : listScore) {								
							if (sc.getUserId() == cv.getAccountId()
									&& (Integer.parseInt( iSeach.doc(i).get("JobId").trim()) == (sc.getJobId()))) {							
								wr.write(cv.getAccountId() + "\t" + iSeach.doc(i).get("JobId") + "\t" + (tf_idf[i] + sc.getScore()) * 0.5f);
								System.out.println(
										"Hybird: Profile " + cv.getAccountId() + ", Job " + iSeach.doc(i).get("JobId") + ", Score=" + (tf_idf[i] + sc.getScore()) * 0.5f);
								System.out.println("CF = " + sc.getScore() + ", CB = " + tf_idf[i]);
								wr.newLine();
							}
						}						
					}
				} catch (Exception exx) {
				}
				wr.close();
			}
			reader.close();
			System.out.println("Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	HybirdRecommenderDataPreparer dataPreparation;
	//ham CF
	private void collaborativeFiltering(String input, String output) {
//		CollaborativeFiltering cf = new CollaborativeFiltering(output
//				+ "cf\\training\\", output + "cf\\result\\", output
//				+ "cf\\testing\\", useConfig);
//		cf.recommend();
	}
	
	public void hibridRecommend() {
		if (dataPreparation == null) {
			System.out.println("Please run init method first!");
			return;
		}		
		runCF();
		boolean ok = runCBIndexData();
		if(!ok) return;
		//chạy Hibrid
		runHyBird();
	}

}
