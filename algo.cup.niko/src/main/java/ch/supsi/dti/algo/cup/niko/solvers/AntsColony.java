package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Arrays;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.Tour;

public class AntsColony implements TSPAlgorithm
{
	private static final int ANTS_POPULATION = 15;
	private static final double GREEDYNESS = 0.999; // 95% greedy 5% explorer
	private int[] antPosition;
	private boolean[][] visitedNode;
	private double[][] pheromone;
	private double defaultPheromone;
	private static final double ALPHA = 0.1;
	private static final double RHO = 0.1; // Pheromone persistance
	private Tour bestAntEver = null;
	private Tour tour;
	private TSP structure;
	private Random random;
	private Tour[] antTour;
	private boolean useCandidates;
	private int iterations = 0;

	public AntsColony(Tour pathToImprove, boolean useCandidates)
	{
		this.tour = pathToImprove;
		this.useCandidates = useCandidates;
		this.antPosition = new int[ANTS_POPULATION];
		// booleans are automatically initialized to false
		this.visitedNode = new boolean[ANTS_POPULATION][pathToImprove.tourSize()];
		this.pheromone = new double[pathToImprove.tourSize()][pathToImprove.tourSize()];
		this.antTour = new Tour[ANTS_POPULATION];
	}

	@Override
	public Tour reduce(TSP structure, Random random)
	{
		long startTime = System.currentTimeMillis();
		System.out.println("---------------------------------");
		this.structure = structure;
		this.random = random;

		this.defaultPheromone = computeDefaultpheromone();
		// initial pheromone values are the same
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			Arrays.fill(this.pheromone[i], this.defaultPheromone);
		}

