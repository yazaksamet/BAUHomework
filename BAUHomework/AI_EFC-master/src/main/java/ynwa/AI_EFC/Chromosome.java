package ynwa.AI_EFC;

import java.util.ArrayList;
import java.util.List;

public class Chromosome {
	private byte[] guardSequence;
	
	private byte[] forwardSequence;
	
	private byte[] centerSequence;
	
	private double fitness;

	private double mutationRate = 0.2;
	
	private Population parent;
	
	public Chromosome(Population parent){
		this.parent = parent;
		if (parent != null && parent.getSpace() != null){
			this.guardSequence = new byte[Integer.toString(parent.getSpace().guardSpace.size(),2).length()];
			this.forwardSequence = new byte[Integer.toString(parent.getSpace().forwardSpace.size(),2).length()];
			this.centerSequence = new byte[Integer.toString(parent.getSpace().centerSpace.size(),2).length()];
		}
	}
	
	public Chromosome(Population parent, int guardSeq, int forwardSeq, int centerSeq){
		this.parent = parent;
		if (parent != null && parent.getSpace() != null){
			this.guardSequence = new byte[Integer.toString(parent.getSpace().guardSpace.size(),2).length()];
			this.forwardSequence = new byte[Integer.toString(parent.getSpace().forwardSpace.size(),2).length()];
			this.centerSequence = new byte[Integer.toString(parent.getSpace().centerSpace.size(),2).length()];
		}
		
		String guardString = Integer.toString(guardSeq,2);
		String forwardString = Integer.toString(forwardSeq,2);
		String centerString = Integer.toString(centerSeq,2);
		
		guardString = String.format("%" + this.guardSequence.length + "s", guardString).replace(' ', '0');
		forwardString = String.format("%" + this.forwardSequence.length + "s", forwardString).replace(' ', '0');
		centerString = String.format("%" + this.centerSequence.length + "s", centerString).replace(' ', '0');
		
		for (int i = 0; i < this.guardSequence.length; i++) {
            this.guardSequence[i] = (byte) (guardString.charAt(i) == '0' ? 0 : 1);
        }
        
        for (int i = 0; i < this.forwardSequence.length; i++) {
            this.forwardSequence[i] = (byte) (forwardString.charAt(i) == '0' ? 0 : 1);
        }
        
        for (int i = 0; i < this.centerSequence.length; i++) {
            this.centerSequence[i] = (byte) (centerString.charAt(i) == '0' ? 0 : 1);
        }
        
        if (this.getGuardIndex() >=  parent.getSpace().getGuardSpace().size()) {
        	this.guardSequence[0] = 0;
        }
        
        if (this.getForwardIndex() >=  parent.getSpace().getForwardSpace().size()) {
        	this.forwardSequence[0] = 0;
        }
        
        if (this.getCenterIndex() >=  parent.getSpace().getCenterSpace().size()) {
        	this.centerSequence[0] = 0;
        }
	}

	public double getFitness() {
		if (parent.getSpace() != null) {
			int guardIndex = Integer.parseInt(SequenceToString(this.guardSequence), 2);
			int forwardIndex = Integer.parseInt(SequenceToString(this.forwardSequence), 2);
			int centerIndex = Integer.parseInt(SequenceToString(this.centerSequence), 2);
			
			double guardScore =	parent.getSpace().getGuardSpace().size() > guardIndex ? parent.getSpace().getGuardSpace().get(guardIndex).TotalScore : 0;
			double forwardScore = parent.getSpace().getForwardSpace().size() > forwardIndex ? parent.getSpace().getForwardSpace().get(forwardIndex).TotalScore : 0;
			double centerScore = parent.getSpace().getCenterSpace().size() > centerIndex ? parent.getSpace().getCenterSpace().get(centerIndex).TotalScore : 0;
			
			double totalScore = guardScore + forwardScore + centerScore;
			
			fitness = (double) Math.round(totalScore * 100) / 100;
			return fitness;
		}
		
		return 0;
	}
	
