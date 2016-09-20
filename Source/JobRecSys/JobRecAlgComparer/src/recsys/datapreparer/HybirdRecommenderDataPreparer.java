package recsys.datapreparer;

import java.io.File;
import java.io.IOException;

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

import dto.JobDTO;

public class HybirdRecommenderDataPreparer extends DataPreparer {

	public HybirdRecommenderDataPreparer(String dir) {
		super(dir);
	}

	private void init() throws Exception {
		System.out.println("Preparing to create index data!");
		String dir = this.dataReader.getSource();
		File file = new File(dir + "INDEX_DATA");		
		if (file.exists()) {
			FileUtils.deleteDirectory(file);			
		}		
		file.mkdir();
		System.out.println("Done!");
	}

	
	public void createCBDataIndex() throws IOException {
		try {
			init();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return;
		}
		System.out.println("Starting index data!");
		this.dataReader.open(DataSetType.Job);
		JobDTO job = null;
		String str_dir = this.dataReader.getSource();
		File file = new File(str_dir + "INDEX_DATA");

		Directory dir = FSDirectory.open(file);

		@SuppressWarnings("deprecation")
		IndexWriterConfig iwr_config = new IndexWriterConfig(Version.LUCENE_45,
				new StandardAnalyzer(Version.LUCENE_45));
		iwr_config.setOpenMode(OpenMode.CREATE);
		iwr_config.setSimilarity(new DefaultSimilarity());
		IndexWriter index_wr = new IndexWriter(dir, iwr_config);
		while ((job = dataReader.nextJob()) != null) {
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
		index_wr.close();

		this.dataReader.close();
	}

}