		while (System.currentTimeMillis() - startTime < 170000)
		{
			int[] amountVisited = new int[structure.getSize()];
			// position ants in a random place on the tour
			// also initialize a tour for each ant
			for (int i = 0; i < ANTS_POPULATION; i++)
			{
				// reset ant's memory if it's not the first tour
				if (this.iterations > 0)
					resetEEPROM(i);

				this.antTour[i] = new Tour(structure);
				if (this.iterations == 0)
				{
					// TODO: verify that only 1 ant can be in 1 node
					int randval = random.nextInt(this.tour.tourSize());
					this.antTour[i].addNode(setAntPosition(i, randval));
				} else
				{
					// memAnt
					int randI = random.nextInt(this.tour.tourSize());
					// store 7% of the best ant already
					int randJ = randI + random.nextInt(this.tour.tourSize() - randI);
					System.out.println(randI + " and " + randJ);
					// for (int j = randI; j < randJ; j++)
					for (int j = randI; j < randJ; j++)
					{
						int candidateNode = this.bestAntEver.getNode(j);
						this.antTour[i].addNode(setAntPosition(i, candidateNode));
						amountVisited[i]++;
						// this.antTour[i].addNode(candidateNode);
						// this.visitedNode[i][candidateNode] = true;
						// this.antPosition[i] = candidateNode;
					}
				}
			}

			// let the ants take n steps so to cycle the whole tour
			for (int i = 0; i < this.tour.tourSize() - 1; i++)
			{
				for (int j = 0; j < ANTS_POPULATION; j++)
				{
					if (i >= (this.tour.tourSize() - 1) - (amountVisited[j] - 1))
						continue;

					this.antTour[j].addNode(nextStep(j));
				}
			}

			// identify the best performing ant
			int bestAnt = -1;
			int tourLength = Integer.MAX_VALUE;
			for (int j = 0; j < ANTS_POPULATION; j++)
			{
				// perform a partial 2opt (only with candidate lists) on each tour
				this.antTour[j] = new TwoOpt(this.antTour[j], false, true).reduce(structure, random);
				// find the best tour
				if (this.antTour[j].getTourLength() < tourLength)
				{
					// System.out.println("after ants opt " + this.antTour[j].getPerformance() * 100);
					// long starttime = System.currentTimeMillis();
					// TODO: try on the best ant only
					// System.out.println("2opt took: " + (System.currentTimeMillis() - starttime) + "ms");
					tourLength = this.antTour[j].getTourLength();
					bestAnt = j;
				}
			}
			// run a full 2-opt on the best ant
			this.antTour[bestAnt] = new TwoOpt(this.antTour[bestAnt], true, true).reduce(structure, random);

			// update the best ant ever if necessary
			if (this.bestAntEver == null || tourLength < this.bestAntEver.getTourLength())
			{
				this.bestAntEver = this.antTour[bestAnt];
				System.out.println("\tAnts progress (" + this.iterations + "): " + this.bestAntEver.getPerformance() * 100 + "%");
				if (this.bestAntEver.getTourLength() <= structure.getBestKnown())
				{
					System.out.println("Best known found in: " + (System.currentTimeMillis() - startTime + "ms"));
					return this.bestAntEver;
				}
			}
			// System.out.println("#Ants progress: " + this.antTour[bestAnt].getPerformance() * 100 + "%" + " best ever: " + this.bestAntEver.getPerformance() * 100 + "%");

			// let the best ant celebrate by throwing a pheromone-party all over the tour!!
			// AKA put extra pheromone on the winning tour
			updateGeneralPheromone(this.antTour[bestAnt]);
			this.iterations++;
		}
		// finally return the best ant ever
		return this.bestAntEver;
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
			this.antPosition[ant] = nextGreedy(ant, origin);

		} else
		{
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
		if (this.useCandidates)
		{
			// candidates version
			for (int i = 0; i < TSP.CANDIDATES_SIZE; i++)
			{
				int candidateNode = this.structure.getCandidates(currentNode)[i];
				if (this.visitedNode[ant][candidateNode] || candidateNode == currentNode)
					continue;
				if (computeMix(currentNode, candidateNode) > maxMixedVal)
				{
					maxMixedVal = computeMix(currentNode, candidateNode);
					bestNode = candidateNode;
				}
			}
			if (bestNode != -1)
				return bestNode;
		}

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
		// TODO: compute inversematrix on startup - done
		// System.out.println("pheromone: " + this.pheromone[origin][destination]);
		return getPheromone(origin, destination) * Math.pow(this.structure.getInverseAbsDistance(origin, destination), 2);
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
		int candidateAnts = 0;
		double maxMix = 0;
		int archWithMaxMix = -1;
		if (this.useCandidates)
		{
			// CANDIDATES VERSION
			double[] candidateTickets = new double[TSP.CANDIDATES_SIZE];

			for (int i = 0; i < TSP.CANDIDATES_SIZE; i++)
			{
				int candidateNode = this.structure.getCandidates(currentNode)[i];
				if (this.visitedNode[ant][candidateNode] || candidateNode == currentNode)
					continue;
				candidateTickets[i] = computeMix(currentNode, candidateNode);

				totalMix += candidateTickets[i];
				candidateAnts++;
				// if a maximum value is found, then it's updated
				if (candidateTickets[i] > maxMix)
				{
					// update the the max with the new vals
					archWithMaxMix = candidateNode;
					maxMix = candidateTickets[i];
				}
			}
			if (candidateAnts > 0)
			{
				if (candidateAnts == 1)
					return archWithMaxMix;
				// remove the best solution from the total value
				totalMix -= maxMix;
				double winningTicket = this.random.nextDouble();
				double lastTicketAmount = 0;

				// calculate the tickets
				for (int i = 0; i < TSP.CANDIDATES_SIZE; i++)
				{
					int candidateNode = this.structure.getCandidates(currentNode)[i];

					candidateTickets[i] = candidateTickets[i] / totalMix + lastTicketAmount;
					lastTicketAmount = candidateTickets[i];

					if (winningTicket <= candidateTickets[i])
					{
						// System.out.println("WInrar");
						return candidateNode;
					}
				}
			}
			// END CANDIDATES VERSION
			// System.out.println("WHAT NOOOO WAIT");
			totalMix = 0;
			candidateAnts = 0;
			maxMix = 0;
		}
		double[] tickets = new double[this.tour.tourSize()];
		// in this iteration we will store the total amount of mix
		// we will save the best candidate
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			// skip visited nodes and current node
			if (i == currentNode || this.visitedNode[ant][i])
				continue;

			tickets[i] = computeMix(currentNode, i);
			// System.out.println("magic mix: " + tickets[i]);
			totalMix += tickets[i];
			candidateAnts++;
			// System.out.println("total magic mix: " + totalMix);
			// if a maximum value is found, then it's updated
			if (tickets[i] > maxMix)
			{
				// update the the max with the new vals
				archWithMaxMix = i;
				maxMix = tickets[i];
			}
		}
		if (candidateAnts == 1)
			return archWithMaxMix;

		// remove the best solution from the total value
		totalMix -= maxMix;
		double winningTicket = this.random.nextDouble();// % totalMix;
		double lastTicketAmount = 0;

		// calculate the tickets
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			// skip visited nodes and current node and best node (we don't want that one)
			// if (this.visitedNode[ant][i] || i == currentNode || i == archWithMaxMix)
			// continue;

			tickets[i] = tickets[i] / totalMix + lastTicketAmount;
			lastTicketAmount = tickets[i];

			if (winningTicket <= tickets[i])
				return i;
		}

		// If we reach this point it means that the ONLY remaining node to visit is the best node. FML
		throw new RuntimeException("Something went wrong with the lottery! No Candidate won");
	}

	private void updateLocalPheromone(int origin, int destination)
	{
		// 1-persistenza feromone (rho) * Feromone fra nodi A e B (ovvero arco corrente) + DefaultPheromone
		setPheromone(origin, destination, (1. - RHO) * getPheromone(origin, destination) + this.defaultPheromone * RHO);
	}

	private void updateGeneralPheromone(Tour bestTour)
	{
		double globallyBestTour = 1. / this.bestAntEver.getTourLength();

		for (int i = 0; i < bestTour.tourSize(); i++)
		{
			int origin = bestTour.getNode(i);
			int destination = bestTour.getNode((i + 1) % bestTour.tourSize());
			// TODO: ask if A->B should be equal to B->A! - ATM yes
			setPheromone(origin, destination, (1. - ALPHA) * getPheromone(origin, destination) + ALPHA * globallyBestTour);
		}
	}

	private void setPheromone(final int origin, final int destination, final double amount)
	{
		// TODO: replace the two conditional IF with two sets, it should be less expensive
		this.pheromone[origin][destination] = amount;
		this.pheromone[destination][origin] = amount;
	}

	private double getPheromone(final int origin, final int destination)
	{
		return this.pheromone[origin][destination];
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
