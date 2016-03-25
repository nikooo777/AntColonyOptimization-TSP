package ch.supsi.dti.algo.cup.niko;

import java.util.Iterator;
import java.util.LinkedList;

public class Path implements Iterable<Integer>
{
	private final LinkedList<Integer> path;

	public Path(int lengthMax)
	{
		this.path = new LinkedList<>();
	}

	public void addNode(int node)
	{
		this.path.add(node);
	}

	public int length()
	{
		return this.path.size();
	}

	@Override
	public Iterator<Integer> iterator()
	{
		return this.path.iterator();
	}
}
