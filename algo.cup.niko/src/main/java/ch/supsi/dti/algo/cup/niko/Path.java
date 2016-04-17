package ch.supsi.dti.algo.cup.niko;

import java.util.HashMap;
import java.util.Map;

public class Path
{
	private final int[] path;
	private int index = 0;
	private final int lengthMax;
	private int distance;
	private final TSP structure;
	private boolean changed = false;

	public Path(final TSP structure)
	{
		this.structure = structure;
		this.lengthMax = structure.getSize();
		this.path = new int[structure.getSize()];
	}

	public void addNode(final int node)
	{
		// System.out.print("-" + node);
		this.path[this.index++] = node;
		this.changed = true;
	}

	public int length()
	{
		return this.lengthMax;
	}

	public int getDistance()
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
		for (int i = 0; i < this.lengthMax; i++)
		{
			final double adddist = this.structure.getAbsDistance(this.path[i], this.path[(i + 1) % (this.lengthMax)]);
			this.distance += adddist;
		}
	}

	public float getPerformance()
	{
		return (this.distance - this.structure.getBestKnown()) / this.structure.getBestKnown();
	}

	public int getNode(final int index)
	{
		return this.path[index];
	}

	public void setNode(final int k, final int node)
	{
		this.path[k] = node;
		this.changed = true;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.path.length; i++)
		{
			sb.append("[" + i + "-" + this.path[i] + "]");
		}
		return sb.toString();
	}

	public boolean validate()
	{
		final Map<Integer, Boolean> checked = new HashMap<>();
		for (final int element : this.path)
		{
			if (checked.containsKey(element))
				return false;
			checked.put(element, true);
		}
		return checked.size() == this.path.length;
	}

	public int[] getSolution()
	{
		return this.path;
	}

	public int getIndexOfNode(int candidate)
	{
		for (int i = 0; i < this.lengthMax; i++)
			if (this.path[i] == candidate)
				return i;
		throw new RuntimeException("Something is wrong in the solution");
	}
}
