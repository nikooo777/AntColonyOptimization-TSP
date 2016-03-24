package ch.supsi.dti.algo.cup.niko;

public class CupLauncher
{

	public static void main(String[] args)
	{
		TSP problem = TSPParser.parse("ch130.tsp");
		NearestFirstAlgorithm.reduce(problem);
	}
}
