package recsys.algorithms.cbf;

public class CBTopNJobs {

	public int topN;
	double[] TopNscore;
	public String[] TopNjob;
	public double max_score = 0.0d;
	
	public CBTopNJobs(int _topN)
	{
		topN = _topN;
		TopNscore = new double[_topN];
		TopNjob = new String[_topN];
		topN = _topN;		
		for (int i = 0; i < _topN; i++) {
			TopNjob[i] = "";
			TopNscore[i] = 0.0d;
		}
	}

	public void add(String jobId, double value)
	{	
		int i = 0;
		int minIndex = 0;
		for (i = 0; i < topN; i++) {
			if (value > TopNscore[i]) {				
				max_score = value > max_score ? value : max_score;			
			}
			if(TopNscore[minIndex] > TopNscore[i])
			{
				minIndex = i;
			}
		}
		TopNjob[minIndex] = jobId;
		TopNscore[minIndex] = value;
	}
}
