/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recsys.algorithms.cbf;

import java.io.IOException;
import java.util.HashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author tiendv
 */
public class CollectionDocument {

    public static final String CONTENT = "Content";
    public static Directory _directory;
    public static final FieldType TYPE_STORED = new FieldType();
    IndexReader reader;

    static {
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.freeze();
    }

    public void openReader() throws IOException {
        reader = DirectoryReader.open(_directory);
    }

    public void closeReader() throws IOException {
        reader.close();
    }

    public void indexAllDocument(HashMap<Integer, String> allDocument) throws IOException {
        _directory = new RAMDirectory();
        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT,
                analyzer);
        System.out.println("======START INDEX ALL DOCUMENTS=====");
        IndexWriter writer = new IndexWriter(_directory, iwc);
        for (int i = 0; i < allDocument.size(); i++) {
            addDocument(writer, allDocument.get(i));
        }
        writer.close();
        System.out.println(_directory);
        System.out.println("====== End INDEX ALL =====");
    }

    void addDocument(IndexWriter writer, String content) throws IOException {
        Document doc = new Document();
        Field field = new Field(CONTENT, content, TYPE_STORED);
        doc.add(field);
        writer.addDocument(doc);
    }

    public Terms getTermWithAuthorID(int docId) throws IOException {
        return reader.getTermVector(docId, CONTENT);
    }
}