	public double getCost() {
		if (parent.getSpace() != null) {
			int guardIndex = Integer.parseInt(SequenceToString(this.guardSequence), 2);
			int forwardIndex = Integer.parseInt(SequenceToString(this.forwardSequence), 2);
			int centerIndex = Integer.parseInt(SequenceToString(this.centerSequence), 2);
			/*
			double totalCost = 
					parent.getSpace().getGuardSpace().size() > guardIndex ? parent.getSpace().getGuardSpace().get(guardIndex).TotalCost : 0 +
					parent.getSpace().getForwardSpace().size() > forwardIndex ? parent.getSpace().getForwardSpace().get(forwardIndex).TotalCost : 0 +
					parent.getSpace().getCenterSpace().size() > centerIndex ? parent.getSpace().getCenterSpace().get(centerIndex).TotalCost : 0;
			*/
			double guardCost =	parent.getSpace().getGuardSpace().size() > guardIndex ? parent.getSpace().getGuardSpace().get(guardIndex).TotalCost : 0;
			double forwardCost = parent.getSpace().getForwardSpace().size() > forwardIndex ? parent.getSpace().getForwardSpace().get(forwardIndex).TotalCost : 0;
			double centerCost = parent.getSpace().getCenterSpace().size() > centerIndex ? parent.getSpace().getCenterSpace().get(centerIndex).TotalCost : 0;
			
			double totalCost = guardCost + forwardCost + centerCost;
			
			totalCost = (double) Math.round(totalCost * 100) / 100;
			
			return totalCost;
		}
		
		return 0;
	}
	
	public void fillRandomly() {
        for (int i = 0; i < this.guardSequence.length; i++) {
            byte gene = (byte) Math.round(Math.random());
            this.guardSequence[i] = gene;
        }
        
        for (int i = 0; i < this.forwardSequence.length; i++) {
            byte gene = (byte) Math.round(Math.random());
            this.forwardSequence[i] = gene;
        }
        
        for (int i = 0; i < this.centerSequence.length; i++) {
            byte gene = (byte) Math.round(Math.random());
            this.centerSequence[i] = gene;
        }
        
        if (this.getGuardIndex() >=  parent.getSpace().getGuardSpace().size()) {
        	this.guardSequence[0] = 0;
        }
        
        if (this.getForwardIndex() >=  parent.getSpace().getForwardSpace().size()) {
        	this.forwardSequence[0] = 0;
        }
        
        if (this.getCenterIndex() >=  parent.getSpace().getCenterSpace().size()) {
        	this.centerSequence[0] = 0;
        }
    }
	
	public int getGuardIndex() {
		return Integer.parseInt(SequenceToString(this.guardSequence), 2);
	}
	
	public int getForwardIndex() {
		return Integer.parseInt(SequenceToString(this.forwardSequence), 2);
	}
	
	public int getCenterIndex() {
		return Integer.parseInt(SequenceToString(this.centerSequence), 2);
	}
	
	public byte getGuardGene(int index) {
        return this.guardSequence[index];
    }

    public void setGuardGene(int index, byte value) {
        this.guardSequence[index] = value;
    }
    
    public byte getForwardGene(int index) {
        return this.forwardSequence[index];
    }

    public void setForwardGene(int index, byte value) {
        this.forwardSequence[index] = value;
    }
    
    public byte getCenterGene(int index) {
        return this.centerSequence[index];
    }

    public void setCenterGene(int index, byte value) {
        this.centerSequence[index] = value;
    }
	
	@Override
    public String toString() {
        String geneString = "Guard:";
        for (int i = 0; i < this.guardSequence.length; i++) {
            geneString += getGuardGene(i);
        }
        
        geneString += " - Forward:";
        for (int i = 0; i < this.forwardSequence.length; i++) {
            geneString += getForwardGene(i);
        }
        
        geneString += " - Center:";
        for (int i = 0; i < this.centerSequence.length; i++) {
            geneString += getCenterGene(i);
        }
        
        geneString += " - Fitness:" + this.fitness;
        
        geneString += " - Cost:" + getCost();
        
        return geneString;
    }

	public byte[] getGuardSequence() {
		return guardSequence;
	}

