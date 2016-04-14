package ch.supsi.dti.algo.cup.niko;

import java.util.HashMap;
import java.util.Map;

public class Path
{
	private final int[] path;
	private int index = 0;
	private final int lengthMax;
	private int distance;
	private TSP structure;
	private boolean changed = false;

	public Path(TSP structure)
	{
		this.structure = structure;
		this.lengthMax = structure.getSize();
		this.path = new int[structure.getSize()];
	}

	public void addNode(int node)
	{
		this.path[this.index++] = node;
	}

	public int length()
	{
		return this.lengthMax;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}

	public int getDistance()
	{
		// if (this.changed)
		// {
		computeDistance();
		this.changed = false;
		// }
		return this.distance;
	}

	private void computeDistance()
	{
		this.distance = 0;
		for (int i = 0; i < this.lengthMax; i++)
		{
			double adddist = this.structure.getAbsDistance(this.path[i], this.path[(i + 1) % (this.lengthMax)]);
			// System.out.println(adddist);
			this.distance += adddist;
			// System.out.println(this.distance);
		}
		int i = 0;
		i = i + 1;
	}

	public float getPerformance()
	{
		return (this.distance - this.structure.getBestKnown()) / this.structure.getBestKnown();
	}

	public int getNode(int index)
	{
		return this.path[index];
	}

	public void setNode(int k, int node)
	{
		this.path[k] = node;
		this.changed = true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.path.length; i++)
		{
			sb.append("[" + i + "-" + this.path[i] + "]");
		}
		return sb.toString();
	}

	public boolean validate()
	{
		Map<Integer, Boolean> checked = new HashMap<>();
		for (int element : this.path)
		{
			if (checked.containsKey(element))
				return false;
			checked.put(element, true);
		}
		return checked.size() == this.path.length;
	}
}
