package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Arrays;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.Tour;

public class oldAntColony implements TSPAlgorithm
{
	private static final int ANTS_POPULATION = 10;
	private static final double GREEDYNESS = 0.88; // 95% greedy 5% explorer
	private int[] antPosition;
	private boolean[][] visitedNode;
	private double[][] pheromone;
	private double defaultPheromone;
	private static final double ALPHA = 0.1;
	private static final double RHO = 0.1; // Pheromone persistance

	private Tour tour;
	private TSP structure;
	private Random random;
	private Tour[] antTour;

	public oldAntColony(Tour pathToImprove)
	{
		this.tour = pathToImprove;
		this.antPosition = new int[ANTS_POPULATION];
		// booleans are automatically initialized to false
		this.visitedNode = new boolean[ANTS_POPULATION][pathToImprove.tourSize()];
		this.pheromone = new double[pathToImprove.tourSize()][pathToImprove.tourSize()];
		this.antTour = new Tour[ANTS_POPULATION];
	}

	@Override
	public Tour reduce(TSP structure, Random random)
	{
		this.structure = structure;
		this.random = random;
		Tour bestAntEver = null;
		this.defaultPheromone = computeDefaultpheromone();
		// initial pheromone values are the same
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			Arrays.fill(this.pheromone[i], this.defaultPheromone);
		}

		for (int niko = 0; niko < 20; niko++)
		{
			// positione ants in a random place on the tour
			// also initialize a tour for each ant
			for (int i = 0; i < ANTS_POPULATION; i++)
			{
				this.antTour[i] = new Tour(structure);
				int randval = random.nextInt(this.tour.tourSize());
				this.antTour[i].addNode(setAntPosition(i, randval));
			}

			// let the ants take n steps so to cycle the whole tour
			for (int i = 0; i < this.tour.tourSize() - 1; i++)
			{
				for (int j = 0; j < ANTS_POPULATION; j++)
				{
					this.antTour[j].addNode(nextStep(j));
				}
			}

			// identify the best performing ant
			int bestAnt = -1;
			double bestPerformance = Double.MAX_VALUE;
			for (int j = 0; j < ANTS_POPULATION; j++)
			{
				if (this.antTour[j].getPerformance() < bestPerformance)
				{
					this.antTour[j] = new TwoOpt(this.antTour[j]).reduce(structure, random);
					bestPerformance = this.antTour[j].getPerformance();
					bestAnt = j;
				}
				resetEEPROM(j);
			}

			// let the best ant celebrate by throwing a pheromone-party all over the tour!!
			// AKA put extra pheromone on the winning tour
			updateGeneralPheromone(this.antTour[bestAnt]);
			// this.tour = this.antTour[bestAnt];
			if (bestAntEver == null || bestAntEver.getTourLength() > this.antTour[bestAnt].getTourLength())
			{
				bestAntEver = this.antTour[bestAnt];
				// System.out.println("Ants progress: " + bestAntEver.getPerformance() * 100 + "%");
			}
		}

