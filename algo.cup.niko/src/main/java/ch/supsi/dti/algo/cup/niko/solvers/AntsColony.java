package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Arrays;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.Tour;

public class AntsColony implements TSPAlgorithm
{
	private static final int ANTS_POPULATION = 10;
	private static final double GREEDYNESS = 0.95; // 95% greedy 5% explorer
	private int[] antPosition;
	private boolean[][] visitedNode;
	private double[][] pheromone;
	private double defaultPheromone;
	private static final double ALPHA = 0.1;
	private static final double RHO = 0.9; // Pheromone persistance

	private Tour tour;
	private TSP structure;
	private Random random;

	public AntsColony(Tour pathToImprove)
	{
		this.tour = pathToImprove;
		this.antPosition = new int[ANTS_POPULATION];
		this.defaultPheromone = computeDefaultpheromone();
		// booleans are automatically initialized to false
		this.visitedNode = new boolean[ANTS_POPULATION][pathToImprove.tourSize()];
		this.pheromone = new double[pathToImprove.tourSize()][pathToImprove.tourSize()];
		// initial TAU values are the same
		for (int i = 0; i < pathToImprove.tourSize(); i++)
		{
			Arrays.fill(this.pheromone[i], this.defaultPheromone);
		}

	}

	@Override
	public Tour reduce(TSP structure, Random random)
	{
		this.structure = structure;
		this.random = random;
		for (int i = 0; i < ANTS_POPULATION; i++)
		{
			setAntPosition(i, random.nextInt(this.tour.tourSize()));
		}

		return null;
	}

	/**
	 * Given an ant a next step is processed.
	 * A step could result in a exploration or exploitation
	 * The node visted is marked as well
	 * 
	 * @param ant
	 */
	private void nextStep(int ant)
	{
		boolean isGreedy = isAntGreedy();
		if (isGreedy)
		{
			this.antPosition[ant] = nextGreedy(ant, this.antPosition[ant]);

		} else
			this.antPosition[ant] = nextExploration(ant, this.antPosition[ant]);

		// mark node as visited
		this.visitedNode[ant][this.antPosition[ant]] = true;
	}

	/**
	 * Based on the amount of pheromone, this algorithm returns the best target node
	 * TODO: currentNode could be extracted from ant alone
	 * 
	 * @param currentNode
	 * @return bestNextNode
	 */
	private int nextGreedy(int ant, int currentNode)
	{
		double maxPheromone = 0;
		int bestNode = -1;
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			if (this.visitedNode[ant][i])
				continue;
			if (this.pheromone[currentNode][i] > maxPheromone)
			{
				maxPheromone = this.pheromone[currentNode][i];
				bestNode = i;
			}
		}
		return bestNode;
	}

	/**
	 * TODO: currentNode could be extracted from ant alone
	 * 
	 * @param ant
	 * @param currentNode
	 * @return nextNodeToExplore
	 */
	private int nextExploration(int ant, int currentNode)
	{
		// tutti gli archi vengono assegnati con dei tickets (più feromone = più tickets)
		// un vincitore viene estratto (quello con più feromone è escluso)
		// TODO: usare qui le candidates lists
		// l'arco pescato sarà quello che porta al nodo successivo!
		double maxPheromone = 0;
		int bestNode = -1;
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			if (this.pheromone[currentNode][i] > maxPheromone)
			{
				maxPheromone = this.pheromone[currentNode][i];
				bestNode = i;
			}
		}
		return bestNode;
	}

	private void updateLocalPheromone(int origin, int destination)
	{
		// 1-persistenza feromone (rho) * Feromone fra nodi A e B (ovvero arco corrente) + DefaultPheromone
		this.pheromone[origin][destination] = (1. - RHO) * this.pheromone[origin][destination] + this.defaultPheromone;
	}

	private void updateGeneralPheromone(Tour bestTour)
	{
		for (int i = 0; i < bestTour.tourSize(); i++)
		{
			this.pheromone[i][(i + 1) % this.tour.tourSize()] = (1. - ALPHA) * this.pheromone[i][(i + 1) % this.tour.tourSize()] + this.defaultPheromone;
		}
		// per ogni arco percorso dalla formica migliore fare:
		// 1-alpha *Feromone fra nodi A e B (ovvero arco corrente)+ DefaultPheromone
	}

	/**
	 * true if greedy
	 * false if explorer
	 * 
	 * @return greedy
	 */
	private boolean isAntGreedy()
	{
		double ticket = this.random.nextDouble();
		return ticket < GREEDYNESS;
	}

	private double computeDefaultpheromone()
	{
		return 1. / (this.tour.getTourLength() * this.structure.getSize());
	}

	private void setAntPosition(int antNr, int position)
	{
		if (!this.visitedNode[antNr][position])
			this.antPosition[antNr] = position;
		else
			throw new RuntimeException("This node has already been visited by this ant");
	}
}
