package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Random;

import ch.supsi.dti.algo.cup.niko.Path;
import ch.supsi.dti.algo.cup.niko.TSP;

public class TwoOpt implements TSPAlgorithm {
	private Path tour;
	private TSP structure;

	public TwoOpt(Path pathToImprove) {
		this.tour = pathToImprove;
	}

	@Override
	public Path reduce(TSP structure, Random random) {
		this.structure = structure;
		// int tourSize = structure.getSize();
		// int improve = 0;
		// while (improve < 10)
		// {
		// double bestDistance = this.tour.getDistance();
		// out: for (int i = 0; i < tourSize - 1; i++)
		// {
		// for (int j = i + 1; j < tourSize; j++)
		// {
		// double distancei_i1 = structure.getAbsDistance(this.tour.getNode(i), this.tour.getNode((i + 1) % tourSize));
		// double distancej_j1 = structure.getAbsDistance(this.tour.getNode(j), this.tour.getNode((j + 1) % tourSize));
		// double distancei_j = structure.getAbsDistance(this.tour.getNode(i), this.tour.getNode(j));
		// double distancei1_j1 = structure.getAbsDistance(this.tour.getNode((i + 1) % tourSize), this.tour.getNode((j + 1) % tourSize));
		//
		// if (distancei_i1 + distancej_j1 > distancei_j + distancei1_j1)
		// {
		// Path newTour = twoOptSwap(i, j);
		// double newDistance = newTour.getDistance();
		// System.out.println(newDistance + " vs " + bestDistance);
		// if (newDistance < bestDistance)
		// {
		// improve = 0;
		// this.tour = newTour;
		// bestDistance = newDistance;
		// // break out;
		// }
		// }
		// }
		// }
		// improve++;
		// }

		// boolean improvement = true;
		// wholeLoop: while (improvement)
		// {
		// int best_i = -1, best_j = -1;
		// improvement = false;
		// int tourSize = structure.getSize();
		// for (int i = 0; i < tourSize; i++)
		// {
		// for (int j = +1; j < tourSize - 1; j++)
		// {
		// if (j == i - 1 || j == i + 1)
		// continue;
		//
		// // if distance(i, i+1) + distance(j, j+1) > distance(i, j) + distance(i+1, j+1) then...
		// double distancei_i1 = structure.getAbsDistance(this.tour.getNode(i), this.tour.getNode((i + 1) % tourSize));
		// double distancej_j1 = structure.getAbsDistance(this.tour.getNode(j), this.tour.getNode((j + 1) % tourSize));
		// double distancei_j = structure.getAbsDistance(this.tour.getNode(i), this.tour.getNode(j));
		// double distancei1_j1 = structure.getAbsDistance(this.tour.getNode((i + 1) % tourSize), this.tour.getNode((j + 1) % tourSize));
		//
		// if (distancei_i1 + distancej_j1 > distancei_j + distancei1_j1)
		// {
		// best_i = i;
		// best_j = j;
		// this.tour = swap(i, j);
		// improvement = true;
		// }
		// }
		//
		// }
		// if (improvement)
		// {
		// swap(best_i, best_j);
		// }
		// }

		int improve = 0;
		boolean first_improvement = true;
		while (improve < 20) {
			int best_i = -1;
			int best_j = -1;
			int best_gain = 0;
			for (int i = 0; i < this.tour.length(); i++) {

				boolean usedCandidates = false;

				// go through the candidates of node i
				for (int j = 0; j < TSP.CANDIDATES_SIZE; j++) {
					int candidate = structure.getCandidates(i)[j];
					int gain = computeGain(i, candidate);
					if (gain < best_gain) {
						// System.out.println("gain: " + gain);
						best_gain = gain;
						best_i = i;
						best_j = j;
						improve = 0;
						usedCandidates = true;
						// if (first_improvement) {
						break;
						// }
					}
				}

				if (!usedCandidates) {
					for (int j = i + 1; j < this.tour.length(); j++) {
						int gain = computeGain(i, j);
						if (gain < best_gain) {
							// System.out.println("gain: " + gain);
							best_gain = gain;
							best_i = i;
							best_j = j;
							improve = 0;
							if (first_improvement) {
								break;
							}
						}
					}
				}
				if (best_gain < 0 && first_improvement)
				// if (best_gain < 0)
				{
					first_improvement = false;
					break;
				}
			}
			if (best_gain < 0) {
				this.tour = swap(best_i, best_j);
			}
			improve++;
		}
		return this.tour;
	}

	private int computeGain(int i, int j) {
		int a = this.tour.getNode(i);
		int b = this.tour.getNode((i + 1) % this.tour.length());
		int c = this.tour.getNode(j);
		int d = this.tour.getNode((j + 1) % this.tour.length());
		return this.structure.getAbsDistance(a, c) + this.structure.getAbsDistance(b, d) - this.structure.getAbsDistance(c, d) - this.structure.getAbsDistance(a, b);
	}

	private Path swap(int i, int j) {
		// System.out.println("swapping between " + i + " and " + j);
		// System.out.println(this.tour);
		// System.out.println("distance before swap: " + this.tour.getDistance());
		// 1. take route[0] to route[i-1] and add them in order to new_route
		int tourSize = this.structure.getSize();
		Path newTour = new Path(this.structure);
		for (int k = 0; k <= i; k++) {
			newTour.addNode(this.tour.getNode(k));
		}
		// 2. take route[i] to route[k] and add them in reverse order to new_route
		int dec = 0;
		for (int k = i + 1; k <= j; k++) {
			newTour.addNode(this.tour.getNode(j - dec));
			dec++;
		}

		// 3. take route[k+1] to end and add them in order to new_route
		for (int k = j + 1; k < tourSize; k++) {
			newTour.addNode(this.tour.getNode(k));
		}
		// System.out.println("new distance after swap: " + newTour.getDistance());
		// System.out.println(newTour);
		// System.exit(0);
		return newTour;
	}
}
