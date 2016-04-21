package ch.supsi.dti.algo.cup.niko;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.solvers.AntsColony;
import ch.supsi.dti.algo.cup.niko.solvers.NearestFirstAlgorithm;

public class CupLauncher
{
	public static long overallstarttime;

	private static void parseProblems(Map<String, TSP> problems)
	{
		// require lower greedyness
		// problems.put("ch130", TSPParser.parse("ch130.tsp"));
		// problems.put("eil76", TSPParser.parse("eil76.tsp"));
		// problems.put("kroA100", TSPParser.parse("kroA100.tsp"));

		// sligthly harder
		// problems.put("lin318", TSPParser.parse("lin318.tsp"));
		// problems.put("d198", TSPParser.parse("d198.tsp"));
		problems.put("pcb442", TSPParser.parse("pcb442.tsp"));
		// problems.put("pr439", TSPParser.parse("pr439.tsp"));

		// hard as fuck
		// problems.put("rat783", TSPParser.parse("rat783.tsp"));
		// problems.put("fl1577", TSPParser.parse("fl1577.tsp"));
		// problems.put("u1060", TSPParser.parse("u1060.tsp"));
	}

	public static void main(final String[] args)
	{
		overallstarttime = System.currentTimeMillis();
		Map<String, TSP> problems = new HashMap<>();
		Map<String, Long> solutions = new HashMap<>();
		Map<String, Integer> solutionSizes = new HashMap<>();

		parseProblems(problems);
		long parseTime = System.currentTimeMillis() - overallstarttime;

		for (final String s : problems.keySet())
		{

			System.out.println("---->" + s);
			int localbest = Integer.MAX_VALUE;
			for (int i = 0; i < 20; i++)
			{
				overallstarttime = System.currentTimeMillis() + parseTime;
				long seed = System.currentTimeMillis();
				System.out.println("seed: " + seed);
				final Random random = new Random(seed);
				long startTime = System.currentTimeMillis();
				final Tour path = new NearestFirstAlgorithm().reduce(problems.get(s), random);

				startTime = System.currentTimeMillis();
				final Tour improvedPath = new AntsColony(path, true).reduce(problems.get(s), random);
				System.out.println("[" + s + "]" + "Runtime: " + (System.currentTimeMillis() - startTime) + "ms. Distance: " + improvedPath.getTourLength() + ". Performance: " + improvedPath.getPerformance() * 100 + "% validation: " + improvedPath.validate());
				if (improvedPath.getTourLength() <= problems.get(s).getBestKnown())
				{
					solutions.put(s, seed);
					localbest = improvedPath.getTourLength();
					solutionSizes.put(s, localbest);
					break;
				}
				if (improvedPath.getTourLength() < localbest)
				{
					localbest = improvedPath.getTourLength();
					solutions.put(s, seed);
					solutionSizes.put(s, localbest);
				}
			}
		}
		System.out.println("Overall Runtime: " + (System.currentTimeMillis() - overallstarttime) / 1000. + "s");
		for (final String s : problems.keySet())
		{
			System.out.println("Seed for " + s + ": " + solutions.get(s) + " with a performance of: " + ((solutionSizes.get(s) - problems.get(s).getBestKnown()) / (double) problems.get(s).getBestKnown() * 100) + "%");
		}
	}
}
