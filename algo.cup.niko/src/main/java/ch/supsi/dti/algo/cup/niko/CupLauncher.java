package ch.supsi.dti.algo.cup.niko;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.solvers.NearestFirstAlgorithm;

public class CupLauncher
{

	public static void main(String[] args)
	{
		Map<String, TSP> problems = new HashMap<>();
		problems.put("ch130", TSPParser.parse("ch130.tsp"));
		problems.put("d198", TSPParser.parse("d198.tsp"));
		problems.put("eil76.opt", TSPParser.parse("eil76.opt.tsp"));
		problems.put("eil76", TSPParser.parse("eil76.tsp"));
		problems.put("fl1577", TSPParser.parse("fl1577.tsp"));
		problems.put("kroA100.opt", TSPParser.parse("kroA100.opt.tsp"));
		problems.put("kroA100", TSPParser.parse("kroA100.tsp"));
		problems.put("lin318", TSPParser.parse("lin318.tsp"));
		problems.put("pcb442", TSPParser.parse("pcb442.tsp"));
		problems.put("pr439", TSPParser.parse("pr439.tsp"));
		problems.put("rat783", TSPParser.parse("rat783.tsp"));
		problems.put("u1060", TSPParser.parse("u1060.tsp"));

		long seed = System.currentTimeMillis();
		Random random = new Random(seed);
		int i = 0;
		while (i < 15)
		{
			long startTime = System.currentTimeMillis();
			Path path = new NearestFirstAlgorithm().reduce(problems.get("ch130"), random);
			System.out.println("Runtime: " + (System.currentTimeMillis() - startTime) + "ms. Distance: " + path.getDistance() + ". Performance: " + path.getPerformance() * 100 + "%");

			// tuo-suo/suo
			i++;
		}
	}
}
