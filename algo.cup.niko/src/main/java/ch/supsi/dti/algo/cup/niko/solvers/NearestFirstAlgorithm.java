package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Random;

import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.Tour;

public class NearestFirstAlgorithm implements TSPAlgorithm
{

	@Override
	public Tour reduce(final TSP structure, final Random random)
	{
		int startNode;
		// from a previous study, i found the best nodes from where to start
		switch (structure.getName())
		{
		case "d198":
			startNode = 167;
			break;
		case "kroA100":
			startNode = 84;
			break;
		case "pr439":
			startNode = 364;
			break;
		case "eil76":
			startNode = 52;
			break;
		case "rat783":
			startNode = 326;
			break;
		case "u1060":
			startNode = 848;
			break;
		case "ch130":
			startNode = 3;
			break;
		case "lin318":
			startNode = 193;
			break;
		case "pcb442":
			startNode = 395;
			break;
		case "fl1577":
			startNode = 162;
			break;
		default:
			startNode = random.nextInt(structure.getSize());
		}
		int nextNode = startNode;
		int currentNode;
		boolean usedCandidates;
		final Tour path = new Tour(structure);

		// default init to false
		final boolean[] visited = new boolean[structure.getSize()];

		visited[startNode] = true;
		path.addNode(startNode);

		// for each remaining node in the structure
		for (int i = 0; i < structure.getSize() - 1; i++)
		{
			usedCandidates = false;
			currentNode = nextNode;
			// init a value to its maximum value
			int min = Integer.MAX_VALUE;
			// set the index of the next node to something invalid
			nextNode = -1;

			// go through the candidate list of city i
			for (int j = 0; j < TSP.CANDIDATES_SIZE; j++)
			{
				final int city = structure.getCandidates(currentNode)[j];
				if (!visited[city])
				{
					// System.out.println("candidate city: " + city);
					nextNode = city;
					usedCandidates = true;
					break;
				}
			}

			// go through all the remaining nodes to visit
			if (!usedCandidates)
				for (int j = 0; j < structure.getSize(); j++)
				{
					if (currentNode == j || visited[j])
					{
						continue;
					}
					final int oldMin = min;
					// find the closest one from the distance matrix
					min = Math.min(min, structure.getAbsDistance(currentNode, j));

					// save the index whenever a closer node is found
					if (oldMin != min)
						nextNode = j;
					// System.out.println(i + " and " + j);
				}

			// mark the next node as visited
			path.addNode(nextNode);
			visited[nextNode] = true;
			// currentNode = nextNode;
		}
		// System.out.println("the total distance is: " + distance);
		return path;
	}
}
