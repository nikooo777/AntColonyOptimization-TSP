package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Random;

import ch.supsi.dti.algo.cup.niko.Path;
import ch.supsi.dti.algo.cup.niko.TSP;

public class TwoOpt implements TSPAlgorithm
{
	private Path tour;
	private TSP structure;

	public TwoOpt(Path pathToImprove)
	{
		this.tour = pathToImprove;
	}

	@Override
	public Path reduce(TSP structure, Random random)
	{
		this.structure = structure;

		int improve = 0;
		boolean first_improvement = true;
		while (improve < 20)
		{
			int best_i = -1;
			int best_j = -1;
			int best_gain = 0;
			for (int i = 0; i < this.tour.length() - 2; i++)
			{
				// boolean usedCandidates = false;

				// go through the candidates of node i
				// for (int j = 0; j < TSP.CANDIDATES_SIZE; j++)
				// {
				// int candidate = structure.getCandidates(i)[j];
				// // given J we want to get the node at J+1. If J is = maxlength of the tour, then the next node is at position 0 of the array
				// candidate = this.tour.getIndexOfNode(candidate);
				// if (candidate == i)
				// continue;
				// int gain = computeGain(i, candidate);
				// if (gain < best_gain)
				// {
				// System.out.println("gain: " + gain);
				// best_gain = gain;
				// best_i = i;
				// best_j = candidate;
				// improve = 0;
				// usedCandidates = true;
				// // break;
				// }
				// }
				// if (usedCandidates && best_gain < 0)
				// {
				// this.tour = swap(Math.min(best_i, best_j), Math.max(best_i, best_j));
				// break;
				// }

				// if (!usedCandidates)
				// {
				for (int j = i + 2; j < this.tour.length(); j++)
				{
					int gain = computeGain(i, j);
					if (gain < best_gain)
					{
						System.out.println("gain: " + gain);
						best_gain = gain;
						best_i = i;
						best_j = j;
						improve = 0;
						if (first_improvement)
						{
							break;
						}
					}
				}
				// }

				if (best_gain < 0 && first_improvement)
				// if (best_gain < 0)
				{
					first_improvement = false;
					break;
				}
			}
			if (best_gain < 0)
			{

				this.tour = swap(Math.min(best_i, best_j), Math.max(best_i, best_j));
			}
			improve++;
		}
		return this.tour;
	}

	private int computeGain(int i, int j)
	{
		int a = this.tour.getNode(i);
		int b = this.tour.getNode((i + 1) % this.tour.length());
		int c = this.tour.getNode(j);
		int d = this.tour.getNode((j + 1) % this.tour.length());
		return this.structure.getAbsDistance(a, c) + this.structure.getAbsDistance(b, d) - this.structure.getAbsDistance(c, d) - this.structure.getAbsDistance(a, b);
	}

	private Path swap(int i, int j)
	{
		System.out.println("swapping between " + i + " and " + j);
		// System.out.println(this.tour);
		// System.out.println("distance before swap: " + this.tour.getDistance());
		// 1. take route[0] to route[i-1] and add them in order to new_route
		int tourSize = this.structure.getSize();
		Path newTour = new Path(this.structure);
		for (int k = 0; k <= i; k++)
		{
			newTour.addNode(this.tour.getNode(k));
		}
		// 2. take route[i] to route[k] and add them in reverse order to new_route
		int dec = 0;
		for (int k = i + 1; k <= j; k++)
		{
			newTour.addNode(this.tour.getNode(j - dec));
			dec++;
		}

		// 3. take route[k+1] to end and add them in order to new_route
		for (int k = j + 1; k < tourSize; k++)
		{
			newTour.addNode(this.tour.getNode(k));
		}
		// System.out.println("new distance after swap: " + newTour.getDistance());
		// System.out.println(newTour);
		return newTour;
	}
}
