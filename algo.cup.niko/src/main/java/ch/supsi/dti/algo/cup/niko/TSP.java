package ch.supsi.dti.algo.cup.niko;

/**
 * values are stored starting from index 0.
 * 
 * @author Niko
 * 
 */
public class TSP
{
	public enum Type
	{
		TSP, TOUR
	}

	private double[][] matrix;
	private int[][] distanceMatrix;
	private final String name;
	private final Type type;
	private final int dimension;
	private final int bestKnown;
	private final String comment;

	public TSP(String name, String comment, Type type, int dimension, int bestKnown, double[][] matrix)
	{
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.dimension = dimension;
		this.bestKnown = bestKnown;
		this.matrix = matrix;
		this.distanceMatrix = new int[this.dimension][this.dimension];
		computeDistances();
	}

	/**
	 * Computes the distance between nodes.
	 * Rounds the result to the nearest integer
	 */
	private void computeDistances()
	{
		for (int i = 0; i < this.dimension; i++)
		{
			for (int j = 0; j < this.dimension; j++)
			{
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
	public int getAbsDistance(int nodeIndexFrom, int nodeIndexTo)
	{
		return this.distanceMatrix[nodeIndexFrom][nodeIndexTo];
	}

	public int[][] getDistanceMatrix()
	{
		return this.distanceMatrix;
	}

	public double[][] getMatrix()
	{
		return this.matrix;
	}

	public int getSize()
	{
		return this.dimension;
	}

	public double computeOptimality(int distance)
	{
		return distance / (double) this.bestKnown;
	}

	public float getBestKnown()
	{
		return this.bestKnown;
	}
}
