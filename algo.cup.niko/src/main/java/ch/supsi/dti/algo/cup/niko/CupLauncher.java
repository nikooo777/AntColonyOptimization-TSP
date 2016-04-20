package ch.supsi.dti.algo.cup.niko;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.solvers.NearestFirstAlgorithm;
import ch.supsi.dti.algo.cup.niko.solvers.TwoOpt;

public class CupLauncher
{

	public static void main(final String[] args)
	{
		final Map<String, TSP> problems = new HashMap<>();
		problems.put("ch130", TSPParser.parse("ch130.tsp"));
		problems.put("d198", TSPParser.parse("d198.tsp"));
		problems.put("eil76", TSPParser.parse("eil76.tsp"));
		problems.put("fl1577", TSPParser.parse("fl1577.tsp"));
		problems.put("kroA100", TSPParser.parse("kroA100.tsp"));
		problems.put("lin318", TSPParser.parse("lin318.tsp"));
		problems.put("pcb442", TSPParser.parse("pcb442.tsp"));
		problems.put("pr439", TSPParser.parse("pr439.tsp"));
		problems.put("rat783", TSPParser.parse("rat783.tsp"));
		problems.put("u1060", TSPParser.parse("u1060.tsp"));

		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final long overallstarttime = System.currentTimeMillis();
		for (final String s : problems.keySet())
		{

			long startTime = System.currentTimeMillis();
			final Tour path = new NearestFirstAlgorithm().reduce(problems.get(s), random);
			System.out.println("[" + s + "]" + "Runtime: " + (System.currentTimeMillis() - startTime) + "ms. Distance: " + path.getTourLength() + ". Performance: " + path.getPerformance() * 100 + "% validation: " + path.validate());

			startTime = System.currentTimeMillis();
			final Tour improvedPath = new TwoOpt(path, true).reduce(problems.get(s), random);
			System.out.println("\t[" + s + "]" + "Runtime: " + (System.currentTimeMillis() - startTime) + "ms. Distance: " + improvedPath.getTourLength() + ". Performance: " + improvedPath.getPerformance() * 100 + "% validation: " + improvedPath.validate());
		}
		System.out.println("Overall Runtime: " + (System.currentTimeMillis() - overallstarttime) / 1000. + "s");
	}
}
