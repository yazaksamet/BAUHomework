package ynwa.AI_EFC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UFCCombination {
	public List<UFCPlayer> PlayerList;
	
	public Double TotalCost;
	
	public Double TotalScore;
	
	public int I;
	
	public int J;
	
	public int K;
	
	public int L;
	
	public UFCCombination(int i, int j, List<UFCPlayer> playerList) {
		this.PlayerList = new ArrayList<UFCPlayer>();
		
		this.PlayerList.add(playerList.get(i));
		this.PlayerList.add(playerList.get(j));
		
		this.TotalCost = playerList.get(i).Price + playerList.get(j).Price;
		this.TotalScore = playerList.get(i).Avarage + playerList.get(j).Avarage;
		
		this.I = i;
		this.J = j;
		this.K = -1;
		this.L = -1;
	}
	
	public UFCCombination(int i, int j, int k, int l, List<UFCPlayer> playerList) {
		this.PlayerList = new ArrayList<UFCPlayer>();
		
		this.PlayerList.add(playerList.get(i));
		this.PlayerList.add(playerList.get(j));
		this.PlayerList.add(playerList.get(k));
		this.PlayerList.add(playerList.get(l));
		
		this.TotalCost = playerList.get(i).Price + playerList.get(j).Price + playerList.get(k).Price + + playerList.get(l).Price;
		this.TotalScore = playerList.get(i).Avarage + playerList.get(j).Avarage + playerList.get(k).Avarage + playerList.get(l).Avarage;
		
		this.I = i;
		this.J = j;
		this.K = k;
		this.L = l;
	}
	
	public String toString()
	{
		String text = "(";
		
		for(Iterator<UFCPlayer> i = this.PlayerList.iterator(); i.hasNext(); ) {
			text += i.next().Name + "-";
		}
		
		text += ")";
		text += "TotalCost: " + this.TotalCost;
		text += "-TotalScore: " + this.TotalScore;
		
		return text;
	}
}
