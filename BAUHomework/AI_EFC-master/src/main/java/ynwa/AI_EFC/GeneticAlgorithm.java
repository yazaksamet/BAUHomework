package ynwa.AI_EFC;

import java.io.FileNotFoundException;

public class GeneticAlgorithm {
	private Population population;
	
	private int numberOfMaxEvolve;
	
	private double fittnessThreshold;
	
	private boolean printInfo;
	
	private Chromosome currentBestChrome;
	
	public GeneticAlgorithm(Population pop, int maxEvolve, double minScore, boolean printInfo) {
		this.population = pop;
		this.fittnessThreshold = minScore;
		this.numberOfMaxEvolve = maxEvolve;
		this.printInfo = printInfo;
	}
	
	public Population getPopulation() {
		return population;
	}

	public void setPopulation(Population population) {
		this.population = population;
	}

	public int getNumberOfMaxEvolve() {
		return numberOfMaxEvolve;
	}

	public void setNumberOfMaxEvolve(int numberOfMaxEvolve) {
		this.numberOfMaxEvolve = numberOfMaxEvolve;
	}

	public double getFittnessThreshold() {
		return fittnessThreshold;
	}

	public void setFittnessThreshold(double fittnessThreshold) {
		this.fittnessThreshold = fittnessThreshold;
	}

	public boolean isPrintInfo() {
		return printInfo;
	}

	public void setPrintInfo(boolean printInfo) {
		this.printInfo = printInfo;
	}
	
	public Chromosome Start() throws Exception {
		for (int i = 0; i < this.numberOfMaxEvolve; i++) {
			population.Evolve();
			
			Chromosome bestChrome = population.getBestChromosome();
			
			if (this.printInfo) {
				System.out.println("Current Evaluation Number:" + i);
				if (currentBestChrome == null || 
						!(currentBestChrome.getGuardIndex() == bestChrome.getGuardIndex()
						&& currentBestChrome.getForwardIndex() == bestChrome.getForwardIndex()
						&& currentBestChrome.getCenterIndex() == bestChrome.getCenterIndex())) {
					
					currentBestChrome = bestChrome;
					System.out.println("Best Chrome Changed:");
					System.out.println(bestChrome);
					bestChrome.PrintSpaceData();
				}
			}
			if (bestChrome.getFitness() >= this.fittnessThreshold) {
				break;
			}
		}
		
		Chromosome bestChrome = population.getBestChromosome();
		
		if (this.printInfo) {
			System.out.println("Evaluation finished:");
			
			System.out.println("Best Chrome:");
			System.out.println(bestChrome);
			bestChrome.PrintSpaceData();
		}
		
		return bestChrome;
	}
}
