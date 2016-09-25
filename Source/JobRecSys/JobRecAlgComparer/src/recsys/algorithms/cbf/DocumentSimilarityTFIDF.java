/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recsys.algorithms.cbf;

/**
 *
 * @author tiendv
 */
import java.io.IOException;
import java.util.*;

import org.apache.commons.math3.linear.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.*;
import org.apache.lucene.search.DocIdSetIterator;


public class DocumentSimilarityTFIDF {


    public static final String CONTENT = "Content";
    public static int N = 0;//Total number of documents
    protected Directory _directory;
    protected Object lock = new Object();

    protected final Set<String> terms = new HashSet<>();
    protected  RealVector v1;
    protected  RealVector v2;
    protected IndexReader reader ;
    public DocumentSimilarityTFIDF() {
    	
    }
    
    public void openReader() throws IOException
    {
    	reader = DirectoryReader.open(_directory);
    }
    public void closeReader() throws IOException
    {
    	reader.close();
    }
    
    public DocumentSimilarityTFIDF(String s1, String s2) throws IOException {
        Directory directory = createIndex(s1, s2);
        IndexReader reader = DirectoryReader.open(directory);
        Map<String, Double> f1 = getWieghts(reader, 0);
        Map<String, Double> f2 = getWieghts(reader, 1);
        reader.close();
        v1 = toRealVector(f1);
        System.out.println( "V1: " +v1 );
        v2 = toRealVector(f2);
        System.out.println( "V2: " +v2 );
        
        System.out.println( "Cosine: "+getCosineSimilarity() );
    }

    public Directory createIndex(String s1, String s2) throws IOException {
        Directory directory = new RAMDirectory();
        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT,
                analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);
        addDocument(writer, s1);
        addDocument(writer, s2);
        writer.close();
        return directory;
    }

    /* Indexed, tokenized, stored. */
    public static final FieldType TYPE_STORED = new FieldType();

    static {
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.freeze();
    }

    void addDocument(IndexWriter writer, String content) throws IOException {
        Document doc = new Document();
        Field field = new Field(CONTENT, content, TYPE_STORED);
        doc.add(field);
        writer.addDocument(doc);
    }

    double getCosineSimilarity() {
        double dotProduct = v1.dotProduct(v2);
        double normalization = (v1.getNorm() * v2.getNorm());
        return dotProduct / normalization;
    }
    
    public double getCosineSimilarityWhenIndexAllDocument( int doc1, int doc2) throws IOException {
        IndexReader reader = DirectoryReader.open(_directory);
        Map<String, Double> f1 = getWieghts(reader, doc1);
        Map<String, Double> f2 = getWieghts(reader, doc2);
        reader.close();
        RealVector _v1 = toRealVector(f1);
        RealVector _v2 = toRealVector(f2);
        double dotProduct = _v1.dotProduct(_v2);
        double normalization = (_v1.getNorm() * _v2.getNorm());
        return dotProduct / normalization;
    }
    
    public double getCosineSimilarityWithUserRating(int cv,ArrayList<Integer> arrayList, int job) throws IOException {        
        Map<String, Double> f1 = getWieghts(reader, cv);
        Map<String, Double> f2 = getWieghts(reader, job);              
        RealVector _v1 = toRealVector(f1);
        if(arrayList != null)
        {        	
            for(int i : arrayList)
            {	            
            	Map<String, Double> p = getWieghts(reader, i);
            	RealVector v = toRealVector(p);
            	_v1 = _v1.add(v);
            }        	
        }

        RealVector _v2 = toRealVector(f2);
        double dotProduct = _v1.dotProduct(_v2);
        double normalization = (_v1.getNorm() * _v2.getNorm());
        return dotProduct / normalization;
    }
    
	public void addTermModel(int doc){     
    	
        try {
			extractTerm(reader, doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
    }
    
    
    public void indexAllDocument(HashMap<Integer, String> allDocument) throws IOException {
        _directory = new RAMDirectory();
        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT,
                analyzer);
        System.out.println("======START INDEX ALL DOCUMENTS=====");
        IndexWriter writer = new IndexWriter(_directory, iwc);
        for (int i=0; i<allDocument.size();i++)
        {
            addDocument(writer, allDocument.get(i));
        }
        writer.close();
        System.out.println("====== End INDEX ALL =====");
    }
    private Map<String, Integer> docFrequencies = new HashMap<>();
    
    public void CalculateIdf() throws IOException
    {
    	for(String i : terms)
    	{
    		docFrequencies.put(i, reader.docFreq( new Term( CONTENT, i ) ));
    	}
    }
    
    public Map<String, Double> getWieghts(IndexReader reader, int docId)
            throws IOException {
        Terms vector = reader.getTermVector(docId, CONTENT);        
        Map<String, Integer> termFrequencies = new HashMap<>();
        Map<String, Double> tf_Idf_Weights = new HashMap<>();
        TermsEnum termsEnum = null;
        DocsEnum docsEnum = null;
        termsEnum = vector.iterator(termsEnum);
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            //docFrequencies.put(term, reader.docFreq( new Term( CONTENT, term ) ));
            docsEnum = termsEnum.docs(null, null);
            while (docsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                termFrequencies.put(term, docsEnum.freq());
            }
        }
        for ( String term : docFrequencies.keySet() ) {
            int tf = termFrequencies.containsKey(term) ? termFrequencies.get(term) : 0;
            int df = docFrequencies.get(term);
            double idf = ( 1 + Math.log(N) - Math.log(df) );
            double w = tf * idf;
            tf_Idf_Weights.put(term, w);
           // System.out.printf("Term: %s - tf: %d, df: %d, idf: %f, w: %f\n", term, tf, df, idf, w);
        }
        return tf_Idf_Weights;
    }
    
    public void extractTerm(IndexReader reader, int docId)
            throws IOException {
        Terms vector = reader.getTermVector(docId, CONTENT);        
        TermsEnum termsEnum = null;       
        termsEnum = vector.iterator(termsEnum);
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();                   
            terms.add(term);
        }       
    }

    public RealVector toRealVector(Map<String, Double> map) {
        RealVector vector = new ArrayRealVector(terms.size());
        int i = 0;
        double value = 0;
         //synchronized(lock) {
            
             for (String term : terms) {
                 if (map.containsKey(term)) {
                     value = map.get(term);
                 } else {
                     value = 0;
                 }
                 vector.setEntry(i++, value);
             }
         //}
        return vector;
    }
    public RealVector toRealVector(Map<String, Double> map, Map<String, Double> world) {
        RealVector vector = new ArrayRealVector(terms.size());
        int i = 0;
        double value = 0;
         //synchronized(lock) {
            
             for (String term : terms) {
                 if (map.containsKey(term)) {
                     value = map.get(term);
                 } else {
                     value = 0;
                 }
                 vector.setEntry(i++, value);
             }
         //}
        return vector;
    }

    public static void printMap(Map<String, Integer> map) {
        for ( String key : map.keySet() ) {
            System.out.println( "Term: " + key + ", value: " + map.get(key) );
        }
    }

    public static void printMapDouble(Map<String, Double> map) {
        for ( String key : map.keySet() ) {
            System.out.println( "Term: " + key + ", value: " + map.get(key) );
        }
    }

}