		// finally return the best ant ever
		return bestAntEver;
	}

	/**
	 * Given an ant a next step is processed.
	 * A step could result in a exploration or exploitation
	 * The node visted is marked as well
	 * 
	 * @param ant
	 */
	private int nextStep(int ant)
	{
		boolean isGreedy = isAntGreedy();
		int origin = this.antPosition[ant];
		if (isGreedy)
		{
			System.out.println("I'm greedy");
			this.antPosition[ant] = nextGreedy(ant, origin);

		} else
		{
			System.out.println("I explore");
			this.antPosition[ant] = nextExploration(ant, origin);
		}

		// mark node as visited
		// System.out.println("ant: " + ant + " position: " + this.antPosition[ant]);
		this.visitedNode[ant][this.antPosition[ant]] = true;
		updateLocalPheromone(origin, this.antPosition[ant]);
		return this.antPosition[ant];
	}

	private void resetEEPROM(int ant)
	{
		Arrays.fill(this.visitedNode[ant], false);
	}

	/**
	 * Based on the amount of pheromone and the inverse of the distance, this algorithm returns the best target node
	 * TODO: currentNode could be extracted from ant alone
	 * 
	 * @param currentNode
	 * @return bestNextNode
	 */
	private int nextGreedy(int ant, int currentNode)
	{
		double maxMixedVal = 0;
		int bestNode = -1;
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			if (this.visitedNode[ant][i] || i == currentNode)
				continue;
			if (computeMix(currentNode, i) > maxMixedVal)
			{
				maxMixedVal = computeMix(currentNode, i);
				bestNode = i;
			}
		}
		if (bestNode != -1)
			return bestNode;
		else
			throw new RuntimeException("No more nodes to explore");
	}

	private double computeMix(int origin, int destination)
	{
		// TODO: compute inversematrix on startup
		// System.out.println("pheromone: " + this.pheromone[origin][destination]);
		return this.pheromone[origin][destination] * 1. / this.structure.getAbsDistance(origin, destination);
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
		// tutti gli archi vengono assegnati con dei tickets (più feromone * inv dist = più tickets)
		// un vincitore viene estratto (quello con più feromone è escluso)
		// TODO: usare qui le candidates lists
		// l'arco pescato sarà quello che porta al nodo successivo!
		double totalMix = 0;
		// node - mixval
		double[] maxMix = new double[2];
		double[] secondMaxMix = new double[2];
		double[] tickets = new double[this.tour.tourSize()];

		// in this iteration we will store the total amount of mix
		// we will save the best candidate
		// we will save the second best candidate
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			// skip visited nodes and current node
			if (i == currentNode || this.visitedNode[ant][i])
				continue;

			tickets[i] = computeMix(currentNode, i);
			totalMix += tickets[i];

			// if a maximum value is found, then it's updated
			if (maxMix[1] < tickets[i])
			{
				// copy the best to the second best
				secondMaxMix = maxMix;
				maxMix[0] = i;
				maxMix[1] = tickets[i];
			} else if (secondMaxMix[1] < tickets[i])
			{
				secondMaxMix[0] = i;
				secondMaxMix[1] = tickets[i];
			}
		}

		// remove the best solution from the total value
		totalMix -= maxMix[1];
		System.out.println(totalMix);
		double winningTicket = this.random.nextDouble();
		int lastChecked = 0;
		// calculate the tickets
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			// skip visited nodes and current node
			if (i == currentNode || this.visitedNode[ant][i])
				continue;

			// if (i != 0)
			tickets[i] = tickets[i] / totalMix + tickets[lastChecked];
			// else
			// tickets[i] = tickets[i] / totalMix;
			lastChecked = i;
			if (winningTicket <= tickets[i])
				return i;
		}
		throw new RuntimeException("Something went wrong with the lottery! No Candidate won");
	}

	private void updateLocalPheromone(int origin, int destination)
	{
		// 1-persistenza feromone (rho) * Feromone fra nodi A e B (ovvero arco corrente) + DefaultPheromone
		this.pheromone[origin][destination] = (1. - RHO) * this.pheromone[origin][destination] + this.defaultPheromone * RHO;
	}

	private void updateGeneralPheromone(Tour bestTour)
	{
		for (int i = 0; i < bestTour.tourSize(); i++)
		{
			this.pheromone[i][(i + 1) % this.tour.tourSize()] = (1. - ALPHA) * this.pheromone[i][(i + 1) % this.tour.tourSize()] + this.defaultPheromone * ALPHA;
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

	/**
	 * On success, the position is returned back again
	 * 
	 * @param antNr
	 * @param position
	 * @return position
	 */
	private int setAntPosition(int antNr, int position)
	{
		// System.out.println("ant nr: " + antNr + " initial pos: " + position);
		if (!this.visitedNode[antNr][position])
		{
			this.antPosition[antNr] = position;
			this.visitedNode[antNr][position] = true;
			return position;
		} else
			throw new RuntimeException("This node has already been visited by this ant");
	}
}
