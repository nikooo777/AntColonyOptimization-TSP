package ch.supsi.dti.algo.cup.niko;

import java.util.HashMap;
import java.util.Map;

public class Tour
{
	private final int[] tour;
	private int index = 0;
	private final int TSPSize;
	private int distance;
	private final TSP structure;
	private boolean changed = false;

	public Tour(final TSP structure)
	{
		this.structure = structure;
		this.TSPSize = structure.getSize();
		this.tour = new int[structure.getSize()];
	}

	public void addNode(final int node)
	{
		// System.out.print("-" + node);
		this.tour[this.index++] = node;
		this.changed = true;
	}

	/**
	 * returns the number of cities in the TSP problem
	 * 
	 * @return size
	 */
	public int tourSize()
	{
		return this.TSPSize;
	}

	/**
	 * the length of the whole solution (calling this might be expensive if the solution was modified)
	 * 
	 * @return solutionLength
	 */
	public int getTourLength()
	{
		// TODO: add distance when adding nodes with addNode
		if (this.changed)
		{
			computeDistance();
			this.changed = false;
		}
		return this.distance;
	}

	private void computeDistance()
	{
		this.distance = 0;
		for (int i = 0; i < this.TSPSize; i++)
		{
			// final double adddist = this.structure.getAbsDistance(this.tour[i], this.tour[(i + 1) % (this.TSPSize)]);
			this.distance += this.structure.getAbsDistance(this.tour[i], this.tour[(i + 1) % (this.TSPSize)]);
		}
	}

	public float getPerformance()
	{
		if (this.changed)
			computeDistance();
		return (this.distance - this.structure.getBestKnown()) / this.structure.getBestKnown();
	}

	public int getNode(final int index)
	{
		return this.tour[index];
	}

	public void setNode(final int k, final int node)
	{
		this.tour[k] = node;
		this.changed = true;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.tour.length; i++)
		{
			sb.append("[" + i + "-" + this.tour[i] + "]");
		}
		return sb.toString();
	}

	public boolean validate()
	{
		final Map<Integer, Boolean> checked = new HashMap<>();
		for (final int element : this.tour)
		{
			if (checked.containsKey(element))
			{
				System.err.println("A duplicate node was found! " + element);
				return false;
			}
			checked.put(element, true);
		}
		if (checked.size() == this.tour.length)
			return true;
		else
			System.err.println("The length of the solution doesn't match the length of the problem! " + checked.size());
		return false;
		// return checked.size() == this.tour.length;
	}

	public int[] getSolution()
	{
		return this.tour;
	}

	public void swap(int i, int j)
	{
		// System.out.println("swapping between " + i + " and " + j);
		// System.out.println(this.tour);
		// System.out.println("distance before swap: " + this.tour.getDistance());
		// 1. take route[0] to route[i-1] and add them in order to new_route
		// int tourSize = this.structure.getSize();
		// Tour newTour = new Tour(this.structure);
		// for (int k = 0; k <= i; k++)
		// {
		// newTour.addNode(this.tour.getNode(k));
		// }
		// 2. take route[i] to route[k] and add them in reverse order to new_route
		// System.out.println("pre " + Arrays.toString(this.tour));
		int dec = 0;
		int supportNode;
		for (int k = i + 1; k <= j; k++)
		{
			if (k >= j - dec)
				break;
			supportNode = this.tour[k];
			this.tour[k] = this.tour[j - dec];
			this.tour[j - dec] = supportNode;
			dec++;
		}
		this.changed = true;
		// 3. take route[k+1] to end and add them in order to new_route
		// for (int k = j + 1; k < tourSize; k++)
		// {
		// newTour.addNode(this.tour.getNode(k));
		// }
		// System.out.println("new distance after swap: " + newTour.getDistance());
		// System.out.println(newTour);
		// return newTour;
	}

	public int getIndexOfNode(int candidate)
	{
		for (int i = 0; i < this.TSPSize; i++)
			if (this.tour[i] == candidate)
				return i;
		System.err.println(candidate);
		throw new RuntimeException("Something is wrong in the solution");
	}
}
