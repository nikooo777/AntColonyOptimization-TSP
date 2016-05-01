package ch.supsi.dti.algo.cup.niko;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ch.supsi.dti.algo.cup.niko.solvers.AntsColony;
import ch.supsi.dti.algo.cup.niko.solvers.NearestFirstAlgorithm;

public class JarLauncher
{

	public static volatile boolean timeOver = false;
	private static TSP Tproblem;
	private static boolean useCandidates;
	private static boolean useQuickAnts;
	private static double alpha;
	private static double beta;
	private static double rho;
	private static double greedyness;
	private static double memory;
	private static int colonySize;
	private static long seed;

	public static String getParams()
	{
		return "population: " + colonySize + " Greedyness: " + greedyness + " Memoryness: " + memory + " Beta: " + beta + " Rho: " + rho + " Alpha: " + alpha;
	}

	/**
	 * arg0 = problem
	 * arg1 = use candidates (true/false)
	 * arg2 = use quick ants (true/false)
	 * arg3 = alpha
	 * arg4 = beta
	 * arg5 = rho
	 * arg6 = greedyness
	 * arg7 = memory(ness)
	 * arg8 = colony size
	 * arg9 = seed
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length < 9 || args.length > 10)
		{
			System.err.println("Illegal number of parameters");
			System.exit(1);
		}

		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				JarLauncher.timeOver = true;
				SolutionWriter.storeSolution(AntsColony.bestAntEver, Tproblem);
				final String outString = "Seed for " + args[0] + ": " + seed + " with a performance of: " + (AntsColony.bestAntEver.getPerformance() * 100) + "%" + " --- " + getParams();
				System.out.println(outString);
				System.out.println("Program terminated");
				System.exit(0);
			}
		}, 179_000);

		String problem = args[0] + ".tsp";
		useCandidates = Boolean.parseBoolean(args[1]);
		useQuickAnts = Boolean.parseBoolean(args[2]);
		alpha = Double.parseDouble(args[3]);
		beta = Double.parseDouble(args[4]);
		rho = Double.parseDouble(args[5]);
		greedyness = Double.parseDouble(args[6]);
		memory = Double.parseDouble(args[7]);
		colonySize = Integer.parseInt(args[8]);
		if (args.length == 10)
			seed = Long.parseLong(args[9]);
		else
			seed = System.currentTimeMillis();
		Tproblem = TSPParser.parse(problem);
		Random random = new Random(seed);
		Tour solution = new NearestFirstAlgorithm().reduce(Tproblem, random);
		solution = new AntsColony(solution, useCandidates, useQuickAnts, alpha, beta, rho, greedyness, memory, colonySize).reduce(Tproblem, random);
		SolutionWriter.storeSolution(solution, Tproblem);
	}

}
