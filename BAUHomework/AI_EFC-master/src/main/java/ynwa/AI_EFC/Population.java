package ynwa.AI_EFC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Population {
	private double uniformRate = 0.5;
	
    private int selectionSize = 50;
    
    private boolean elitism = true;
	
    private int elitismSize = 5;
    
	List<Chromosome> chromosomeList;

	private PopulationSpace space;
	
	private double maxCost = 0;
	
	private boolean useHillClimbing = false;
	
	private int populationSize;
	
	private int heuristicSearchSize = 200;
	
	private boolean printOnce = true;
	
	public boolean isUseHillClimbing() {
		return useHillClimbing;
	}

	public void setUseHillClimbing(boolean useHillClimbing) {
		this.useHillClimbing = useHillClimbing;
	}

	public boolean isUseAphaBeta() {
		return useAphaBeta;
	}

	public void setUseAphaBeta(boolean useAphaBeta) {
		this.useAphaBeta = useAphaBeta;
	}

	private boolean useAphaBeta = false;
	
	public double getMaxCost() {
		return maxCost;
	}

	public void setMaxCost(double maxCost) {
		this.maxCost = maxCost;
	}

	public PopulationSpace getSpace() {
		return space;
	}

	public void setSpace(PopulationSpace space) {
		this.space = space;
	}

	public List<Chromosome> getChromosomeList() {
		return chromosomeList;
	}

	public void setChromosomeList(List<Chromosome> chromosomeList) {
		this.chromosomeList = chromosomeList;
	}
	
	public Chromosome getChromosome(int index) {
		return this.chromosomeList.get(index);
	}
	
	public void setChromosome(int index, Chromosome chrome) {
		this.chromosomeList.set(index, chrome);
	}
	
	public void addChromosome(Chromosome chrome) {
		this.chromosomeList.add(chrome);
	}
	
	public Population(int populationSize, boolean initialize, PopulationSpace space, double maxCost, String initType) throws Exception{
		this.chromosomeList = new ArrayList<Chromosome>();
		this.space = space;
		this.maxCost = maxCost;
		this.populationSize = populationSize;
		
		if (initType.equals("RN")) {
			if (initialize) {
				System.out.println("Random init selected");
			}
			
			RandomInitialize(initialize);
		}
		else if (initType.equals("HR")) {
			if (initialize) {
				System.out.println("Heuristic init selected");
			}
			
			HeuristicInitialize();
		}
		else {
			throw new Exception("Invalid init type. Possible values, RN, HR. Entered: " + initType);
		}
	}
	
	private void HeuristicInitialize() throws FileNotFoundException {
		Chromosome heuristicChrome = GetHeuristicChrome();
		Random rn = new Random();
		
		if (heuristicChrome != null) {
			while (this.chromosomeList.size() < this.populationSize) {
				int guardHeur = heuristicChrome.getGuardIndex() + rn.nextInt(heuristicSearchSize) - (heuristicSearchSize / 2);
				int forwardHeur = heuristicChrome.getForwardIndex() + rn.nextInt(heuristicSearchSize) - (heuristicSearchSize / 2);
				int centerHeur = heuristicChrome.getCenterIndex() + rn.nextInt(heuristicSearchSize) - (heuristicSearchSize / 2);
				
				if (guardHeur > -1 && guardHeur < this.space.getGuardSpace().size()
   					 && forwardHeur > -1 && forwardHeur < this.space.getForwardSpace().size()
   					 && centerHeur > -1 && centerHeur < this.space.getCenterSpace().size()
   					 ) {
					
					Chromosome newChrome = new Chromosome(this, guardHeur, forwardHeur, centerHeur);
					
					if (newChrome.getCost() <= this.maxCost) {
						this.chromosomeList.add(newChrome);
					}
				}
			}
		}
	}
	
	private Chromosome GetHeuristicChrome() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("results/sample_start_list.txt"));
        scanner.useDelimiter("\n");
		Chromosome heuristicChrome = null;
        
        if( scanner.hasNext() ){
        	String line = scanner.next();
        	String[] chromeParts = line.split("-");
        	
        	if (chromeParts.length == 3) {
        		int guardIndex = Integer.parseInt(chromeParts[0], 2);
    			int forwardIndex = Integer.parseInt(chromeParts[1], 2);
    			int centerIndex = Integer.parseInt(chromeParts[2], 2);
    			
        		heuristicChrome = new Chromosome(this, guardIndex, forwardIndex, centerIndex);
        	}
        }
        
        scanner.close();
        
        return heuristicChrome;
	}
	
	private void RandomInitialize(boolean initialize) {
		for (int i = 0; i < this.populationSize; i++) {
			Chromosome newChrome = new Chromosome(this);
			
			if (initialize) {
				newChrome.fillRandomly();
				newChrome.getFitness();
			}
			
			this.chromosomeList.add(newChrome);
		}
	}
	
	public Chromosome getBestChromosome() {
        Chromosome bestChrome = this.chromosomeList.get(0);
        double bestScore = 0;
        int bestIndex = 0;
        
        for (int i = 0; i < this.chromosomeList.size(); i++) {
            Chromosome currentChrome = this.chromosomeList.get(i);
        	double currentScore = currentChrome.getFitness();
        	double currentCost = currentChrome.getCost();
        	int currentIndex = currentChrome.getGuardIndex() + currentChrome.getForwardIndex() + currentChrome.getCenterIndex();
        	
        	if (currentScore > bestScore && currentCost <= maxCost ) {
                bestChrome = currentChrome;
                bestScore = currentScore;
                bestIndex = currentIndex;
            }
        	else if (currentScore == bestScore && currentCost <= maxCost) {
        		//double bestCost = bestChrome.getCost();
        		
        		if (currentIndex < bestIndex) {
        			bestChrome = currentChrome;
        			bestScore = currentScore;
        			bestIndex = currentIndex;
        		}
        	}
        }
        return bestChrome;
    }
	
	public void Evolve() throws Exception {
		List<Chromosome> newChromList = new ArrayList<Chromosome>();
		List<Chromosome> elitPopulation = new ArrayList<Chromosome>();
		int populationStartIndex = 0;
		
		if (this.elitism) {
			Collections.sort(this.chromosomeList, new FittnessCompare());
			elitPopulation.addAll(this.chromosomeList.subList(0, elitismSize));
            
            List<Chromosome> betterChromes = FindBetterChromosomes(this.chromosomeList.get(0));
            if (betterChromes.size() > 0) {
            	elitPopulation.addAll(betterChromes);
            }
            else {
            	elitPopulation.add(this.getBestChromosome());
            }
        }
		
        for (int i = populationStartIndex; i < this.chromosomeList.size(); i++) {
        	Chromosome chrome1 = NaturalSelection();
        	Chromosome chrome2 = NaturalSelection();
        	Chromosome newIndiv = Crossover(chrome1, chrome2);
        	
        	if (newIndiv.getCost() > this.maxCost) {
        		newIndiv.fillRandomly();
        	}
        	
            newChromList.add(newIndiv);
        }

        for (int i = populationStartIndex; i < newChromList.size(); i++) {
            newChromList.get(i).Mutate(this.maxCost);
        }
        
        if (elitPopulation.size() > 0) {
        	newChromList.addAll(elitPopulation);
        	Collections.sort(newChromList, new FittnessCompare());
        }
        
        this.chromosomeList = newChromList.size() > this.populationSize ?  
        		newChromList.subList(0, this.populationSize)
        		: newChromList;
	}
	
	private List<Chromosome> HillClimbing(Chromosome chrome) {
		List<Chromosome> betterChromes = new ArrayList<Chromosome>();
		
		int currentGuardIndex = chrome.getGuardIndex();
		int currentForwardIndex = chrome.getForwardIndex();
		int currentCenterIndex = chrome.getCenterIndex();
		
		Chromosome betterGuard = new Chromosome(this, currentGuardIndex - 1, currentForwardIndex, currentCenterIndex);
		Chromosome betterForward = new Chromosome(this, currentGuardIndex, currentForwardIndex - 1, currentCenterIndex);
		Chromosome betterCenter = new Chromosome(this, currentGuardIndex, currentForwardIndex, currentCenterIndex - 1);
		
		if (betterGuard.getCost() <= this.maxCost) {
			betterChromes.add(betterGuard);
		}
		else {
			betterGuard = new Chromosome(this, currentGuardIndex + 1, currentForwardIndex, currentCenterIndex);
			
			if (betterGuard.getCost() <= this.maxCost) {
				betterChromes.add(betterGuard);
			}
		}
		
		if (betterForward.getCost() <= this.maxCost) {
			betterChromes.add(betterForward);
		}
		else {
			betterForward = new Chromosome(this, currentGuardIndex, currentForwardIndex + 1, currentCenterIndex);
			
			if (betterForward.getCost() <= this.maxCost) {
				betterChromes.add(betterForward);
			}
		}
		
		if (betterCenter.getCost() <= this.maxCost) {
			betterChromes.add(betterCenter);
		}
		else {
			betterCenter = new Chromosome(this, currentGuardIndex, currentForwardIndex, currentCenterIndex + 1);
			
			if (betterCenter.getCost() <= this.maxCost) {
				betterChromes.add(betterCenter);
			}
		}
		
		return betterChromes;
	}
	
	private List<Chromosome> AlphaBetaPrunning(Chromosome chrome) {
		List<Chromosome> betterChromes = new ArrayList<Chromosome>();
		
		int currentGuardIndex = chrome.getGuardIndex();
		int currentForwardIndex = chrome.getForwardIndex();
		int currentCenterIndex = chrome.getCenterIndex();
		
		double currentFittness = chrome.getFitness();
		
		int maxGuardIndex = this.space.guardSpace.size();
		int maxForwardIndex = this.space.forwardSpace.size();
		int maxCenterIndex = this.space.centerSpace.size();
		
		for (int i = 0; i < this.space.guardList.size(); i++) {
			int downIndex = currentGuardIndex + (i+1);
			int upIndex = currentGuardIndex - (i+1);
			
			if (downIndex > -1 && downIndex < maxGuardIndex) {
				Chromosome downGuard = new Chromosome(this, downIndex, currentForwardIndex, currentCenterIndex);
				
				if (downGuard.getCost() <= this.maxCost && currentFittness < downGuard.getFitness()) {
					betterChromes.add(downGuard);
				}
			}
			
			if (upIndex > -1 && upIndex < maxGuardIndex) {
				Chromosome upGuard = new Chromosome(this, upIndex, currentForwardIndex, currentCenterIndex);
				
				if (upGuard.getCost() <= this.maxCost && currentFittness < upGuard.getFitness()) {
					betterChromes.add(upGuard);
				}
			}
		}
		
		for (int i = 0; i < this.space.forwardList.size(); i++) {
			int downIndex = currentForwardIndex + (i+1);
			int upIndex = currentForwardIndex - (i+1);
			
			if (downIndex > -1 && downIndex < maxForwardIndex) {
				Chromosome downForward = new Chromosome(this, currentGuardIndex, downIndex, currentCenterIndex);
				
				if (downForward.getCost() <= this.maxCost && currentFittness < downForward.getFitness()) {
					betterChromes.add(downForward);
				}
			}
			
			if (upIndex > -1 && upIndex < maxForwardIndex) {
				Chromosome upForward = new Chromosome(this, currentGuardIndex, upIndex, currentCenterIndex);
				
				if (upForward.getCost() <= this.maxCost && currentFittness < upForward.getFitness()) {
					betterChromes.add(upForward);
				}
			}
		}

		for (int i = 0; i < this.space.forwardList.size(); i++) {
			int downIndex = currentCenterIndex + (i+1);
			int upIndex = currentCenterIndex - (i+1);
			
			if (downIndex > -1 && downIndex < maxCenterIndex) {
				Chromosome downCenter = new Chromosome(this, currentGuardIndex, currentForwardIndex, downIndex);
				
				if (downCenter.getCost() <= this.maxCost && currentFittness < downCenter.getFitness()) {
					betterChromes.add(downCenter);
				}
			}
			
			if (upIndex > -1 && upIndex < maxCenterIndex) {
				Chromosome upCenter = new Chromosome(this, currentGuardIndex, currentForwardIndex, upIndex);
				
				if (upCenter.getCost() <= this.maxCost && currentFittness < upCenter.getFitness()) {
					betterChromes.add(upCenter);
				}
			}
		}
		
		return betterChromes;
	}
	
	private List<Chromosome> FindBetterChromosomes(Chromosome chrome) {
		List<Chromosome> betterChromes = new ArrayList<Chromosome>();
		
		if (this.useHillClimbing) {
			if (this.printOnce) {
				System.out.println("Selection - Hill Climbing");
			}
			
			List<Chromosome> hillClimbingResult = HillClimbing(chrome);
			betterChromes.addAll(hillClimbingResult);
		}
		
		if (this.useAphaBeta) {
			if (this.printOnce) {
				System.out.println("Selection - Alpha beta prunning");
			}
			
			List<Chromosome> alphaBetaResult = AlphaBetaPrunning(chrome);
			betterChromes.addAll(alphaBetaResult);
		}
		
		this.printOnce = false;
		
		List<Chromosome> topChromes = null;
		if (betterChromes.size() > 5) {
			Collections.sort(betterChromes, new FittnessCompare());
			topChromes = betterChromes.subList(0, 5);
		}
		else {
			topChromes = betterChromes;
		}
		
		return topChromes;
	}
	
    private Chromosome NaturalSelection() throws Exception {
        Population selection = new Population(selectionSize, false, this.space, this.maxCost, "RN");
        
        for (int i = 0; i < selectionSize; i++) {
            int randomId = (int) (Math.random() * this.chromosomeList.size());
            selection.setChromosome(i, this.chromosomeList.get(randomId));
        }
        
        Chromosome bestSelection = selection.getBestChromosome();
        return bestSelection;
    }
    
    private Chromosome Crossover(Chromosome chrome1, Chromosome chrome2) {
    	Chromosome newChrome = new Chromosome(this);
        
        for (int i = 0; i < chrome1.getGuardSequence().length; i++) {
            if (Math.random() <= uniformRate) {
            	newChrome.setGuardGene(i, chrome1.getGuardGene(i));
            } else {
            	newChrome.setGuardGene(i, chrome2.getGuardGene(i));
            }
        }
        
        for (int i = 0; i < chrome1.getForwardSequence().length; i++) {
            if (Math.random() <= uniformRate) {
            	newChrome.setForwardGene(i, chrome1.getForwardGene(i));
            } else {
            	newChrome.setForwardGene(i, chrome2.getForwardGene(i));
            }
        }
        
        for (int i = 0; i < chrome1.getCenterSequence().length; i++) {
            if (Math.random() <= uniformRate) {
            	newChrome.setCenterGene(i, chrome1.getCenterGene(i));
            } else {
            	newChrome.setCenterGene(i, chrome2.getCenterGene(i));
            }
        }
        
        newChrome.getFitness();
        return newChrome;
    }
	
}
