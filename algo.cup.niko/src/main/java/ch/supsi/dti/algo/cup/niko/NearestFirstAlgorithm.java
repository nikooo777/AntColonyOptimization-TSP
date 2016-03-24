package ch.supsi.dti.algo.cup.niko;

import java.util.HashSet;

public class NearestFirstAlgorithm
{
	public static void reduce(TSP structure)
	{
		int distance = 0;
		int nextNode = 0;
		int currentNode = -1;

		HashSet<Integer> visitedNodes = new HashSet<>();
		visitedNodes.add(0);

		// for each remaining node in the structure
		for (int i = 0; i < structure.getSize(); i++)
		{
			currentNode = nextNode;
			// init a value to its maximum value
			int min = Integer.MAX_VALUE;
			// set the index of the next node to something invalid
			nextNode = -1;

			// if we visited all nodes then we have to add the last segment and break free
			if (i == structure.getSize() - 1)
			{
				distance += structure.getAbsDistance(currentNode, 0);
				System.out.println("from " + currentNode + " to " + 0 + " distance: \t" + structure.getAbsDistance(currentNode, 0) + "\t total distance: " + distance);
				break;
			}

			// go through all the remaining nodes to visit
			for (int j = 0; j < structure.getSize(); j++)
			{
				if (currentNode == j)
					continue;
				if (visitedNodes.contains(j))
				{
					continue;
				}
				int oldMin = min;
				// find the closest one from the distance matrix
				min = Math.min(min, structure.getAbsDistance(currentNode, j));

				// save the index whenever a closer node is found
				if (oldMin != min)
					nextNode = j;
				// System.out.println(i + " and " + j);
			}

			// mark the next node as visited
			distance += structure.getAbsDistance(currentNode, nextNode);
			System.out.println("from " + currentNode + " to " + nextNode + " distance: \t" + structure.getAbsDistance(currentNode, nextNode) + "\t total distance: " + distance);
			visitedNodes.add(nextNode);
			// currentNode = nextNode;
		}
		System.out.println("the total distance is: " + distance);
	}
}
