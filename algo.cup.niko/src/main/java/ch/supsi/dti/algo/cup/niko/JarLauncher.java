package ch.supsi.dti.algo.cup.niko;

import java.util.Random;

import ch.supsi.dti.algo.cup.niko.solvers.AntsColony;
import ch.supsi.dti.algo.cup.niko.solvers.NearestFirstAlgorithm;

public class JarLauncher {

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
	public static void main(String[] args) {
		if (args.length < 9 || args.length > 10) {
			System.err.println("Illegal number of parameters");
			System.exit(1);
		}

		String problem = args[0];
		boolean useCandidates = Boolean.parseBoolean(args[1]);
		boolean useQuickAnts = Boolean.parseBoolean(args[2]);
		double alpha = Double.parseDouble(args[3]);
		double beta = Double.parseDouble(args[4]);
		double rho = Double.parseDouble(args[5]);
		double greedyness = Double.parseDouble(args[6]);
		double memory = Double.parseDouble(args[7]);
		int colonySize = Integer.parseInt(args[8]);
		long seed;
		if (args.length == 10)
			seed = Long.parseLong(args[9]);
		else
			seed = System.currentTimeMillis();
		TSP Tproblem = TSPParser.parse(problem);
		Random random = new Random(seed);
		Tour solution = new NearestFirstAlgorithm().reduce(Tproblem, random);
		solution = new AntsColony(solution, useCandidates, useQuickAnts, alpha, beta, rho, greedyness, memory, colonySize).reduce(Tproblem, random);
		SolutionWriter.storeSolution(solution, Tproblem);
	}

}