	public void setGuardSequence(byte[] guardSequence) {
		this.guardSequence = guardSequence;
	}

	public byte[] getForwardSequence() {
		return forwardSequence;
	}

	public void setForwardSequence(byte[] forwardSequence) {
		this.forwardSequence = forwardSequence;
	}

	public byte[] getCenterSequence() {
		return centerSequence;
	}

	public void setCenterSequence(byte[] centerSequence) {
		this.centerSequence = centerSequence;
	}
	
	public String SequenceToString(byte[] sequence){
		String geneString = "";
        for (int i = 0; i < sequence.length; i++) {
            geneString += sequence[i];
        }
        return geneString;
	}
	
	public void Mutate(double maxCost) {
        // Loop through genes
        for (int i = 0; i < this.getGuardSequence().length; i++) {
            if (Math.random() <= mutationRate) {
                byte gene = (byte) Math.round(Math.random());
                this.setGuardGene(i, gene);
            }
        }
        
        for (int i = 0; i < this.getForwardSequence().length; i++) {
            if (Math.random() <= mutationRate) {
                byte gene = (byte) Math.round(Math.random());
                this.setForwardGene(i, gene);
            }
        }
        
        for (int i = 0; i < this.getCenterSequence().length; i++) {
            if (Math.random() <= mutationRate) {
                byte gene = (byte) Math.round(Math.random());
                this.setCenterGene(i, gene);
            }
        }
        
        if (this.getCost() > maxCost) {
        	this.fillRandomly();
        }
    }
	
	public List<UFCPlayer> GetPlayerList() {
		int guardIndex = Integer.parseInt(SequenceToString(this.guardSequence), 2);
		int forwardIndex = Integer.parseInt(SequenceToString(this.forwardSequence), 2);
		int centerIndex = Integer.parseInt(SequenceToString(this.centerSequence), 2);
		
		UFCCombination guard = parent.getSpace().getGuardSpace().size() > guardIndex ? parent.getSpace().getGuardSpace().get(guardIndex) : null;
		UFCCombination forward = parent.getSpace().getForwardSpace().size() > forwardIndex ? parent.getSpace().getForwardSpace().get(forwardIndex) : null;
		UFCCombination center = parent.getSpace().getCenterSpace().size() > centerIndex ? parent.getSpace().getCenterSpace().get(centerIndex) : null;
		
		List<UFCPlayer> playerList = new ArrayList<UFCPlayer>();
		playerList.addAll(guard.PlayerList);
		playerList.addAll(forward.PlayerList);
		playerList.addAll(center.PlayerList);
		
		return playerList;
	}
	
	public void PrintSpaceData() {
		int guardIndex = Integer.parseInt(SequenceToString(this.guardSequence), 2);
		int forwardIndex = Integer.parseInt(SequenceToString(this.forwardSequence), 2);
		int centerIndex = Integer.parseInt(SequenceToString(this.centerSequence), 2);
		
		UFCCombination guard = parent.getSpace().getGuardSpace().size() > guardIndex ? parent.getSpace().getGuardSpace().get(guardIndex) : null;
		UFCCombination forward = parent.getSpace().getForwardSpace().size() > forwardIndex ? parent.getSpace().getForwardSpace().get(forwardIndex) : null;
		UFCCombination center = parent.getSpace().getCenterSpace().size() > centerIndex ? parent.getSpace().getCenterSpace().get(centerIndex) : null;
		
		System.out.println("Guard:");
		
		if (guard != null) {
			for (int i = 0; i < guard.PlayerList.size(); i++) {
				System.out.println(guard.PlayerList.get(i));
			}
		}
		
		System.out.println("Forward:");
		
		if (forward != null) {
			for (int i = 0; i < forward.PlayerList.size(); i++) {
				System.out.println(forward.PlayerList.get(i));
			}
		}
		
		System.out.println("Center:");
		
		if (center != null) {
			for (int i = 0; i < center.PlayerList.size(); i++) {
				System.out.println(center.PlayerList.get(i));
			}
		}
	}
}
