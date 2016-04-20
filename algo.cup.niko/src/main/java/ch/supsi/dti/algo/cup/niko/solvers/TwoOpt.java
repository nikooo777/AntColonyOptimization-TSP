package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Random;

import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.Tour;

public class TwoOpt implements TSPAlgorithm
{
	private Tour tour;
	private TSP structure;
	private boolean useCandidates;

	public TwoOpt(Tour pathToImprove, boolean useCandidates)
	{
		this.tour = pathToImprove;
		this.useCandidates = useCandidates;
	}

	@Override
	public Tour reduce(TSP structure, Random random)
	{
		this.structure = structure;
		boolean first_improvement = true;
		int best_gain = -1;
		int best_i, best_j;
		boolean usedCandidates = false;
		// double starttime = System.currentTimeMillis();
		while (best_gain < 0)
		{
			best_i = -1;
			best_j = -1;
			best_gain = 0;
			for (int i = 0; i < this.tour.tourSize() - 2; i++)
			{
				if (this.useCandidates)
				{
					usedCandidates = false;
					// go through the candidates of node i
					for (int j = 0; j < TSP.CANDIDATES_SIZE; j++)
					{
						int candidate = structure.getCandidates(i)[j];
						// given J we want to get the node at J+1. If J is = maxlength of the tour, then the next node is at position 0 of the array
						candidate = this.tour.getIndexOfNode(candidate);
						if (candidate == i)
							continue;

						int gain = computeGain(i, candidate);
						if (gain < best_gain)
						{
							// System.out.println("gain: " + gain);
							best_gain = gain;
							if (i < candidate)
							{
								best_i = i;
								best_j = candidate;
							} else
							{
								best_i = candidate;
								best_j = i;
							}
							usedCandidates = true;
							// first_improvement = true;
							// break;
						}
					}
				}
				if (!usedCandidates)
				{
					for (int j = i + 2; j < this.tour.tourSize(); j++)
					{
						int gain = computeGain(i, j);
						if (gain < best_gain)
						{
							best_gain = gain;
							best_i = i;
							best_j = j;
							if (first_improvement)
							{
								// first_improvement = false;
								break;
							}
						}
					}
				}

				if (best_gain < 0 && (usedCandidates || first_improvement))
				{
					break;
				}
			}
			if (best_gain < 0)
			{
				// double delta = this.tour.getTourLength();
				// System.out.println("#1 delta: " + delta + " performance: " + this.tour.getPerformance() * 100 + "% length: " + this.tour.getTourLength());

				this.tour = swap(best_i, best_j);

				// delta -= this.tour.getTourLength();
				// if (delta < 0)
				// System.err.println("Delta is negative you dumbfuck");
				// System.out.println("#2 delta: " + delta + " performance: " + this.tour.getPerformance() * 100 + "% length: " + this.tour.getTourLength());
			}
		}
		// System.out.println("for took: " + (System.currentTimeMillis() - starttime) + "ms");
		// System.err.println("----------------------------------------------------");
		return this.tour;
	}

	private int computeGain(int i, int j)
	{
		final int a = this.tour.getNode(i);
		final int b = this.tour.getNode((i + 1) % this.tour.tourSize());
		final int c = this.tour.getNode(j);
		final int d = this.tour.getNode((j + 1) % this.tour.tourSize());
		return this.structure.getAbsDistance(a, c) + this.structure.getAbsDistance(b, d) - this.structure.getAbsDistance(c, d) - this.structure.getAbsDistance(a, b);
	}

	private Tour swap(int i, int j)
	{
		if (i > j)
			System.err.println("i > j");
		// System.out.println("swapping between " + i + " and " + j);
		// System.out.println(this.tour);
		// System.out.println("distance before swap: " + this.tour.getDistance());
		// 1. take route[0] to route[i-1] and add them in order to new_route
		int tourSize = this.structure.getSize();
		Tour newTour = new Tour(this.structure);
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
