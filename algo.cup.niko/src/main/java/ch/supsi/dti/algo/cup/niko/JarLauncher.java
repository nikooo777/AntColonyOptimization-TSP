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
	// private static boolean useCandidates;
	// private static boolean useQuickAnts;
	private static double alpha;
	private static double beta;
	private static double rho;
	private static double greedyness;
	private static double memory;
	private static int colonySize;
	private static long seed;
	private static long runTime;

	public static String getParams()
	{
		return "population: " + colonySize + " Greedyness: " + greedyness + " Memoryness: " + memory + " Beta: " + beta + " Rho: " + rho + " Alpha: " + alpha;
	}

	// arg1 = use candidates (true/false)
	// arg2 = use quick ants (true/false)
	/**
	 * arg0 = problem
	 * arg1 = alpha
	 * arg2 = beta
	 * arg3 = rho
	 * arg4 = greedyness
	 * arg5 = memory(ness)
	 * arg6 = colony size
	 * arg7 = time
	 * arg8 = seed
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length < 8 || args.length > 9)
		{
			System.err.println("Illegal number of parameters");
			System.exit(1);
		}
		runTime = Long.parseLong(args[7]);

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
				System.exit(0);
			}
		}, runTime);

		String problem = args[0] + ".tsp";
		// useCandidates = Boolean.parseBoolean(args[1]);
		// useQuickAnts = Boolean.parseBoolean(args[2]);
		alpha = Double.parseDouble(args[1]);
		if (alpha > 0.15)
			System.err.println("Alpha is usually 0.1! Don't mistake it with beta.");

		beta = Double.parseDouble(args[2]);
		if (beta < 1)
			System.err.println("Beta is usually between 1-2! Don't mistake it with alpha.");
		rho = Double.parseDouble(args[3]);
		if (rho > 0.15)
			System.err.println("rho is usually 0.1!");

		greedyness = Double.parseDouble(args[4]);
		if (greedyness < 0.89)
			System.err.println("greedyness is usually over between 0.9 and 1!");
		memory = Double.parseDouble(args[5]);

		colonySize = Integer.parseInt(args[6]);
		if (args.length == 9)
			seed = Long.parseLong(args[8]);
		else
			seed = System.currentTimeMillis();
		Tproblem = TSPParser.parse(problem);
		Random random = new Random(seed);
		Tour solution = new NearestFirstAlgorithm().reduce(Tproblem, random);
		solution = new AntsColony(solution, true, true, alpha, beta, rho, greedyness, memory, colonySize).reduce(Tproblem, random);
		SolutionWriter.storeSolution(solution, Tproblem);
	}

}
