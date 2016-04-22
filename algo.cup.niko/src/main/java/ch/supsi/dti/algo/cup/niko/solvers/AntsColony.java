package ch.supsi.dti.algo.cup.niko.solvers;

import java.util.Arrays;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.CupLauncher;
import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.Tour;

public class AntsColony implements TSPAlgorithm
{
	// private static final int ANTS_POPULATION = 17;
	// private static final double GREEDYNESS = 0.95;
	// private static final double MEMORYNESS = 0.70;
	// private static final double BETA = 1;
	// private static final double RHO = 0.1;
	// private static final double ALPHA = 0.1;
	// for hard problems
	private static final int ANTS_POPULATION = 30;
	private static final double GREEDYNESS = 0.96;
	private static final double MEMORYNESS = 0.65;
	private static final double BETA = 2;
	private static final double RHO = 0.1;
	private static final double ALPHA = 0.1;

	private Tour tour;
	private boolean useCandidates;
	private int[] antPosition;
	private boolean[][] visitedNode;
	private double[][] pheromone;
	private Tour[] antTour;
	private TSP structure;
	private Random random;
	private double defaultPheromone;
	private int iterations = 0;
	private Tour bestAntEver = null;

	///////////////////// WRITE SUPPORT/////////////////////////
	public String getParams()
	{
		return "population: " + ANTS_POPULATION + " Greedyness: " + GREEDYNESS + " Memoryness: " + MEMORYNESS + " Beta: " + BETA + " Rho: " + RHO + " Alpha: " + ALPHA;
	}

	///////////////////// END WRITE SUPPORT/////////////////////////

	public AntsColony(Tour tourToImprove, boolean useCandidates)
	{
		this.tour = tourToImprove;
		this.useCandidates = useCandidates;
		this.antPosition = new int[ANTS_POPULATION];
		// booleans are automatically initialized to false
		this.visitedNode = new boolean[ANTS_POPULATION][tourToImprove.tourSize()];
		this.pheromone = new double[tourToImprove.tourSize()][tourToImprove.tourSize()];
		this.antTour = new Tour[ANTS_POPULATION];
	}

	@Override
	public Tour reduce(TSP structure, Random random)
	{
		// memorize the start time
		long startTime = System.currentTimeMillis();
		System.out.println("---------------------------------");
		this.structure = structure;
		this.random = random;

		// one time only - compute the base-pheromone
		this.defaultPheromone = computeDefaultpheromone();

		// initial pheromone values must be the same and equal to the default pheromone val
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			Arrays.fill(this.pheromone[i], this.defaultPheromone);
		}

		// while there is time left for the algorithm to run -> 60*3*1000
		while (!isTimeOver())
		{
			// since we run memory ants we need to know how many cities an ant remembers
			int[] initialCitiesVisitedByAnt = new int[structure.getSize()];

			// position the ants randomly on the tour OR give them some memory from the best ant ever
			initAnts(initialCitiesVisitedByAnt);

			// let the ants take n steps so to cycle the whole tour
			// we subtract 1 as the ant has been placed on one city already
			for (int i = 0; i < this.structure.getSize() - 1; i++)
			{
				for (int j = 0; j < ANTS_POPULATION; j++)
				{
					// if the ant has memory then we don't want them to take more steps!
					if (i >= (this.structure.getSize() - 1) - (initialCitiesVisitedByAnt[j] - 1))
						continue;

					// let the ant take the next step
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
					tourLength = this.antTour[j].getTourLength();
					bestAnt = j;
				}
			}

			// run a full 2-opt on the best ant and store again the update value of the tour length
			// this.antTour[bestAnt] = new TwoOpt(this.antTour[bestAnt], false, true).reduce(structure, random);
			tourLength = this.antTour[bestAnt].getTourLength();

			// if the local best ant is better than the globally best ant OR if there is no local best and AND there is still time THEN update it
			if (this.bestAntEver == null || ((tourLength < this.bestAntEver.getTourLength()) && !isTimeOver()))
			{
				this.bestAntEver = this.antTour[bestAnt];

				System.out.println("\tAnts progress (" + this.iterations + "): " + this.bestAntEver.getPerformance() * 100 + "%");

				// if this is the optimal solution then stop the algorithm
				if (tourLength <= structure.getBestKnown())
				{
					System.out.println("Best known found in: " + (System.currentTimeMillis() - startTime + "ms"));
					return this.bestAntEver;
				}
			}

			// print the best local ant
			// System.out.println("#Ants progress: " + this.antTour[bestAnt].getPerformance() * 100 + "%" + " best ever: " + this.bestAntEver.getPerformance() * 100 + "%");

			// let the best ant celebrate by throwing a pheromone-party all over the tour!!
			// AKA put extra pheromone on the winning tour
			updateGeneralPheromone(this.antTour[bestAnt]);

			// increment the iterations
			this.iterations++;
		}

