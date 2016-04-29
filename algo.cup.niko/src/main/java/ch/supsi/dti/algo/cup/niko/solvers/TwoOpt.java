package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Random;

import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.Tour;

public class TwoOpt implements TSPAlgorithm
{
	private Tour tour;
	private TSP structure;
	private boolean useCandidates;
	private boolean complete;

	public TwoOpt(Tour pathToImprove, boolean useCandidates, boolean complete)
	{
		this.tour = pathToImprove;
		this.useCandidates = useCandidates;
		this.complete = complete;
	}

	@Override
	public Tour reduce(TSP structure, Random random)
	{
		this.structure = structure;
		int best_gain = -1;
		int best_i, best_j;
		int gain;
		int candidate;
		boolean usedCandidates = false;
		// double starttime = System.currentTimeMillis();
		while (best_gain != 0)
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
					for (int j = 0; j < structure.CANDIDATES_SIZE; j++)
					{
						candidate = structure.getCandidates(i)[j];
						// given J we want to get the node at J+1. If J is = maxlength of the tour, then the next node is at position 0 of the array
						candidate = this.tour.getIndexOfNode(candidate);
						if (candidate == i)
							continue;

						gain = computeGain(i, candidate);
						if (gain < best_gain)
						{
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
							break;
						}
					}
				}
				if (this.complete && !usedCandidates)
				{
					for (int j = i + 2; j < this.tour.tourSize(); j++)
					{
						gain = computeGain(i, j);
						if (gain < best_gain)
						{
							best_gain = gain;
							best_i = i;
							best_j = j;
							break;
						}
					}
				}

				if (best_gain < 0)
				{
					break;
				}
			}
			if (best_gain < 0)
			{
				// double delta = this.tour.getTourLength();
				// System.out.println("#1 delta: " + delta + " performance: " + this.tour.getPerformance() * 100 + "% length: " + this.tour.getTourLength());

				// this.tour = swap(best_i, best_j);
				this.tour.swap(best_i, best_j);
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
		// final int a = this.tour.getNode(i);
		// final int b = this.tour.getNode((i + 1) % this.tour.tourSize());
		// final int c = this.tour.getNode(j);
		// final int d = this.tour.getNode((j + 1) % this.tour.tourSize());
		return this.structure.getAbsDistance(this.tour.getNode(i), this.tour.getNode(j)) + this.structure.getAbsDistance(this.tour.getNode((i + 1) % this.tour.tourSize()), this.tour.getNode((j + 1) % this.tour.tourSize())) - this.structure.getAbsDistance(this.tour.getNode(j), this.tour.getNode((j + 1) % this.tour.tourSize())) - this.structure.getAbsDistance(this.tour.getNode(i), this.tour.getNode((i + 1) % this.tour.tourSize()));
	}
}
