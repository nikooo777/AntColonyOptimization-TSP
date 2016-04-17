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

	public int tourSize()
	{
		return this.TSPSize;
	}

	public int getTourLength()
	{
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
			final double adddist = this.structure.getAbsDistance(this.tour[i], this.tour[(i + 1) % (this.TSPSize)]);
			this.distance += adddist;
		}
	}

	public float getPerformance()
	{
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
				return false;
			checked.put(element, true);
		}
		return checked.size() == this.tour.length;
	}

	public int[] getSolution()
	{
		return this.tour;
	}

	public int getIndexOfNode(int candidate)
	{
		for (int i = 0; i < this.TSPSize; i++)
			if (this.tour[i] == candidate)
				return i;
		throw new RuntimeException("Something is wrong in the solution");
	}
}
