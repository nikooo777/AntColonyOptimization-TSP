package ch.supsi.dti.algo.cup.niko;

import java.util.ArrayList;
import java.util.Collections;

/**
 * values are stored starting from index 0.
 *
 * @author Niko
 *
 */
public class TSP {
	public static final int CANDIDATES_SIZE = 15;

	public enum Type {
		TSP, TOUR
	}

	private final double[][] matrix;
	private final int[][] distanceMatrix;
	private final String name;
	private final Type type;
	private final int dimension;
	private final int bestKnown;
	private final String comment;
	private final int[][] candidates;

	public TSP(final String name, final String comment, final Type type, final int dimension, final int bestKnown, final double[][] matrix) {
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.dimension = dimension;
		this.bestKnown = bestKnown;
		this.matrix = matrix;
		this.distanceMatrix = new int[this.dimension][this.dimension];
		this.candidates = new int[this.dimension][CANDIDATES_SIZE];
		computeDistances();
		buildCandidateLists();
	}

	class CityDistance implements Comparable<CityDistance> {
		private final int city;
		private final int distance;

		public CityDistance(final int city, final int distance) {
			this.city = city;
			this.distance = distance;
		}

		@Override
		public int compareTo(final CityDistance o) {
			return Integer.compare(this.distance, o.distance);
		}

	}

	private void buildCandidateLists() {

		for (int i = 0; i < this.dimension; i++) {
			final ArrayList<CityDistance> sortedList = new ArrayList<>();
			for (int j = 0; j < this.dimension; j++) {
				if (i == j)
					continue;
				sortedList.add(new CityDistance(j, this.distanceMatrix[i][j]));
			}
			Collections.sort(sortedList);
			for (int j = 0; j < 15; j++) {
				this.candidates[i][j] = sortedList.get(j).city;
			}
		}
	}

	public int[] getCandidates(final int node) {
		return this.candidates[node];
	}

	/**
	 * Computes the distance between nodes.
	 * Rounds the result to the nearest integer
	 */
	private void computeDistances() {
		for (int i = 0; i < this.dimension; i++) {
			for (int j = 0; j < this.dimension; j++) {
				this.distanceMatrix[i][j] = (int) Math.round(Math.sqrt(Math.pow(this.matrix[i][0] - this.matrix[j][0], 2) + Math.pow(this.matrix[i][1] - this.matrix[j][1], 2)));
			}
		}
	}

	/**
	 * Returns the distance between two nodes in absolute values
	 * Be careful in using indexes starting from 0.
	 * For performance reasons bounds aren't checked so submitting invalid values will result in
	 * an index out of bounds exception.
	 *
	 * @param nodeIndexFrom
	 * @param nodeIndexTo
	 * @return distance
	 */
	public int getAbsDistance(final int nodeIndexFrom, final int nodeIndexTo) {
		return this.distanceMatrix[nodeIndexFrom][nodeIndexTo];
	}

	public int[][] getDistanceMatrix() {
		return this.distanceMatrix;
	}

	public double[][] getMatrix() {
		return this.matrix;
	}

	public int getSize() {
		return this.dimension;
	}

	public double computeOptimality(final int distance) {
		return distance / (double) this.bestKnown;
	}

	public float getBestKnown() {
		return this.bestKnown;
	}

	public double getY(final int i) {
		return this.matrix[i][1];
	}

	public double getX(final int i) {
		return this.matrix[i][0];
	}
}
