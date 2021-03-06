package ch.supsi.dti.algo.cup.niko;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.solvers.AntsColony;
import ch.supsi.dti.algo.cup.niko.solvers.NearestFirstAlgorithm;

public class CupLauncher
{
	public static long overallstarttime;

	private static void parseProblems(final Map<String, TSP> problems)
	{
		// require lower greedyness
		// problems.put("ch130", TSPParser.parse("ch130.tsp"));
		// problems.put("eil76", TSPParser.parse("eil76.tsp"));
		// problems.put("kroA100", TSPParser.parse("kroA100.tsp"));

		// sligthly harder
		// problems.put("lin318", TSPParser.parse("lin318.tsp"));
		// problems.put("d198", TSPParser.parse("d198.tsp"));
		// problems.put("pcb442", TSPParser.parse("pcb442.tsp"));
		// problems.put("pr439", TSPParser.parse("pr439.tsp"));

		// hard as fuck
		problems.put("rat783", TSPParser.parse("rat783.tsp"));
		// problems.put("fl1577", TSPParser.parse("fl1577.tsp"));
		// problems.put("u1060", TSPParser.parse("u1060.tsp"));
	}

	public static void main(final String[] args)
	{
		overallstarttime = System.currentTimeMillis();
		final Map<String, TSP> problems = new HashMap<>();
		final Map<String, Long> solutions = new HashMap<>();
		final Map<String, Integer> solutionSizes = new HashMap<>();
		final Map<String, String> solutionParams = new HashMap<>();

		parseProblems(problems);
		final long parseTime = System.currentTimeMillis() - overallstarttime;

		for (final String s : problems.keySet())
		{
			System.out.println("---->" + s);
			int localbest = Integer.MAX_VALUE;
			for (int i = 0; i < 1; i++)
			{
				overallstarttime = System.currentTimeMillis() + parseTime;
				// seed generation
				final long seed = System.currentTimeMillis();
				System.out.println("seed: " + seed);
				// while testing check if this is set to seed
				final Random random = new Random(seed);
				long startTime = System.currentTimeMillis();
				final Tour tour = new NearestFirstAlgorithm().reduce(problems.get(s), random);

				startTime = System.currentTimeMillis();
				final AntsColony ac = new AntsColony(tour, true, true, 2, 0.1, 0.1, 0.95, 0.0, 15);
				final Tour improvedTour = ac.reduce(problems.get(s), random);

				System.out.println("[" + s + "]" + "Runtime: " + (System.currentTimeMillis() - startTime) + "ms. Distance: " + improvedTour.getTourLength() + ". Performance: " + improvedTour.getPerformance() * 100 + "% validation: " + improvedTour.validate());
				if (improvedTour.getTourLength() <= problems.get(s).getBestKnown())
				{
					solutions.put(s, seed);
					localbest = improvedTour.getTourLength();
					solutionSizes.put(s, localbest);
					solutionParams.put(s, ac.getParams());
					break;
				}
				if (improvedTour.getTourLength() < localbest)
				{
					localbest = improvedTour.getTourLength();
					solutions.put(s, seed);
					solutionSizes.put(s, localbest);
					solutionParams.put(s, ac.getParams());
				}
				final String outString = "Best so far: Seed for " + s + ": " + solutions.get(s) + " with a performance of: " + ((solutionSizes.get(s) - problems.get(s).getBestKnown()) / (double) problems.get(s).getBestKnown() * 100) + "%" + " --- " + solutionParams.get(s) + "\n";
				System.out.println(outString);
			}
		}
		System.out.println("Overall Runtime: " + (System.currentTimeMillis() - overallstarttime) / 1000. + "s");
		for (final String s : problems.keySet())
		{
			try
			{
				if (!Files.exists(Paths.get(s + "_sol.txt")))
					Files.createFile(Paths.get(s + "_sol.txt"));

				final String outString = "Seed for " + s + ": " + solutions.get(s) + " with a performance of: " + ((solutionSizes.get(s) - problems.get(s).getBestKnown()) / (double) problems.get(s).getBestKnown() * 100) + "%" + " --- " + solutionParams.get(s) + "\n";
				System.out.println(outString);

				Files.write(Paths.get(s + "_sol.txt"), outString.getBytes(), StandardOpenOption.APPEND);
			} catch (final IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}