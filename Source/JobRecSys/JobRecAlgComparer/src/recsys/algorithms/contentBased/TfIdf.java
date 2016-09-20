package recsys.algorithms.contentBased;

import java.util.List;

public class TfIdf {

	/**
	 * Calculates the tf of term termToCheck
	 * 
	 * @param totalterms
	 *            : Array of all the words under processing document
	 * @param termToCheck
	 *            : term of which tf is to be calculated.
	 * @return tf(term frequency) of term termToCheck
	 */
	public double tfCalculator(String[] totalterms, String termToCheck) {
		double count = 0;
		for (String s : totalterms) {
			if (s.equals(termToCheck)) {
				count++;
			}
		}					
		return count;
		//return Math.log10(count + 1);
	}

	/**
	 * Calculates idf of term termToCheck
	 * 
	 * @param profileSet
	 *            : all the terms of all the documents
	 * @param termToCheck
	 * @return idf(inverse document frequency) score
	 */
	public double idfCalculator(List<String[]>  documentSet, String termToCheck) {
		double count = 0.0d;		
		for(String[] doc : documentSet)
		{
			for(String term : doc)
			{
				if(term.equals(termToCheck))
				{
					count++;
					break;
				}
			}
		}		
		double val = ((documentSet.size() + 1)) / (count + 1);
		return Math.log10(val);
	}
}