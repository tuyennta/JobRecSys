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
import org.apache.lucene.index.*;
import org.apache.lucene.util.*;
public class SimilarityTF {

    public static final String CONTENT = "Content";
    private final Set<String> terms = new HashSet<>();

    public SimilarityTF() {
    }

    public double getCosineSimilarityWhenIndexAllDocument(Terms author1, Terms author2)
            throws IOException {;
        Map<String, Integer> f1 = getTermFrequencies(author1);
        Map<String, Integer> f2 = getTermFrequencies(author2);
        RealVector v1 = toRealVector(f1);
        //  System.out.println("V1"+ v1);
        RealVector v2 = toRealVector(f2);
        terms.clear();
        //  System.out.println("V2"+ v2);
        if (v1 != null && v2 != null) {
            //System.out.println( "Similarity: "+ (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm()));
            return (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());
        } else {
            return 0;
        }
    }

    public Map<String, Integer> getTermFrequencies(Terms vector)
            throws IOException {
        TermsEnum termsEnum = null;
        termsEnum = vector.iterator(termsEnum);
        Map<String, Integer> frequencies = new HashMap<>();
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            int freq = (int) termsEnum.totalTermFreq();
            frequencies.put(term, freq);
            terms.add(term);
        }
        return frequencies;
    }

    public RealVector toRealVector(Map<String, Integer> map) {
        RealVector vector = new ArrayRealVector(terms.size());
        int i = 0;
        double value = 0;
        for (String term : terms) {
            if (map.containsKey(term)) {
                value = map.get(term);
            } else {
                value = 0;
            }
            vector.setEntry(i++, value);
        }
        return vector;
    }
}