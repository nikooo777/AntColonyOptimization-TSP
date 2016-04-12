package ch.supsi.dti.algo.cup.niko;

public class Path
{
	private final int[] path;
	private int index = 0;
	private final int lengthMax;
	private int distance;
	private TSP structure;

	public Path(TSP structure)
	{
		this.structure = structure;
		// +1 due to the tour being closed
		this.lengthMax = structure.getSize() + 1;
		this.path = new int[structure.getSize() + 1];
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
		return this.distance;
	}

	public float getPerformance()
	{
		return (this.distance - this.structure.getBestKnown()) / this.structure.getBestKnown();
	}
}
