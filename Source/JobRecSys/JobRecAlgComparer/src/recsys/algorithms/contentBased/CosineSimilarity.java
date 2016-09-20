package recsys.algorithms.contentBased;

import java.util.ArrayList;

public class CosineSimilarity {

	String arrayToString(double[] data) {
		StringBuilder sb = new StringBuilder();
		for (double i : data) {
			sb.append(i + "\t");
		}
		return sb.toString();
	}

	public void sort(double[] values) {
		// check for empty or null array
		if (values == null || values.length == 0) {
			return;
		}
		quicksort(values, 0, values.length - 1);
	}

	private void quicksort(double[] numbers, int low, int high) {
		int i = low, j = high;
		// Get the pivot element from the middle of the list
		double pivot = numbers[low + (high - low) / 2];

		// Divide into two lists
		while (i <= j) {
			// If the current value from the left list is smaller then the pivot
			// element then get the next element from the left list
			while (numbers[i] < pivot) {
				i++;
			}
			// If the current value from the right list is larger then the pivot
			// element then get the next element from the right list
			while (numbers[j] > pivot) {
				j--;
			}

			// If we have found a values in the left list which is larger then
			// the pivot element and if we have found a value in the right list
			// which is smaller then the pivot element then we exchange the
			// values.
			// As we are done we can increase i and j
			if (i <= j) {
				exchange(numbers, i, j);
				i++;
				j--;
			}
		}
		// Recursion
		if (low < j)
			quicksort(numbers, low, j);
		if (i < high)
			quicksort(numbers, i, high);
	}

	private void exchange(double[] numbers, int i, int j) {
		double temp = numbers[i];
		numbers[i] = numbers[j];
		numbers[j] = temp;
	}

	public double jaccardSimilarity(double[] docVector1, double[] docVector2) {

		double jaccard = 0.0;
		double dim = 0.0;
		for (int i = 0; i < docVector1.length; i++) // docVector1 and docVector2
		{
			if (docVector1[i] != 0.0d || docVector2[i] != 0.0d) {
				dim++;
			}
			if (docVector1[i] * docVector2[i] == 0.0d)
				continue;

			jaccard++;

		}
		return jaccard / dim;
	}

	private double getMaxTop(double[] array, int top) {
		double[] buf = new double[array.length];
		buf = array.clone();
		sort(buf);
		return buf[buf.length - top];
	}

	/**
	 * Method to calculate cosine similarity between two documents.
	 * 
	 * @param docVector1
	 *            : document vector 1 (a)
	 * @param docVector2
	 *            : document vector 2 (b)
	 * @return
	 */
	public double cosineSimilarity(double[] docVector1, double[] docVector2) {
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;
		int top = (int)(docVector1.length * 0.3d);
		double max1 = getMaxTop(docVector1, top);
		double max2 = getMaxTop(docVector2, top);
		for (int i = 0; i < docVector1.length; i++) // docVector1 and docVector2
		{
			if(docVector1[i] < max1)
			{
				docVector1[i] = 0;
			}
			if(docVector2[i] < max2)
			{
				docVector2[i] = 0;
			}
			
			dotProduct += (docVector1[i] * docVector2[i]); // a.b
			magnitude1 += Math.pow(docVector1[i], 2); // (a^2)
			magnitude2 += Math.pow(docVector2[i], 2); // (b^2)
		}
		magnitude1 = Math.sqrt(magnitude1);// sqrt(a^2)
		magnitude2 = Math.sqrt(magnitude2);// sqrt(b^2)
		if (magnitude1 != 0.0 && magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			return 0.0;
		}
		return cosineSimilarity;
	}
}