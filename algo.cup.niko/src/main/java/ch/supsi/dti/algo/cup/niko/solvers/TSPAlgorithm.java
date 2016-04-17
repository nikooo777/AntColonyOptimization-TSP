package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Random;

import ch.supsi.dti.algo.cup.niko.Tour;
import ch.supsi.dti.algo.cup.niko.TSP;

public interface TSPAlgorithm
{
	public Tour reduce(TSP structure, Random random);
}