		// finally return the best ant ever
		return this.bestAntEver;
	}

	/**
	 * place pheromone all over a given tour.
	 * The amount of pheromone placed is based on the length of the best tour ever so far
	 * 
	 * @param tour
	 */
	private void updateGeneralPheromone(Tour tour)
	{
		double globallyBestTourInverse = 1. / this.bestAntEver.getTourLength();

		// go through all the nodes
		for (int i = 0; i < tour.tourSize(); i++)
		{
			int origin = tour.getNode(i);
			int destination = tour.getNode((i + 1) % tour.tourSize());

			setPheromone(origin, destination, (1. - ALPHA) * getPheromone(origin, destination) + ALPHA * globallyBestTourInverse);
		}
	}

	/**
	 * true if the time is over
	 * false if otherwise
	 * 
	 * @return boolean
	 */
	private boolean isTimeOver()
	{
		return (System.currentTimeMillis() - CupLauncher.overallstarttime) > 180_000;
	}

	/**
	 * Given an ant, a next step is processed.
	 * A step could result in a exploration or exploitation
	 * The node is marked as visited and the local pheromone is updated as well
	 * 
	 * @param ant
	 */
	private int nextStep(int ant)
	{
		int origin = this.antPosition[ant];
		if (isAntGreedy())
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

	/**
	 * 
	 * Each time an ant passes on an arch, we want to make some pehromone evaporate
	 * 
	 * @param origin
	 * @param destination
	 */
	private void updateLocalPheromone(int origin, int destination)
	{
		setPheromone(origin, destination, (1. - RHO) * getPheromone(origin, destination) + this.defaultPheromone * RHO);
	}

	/**
	 * Set the new pehromone value in the matrix
	 * 
	 * @param origin
	 * @param destination
	 * @param amount
	 */
	private void setPheromone(final int origin, final int destination, final double amount)
	{
		// as the pheromone from A-B should be equal to B-A, we store it in both cells
		this.pheromone[origin][destination] = amount;
		this.pheromone[destination][origin] = amount;
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

	/**
	 * All arches are assigned with a ticket (more mix one arch has, more tickets)
	 * A winner is drawn and becomes the next node to explore
	 * 
	 * @param ant
	 * @param currentNode
	 * @return nextNodeToExplore
	 */
	private int nextExploration(int ant, int currentNode)
	{
		double totalMix = 0;
		int candidateAnts = 0;
		double maxMix = 0;
		int archWithMaxMix = -1;

		if (this.useCandidates)
		{
			// we don't need many tickets and we don't want to reset them later on, so let's create our own array
			double[] candidateTickets = new double[this.structure.CANDIDATES_SIZE];

			// find the candidate with the best mix
			for (int i = 0; i < this.structure.CANDIDATES_SIZE; i++)
			{
				int candidateNode = this.structure.getCandidates(currentNode)[i];

				// don't visit nodes that can't be visited anymore
				if (this.visitedNode[ant][candidateNode] || candidateNode == currentNode)
					continue;

				// the amount of mix is stored in the tickets array as NUMERATOR! (it will be later divided by the total mix value)
				candidateTickets[i] = computeMix(currentNode, candidateNode);

				// store the total mix value
				totalMix += candidateTickets[i];

				// if a maximum value is found, then it's updated
				if (candidateTickets[i] > maxMix)
				{
					// update the the max with the new vals
					maxMix = candidateTickets[i];
					archWithMaxMix = candidateNode;
				}
				candidateAnts++;
			}
			// if at least 1 candidate is visitable then we draw the winner
			if (candidateAnts > 0)
			{
				// if there is only 1 candidate then don't even bother and return it already
				if (candidateAnts == 1)
					return archWithMaxMix;

				// remove the best solution from the pool of possible winners
				totalMix -= maxMix;
				double winningTicket = this.random.nextDouble();
				double lastTickets = 0;

				// calculate the tickets for each candidate
				for (int i = 0; i < this.structure.CANDIDATES_SIZE; i++)
				{
					int candidateNode = this.structure.getCandidates(currentNode)[i];

					// take the stored value as numerator and divide by the total. Then add the previous tickets so that we get an incremental value from 0 to 1
					lastTickets = (candidateTickets[i] / totalMix) + lastTickets;

					// if the ticket is less than the amount of tickets this candidate has, then this one is the winner!
					if (winningTicket <= lastTickets)
					{
						return candidateNode;
					}
				}
				// END CANDIDATES VERSION
			}

			// at this point the candidates lists failed and we have to fallback to the other method
			// reset some stuff
			totalMix = 0;
			candidateAnts = 0;
			maxMix = 0;
		}

		// spawn n amount of tickets
		double[] tickets = new double[this.tour.tourSize()];

		// in this iteration we will store the total amount of mix
		// we will save the best candidate
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			// skip visited nodes and current node
			if (i == currentNode || this.visitedNode[ant][i])
				continue;

			// the amount of mix is stored in the tickets array as NUMERATOR! (it will be later divided by the total mix value)
			tickets[i] = computeMix(currentNode, i);

			// store the total mix value
			totalMix += tickets[i];

			// if a maximum value is found, then it's updated
			if (tickets[i] > maxMix)
			{
				// update the the max with the new vals
				maxMix = tickets[i];
				archWithMaxMix = i;
			}
			candidateAnts++;
		}
		// if there is only 1 candidate then this is the city we want to visit
		if (candidateAnts == 1)
			return archWithMaxMix;

		// remove the best solution from the pool of possible winners
		totalMix -= maxMix;
		double winningTicket = this.random.nextDouble();
		double lastTickets = 0;

		// calculate the tickets for each candidate
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			// take the stored value as numerator and divide by the total. Then add the previous tickets so that we get an incremental value from 0 to 1
			lastTickets = (tickets[i] / totalMix) + lastTickets;

			// if the ticket is less than the amount of tickets this candidate has, then this one is the winner!
			if (winningTicket <= lastTickets)
				return i;
		}

		// If we reach this point it means that the ONLY remaining node to visit is the best node. FML
		throw new RuntimeException("Something went wrong with the lottery! No Candidate won");
	}

	/**
	 * Based on the amount of pheromone and the inverse of the distance, this algorithm returns the best target node
	 * TODO: currentNode could be extracted from ant alone, or nah
	 * 
	 * @param currentNode
	 * @return bestNextNode
	 */
	private int nextGreedy(int ant, int currentNode)
	{
		double maxMixedVal = 0;
		int bestNode = -1;

		// if we use the candidate lists then we can have some fun
		if (this.useCandidates)
		{
			// go through the candidates to find the most interesting node to visit
			for (int i = 0; i < this.structure.CANDIDATES_SIZE; i++)
			{
				int candidateNode = this.structure.getCandidates(currentNode)[i];
				// if the candidate has been visited already or we're on it then skip (second IF might be unnecessary, but no impact is made)
				if (this.visitedNode[ant][candidateNode] || candidateNode == currentNode)
					continue;

				// compute the mix value and save it along with the ant if it's the most interesting so far
				double mix = computeMix(currentNode, candidateNode);
				if (mix > maxMixedVal)
				{
					maxMixedVal = mix;
					bestNode = candidateNode;
				}
			}
			// unless we found no interesting city to visit, we return it as next city to visit
			if (bestNode != -1)
				return bestNode;
		}

		// if candidates lists failed to find a good city to visit, then we fallback to the normal procedure
		// go through all nodes
		for (int i = 0; i < this.tour.tourSize(); i++)
		{
			// if the node has been visited already or we're on it then skip (second IF might be unnecessary, but no impact is made)
			if (this.visitedNode[ant][i] || i == currentNode)
				continue;

			// compute the mix value and save it along with the ant if it's the most interesting so far
			double mix = computeMix(currentNode, i);
			if (mix > maxMixedVal)
			{
				maxMixedVal = mix;
				bestNode = i;
			}
		}

		// at this point a node must have been found to visit, hence we return it
		if (bestNode != -1)
			return bestNode;

		// if no node was found, then we will throw an exception because something is wrong
		throw new RuntimeException("No more nodes to explore. Did you try to take an extra step?");
	}

	/**
	 * given a connection, a value, using the inverse of the distance between the cities and the pheromone between the two, is returned
	 * 
	 * @param origin
	 * @param destination
	 * @return mix
	 */
	private double computeMix(int origin, int destination)
	{
		return getPheromone(origin, destination) * Math.pow(this.structure.getInverseAbsDistance(origin, destination), BETA);
	}

	/**
	 * Value of pheromone between two cities
	 * 
	 * @param origin
	 * @param destination
	 * @return pheromone
	 */
	private double getPheromone(final int origin, final int destination)
	{
		return this.pheromone[origin][destination];
	}

	/**
	 * Places the ants in their initial positions
	 * 
	 * @param initialCitiesVisitedByAnt
	 */
	private void initAnts(int[] initialCitiesVisitedByAnt)
	{
		for (int i = 0; i < ANTS_POPULATION; i++)
		{
			// reset ant's memory if it's not the first tour
			if (this.iterations > 0)
				resetEEPROM(i);

			// initialize a new Tour to store the solutions
			this.antTour[i] = new Tour(this.structure);

			// if the ant has no memory OR it's the first iteration (no memory at all) then place the ant randomly in the tour
			if (this.iterations == 0 || !isAntWithMemory())
			{
				// TODO: verify that only 1 ant can be in 1 node
				int randval = this.random.nextInt(this.tour.tourSize());
				this.antTour[i].addNode(setAntPosition(i, randval));
			}
			// if the ant is lucky enough to be allowed to have some memory then we give them memories of their ancestor best ant
			else
			{
				// generate 2 random numbers such that 0 < i < j < n
				int randI = this.random.nextInt(this.tour.tourSize() - 2);
				int randJ;
				do
				{
					randJ = randI + 1 + this.random.nextInt(this.tour.tourSize() - randI);
				} while (randJ <= randI);

				// memorize the nodes between those 2 indexes
				for (int j = randI; j < randJ; j++)
				{
					// get the node we want to restore into memory
					int candidateNode = this.bestAntEver.getNode(j);
					// set the ant position to that node and add it to the tour (we use setAntPosition because it handles all the flags already)
					this.antTour[i].addNode(setAntPosition(i, candidateNode));
					// count the cities that the ant memorizes
					initialCitiesVisitedByAnt[i]++;
				}
			}
		}
	}

	/**
	 * On success, the position is returned back again
	 * 
	 * @param ant
	 * @param position
	 * @return position
	 */
	private int setAntPosition(int ant, int position)
	{
		if (!this.visitedNode[ant][position])
		{
			this.antPosition[ant] = position;
			this.visitedNode[ant][position] = true;
			return position;
		} else
			throw new RuntimeException("This node has already been visited by this ant");
	}

	/**
	 * true if ant is a M(emory)ant
	 * false if ant is just an ant
	 * 
	 * @return greedy
	 */
	private boolean isAntWithMemory()
	{
		double ticket = this.random.nextDouble();
		return ticket < MEMORYNESS;
	}

	/**
	 * formats ant's memory
	 * 
	 * @param ant
	 */
	private void resetEEPROM(int ant)
	{
		Arrays.fill(this.visitedNode[ant], false);
	}

	/**
	 * Computes the pheromone base value
	 * 
	 * @return pheromone
	 */
	private double computeDefaultpheromone()
	{
		return 1. / (this.tour.getTourLength() * this.structure.getSize());
	}
}
