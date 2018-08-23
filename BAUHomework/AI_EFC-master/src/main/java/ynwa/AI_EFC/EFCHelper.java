package ynwa.AI_EFC;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class EFCHelper {
	public static List<UFCPlayer> FindBestLists(List<UFCCombination> guardList, List<UFCCombination> forwardList, List<UFCCombination> centerList, double maxCost, long maxIteration)
    {
    	double currentMaxScore = 0;
    	double currentMaxCost = 0;
    	List<UFCPlayer> playerList = new ArrayList<UFCPlayer>();
    	
    	long index = 0;
    	long displayIndex = maxIteration / 100;
    	
    	for (int i = 0; i < guardList.size(); i++)
    	{
    		UFCCombination guard = guardList.get(i);
    		List<UFCCombination> filteredForwardList = forwardList;
    				//.stream()
    			    //.filter(p -> p.TotalCost + guard.TotalCost <= maxCost).collect(Collectors.toList());
    		
    		for (int j = 0; j < filteredForwardList.size(); j++)
    		{
    			UFCCombination forward = filteredForwardList.get(j);
    			List<UFCCombination> filteredCenterList = centerList;
    					//.stream()
        			    //.filter(p -> p.TotalCost + guard.TotalCost + forward.TotalCost <= maxCost).collect(Collectors.toList());
        		
    			for (int k = 0; k < filteredCenterList.size(); k++)
    			{
    				index++;
    				
    				if (index > maxIteration) {
    					System.out.println("FinishedMaxScore:" + currentMaxScore);
    					System.out.println("FinishedMaxCost:" + currentMaxCost);
    					return playerList;
    				}
    				
    				if (index % displayIndex == 0)
    				{
    					System.out.println("%:" + index / displayIndex);
    				}
    				
    				UFCCombination center = centerList.get(k);
    				
    				double cost = guard.TotalCost + forward.TotalCost + center.TotalCost;
    				double score = guard.TotalScore + forward.TotalScore + center.TotalScore;
    				
    				if (cost <= maxCost )
    				{
    					if (score > currentMaxScore)
    					{
    						currentMaxScore = score;
    						currentMaxCost = cost;
    						
    						List<UFCPlayer> currentPlayerList = new ArrayList<UFCPlayer>();
    						
    						currentPlayerList.addAll(guard.PlayerList);
    						currentPlayerList.addAll(forward.PlayerList);
    						currentPlayerList.addAll(center.PlayerList);
    						
    						playerList = currentPlayerList;
    						
    						System.out.println("CurrentMaxScore:" + currentMaxScore);
    						System.out.println("CurrentMaxCost:" + currentMaxCost);
    						
    						System.out.println(Integer.toString(i,2) + "-" + Integer.toString(j,2) + "-" + Integer.toString(k,2));
    						
    						PrintPlayerList(currentPlayerList);
    					}
    				}
    			}
    		}
    	}
    	
    	return playerList;
    }
	
	public static void PrintPlayerList(List<UFCPlayer> playerList)
    {
    	for(Iterator<UFCPlayer> i = playerList.iterator(); i.hasNext(); ) {
			UFCPlayer center = i.next();
		    System.out.println(center.Name);
		}
    }
    
	public static List<UFCCombination> GetCombinationsWithTwo(List<UFCPlayer> playerList)
    {
    	List<UFCCombination> combList = new ArrayList<UFCCombination>();
    	
    	for (int i = 0; i < playerList.size(); i++)
    	{
    		playerList.get(i).Avarage = (double) Math.round(playerList.get(i).Avarage * 100) / 100;
    		playerList.get(i).Price = (double) Math.round(playerList.get(i).Price * 100) / 100;
    		for (int j = i+1; j < playerList.size(); j++)
    		{
    			UFCCombination comb = new UFCCombination(i, j, playerList);
    			combList.add(comb);
    		}
    	}
    	    	
    	return combList;
    }
    
    public static List<UFCCombination> GetCombinationsWithFour(List<UFCPlayer> playerList)
    {
    	List<UFCCombination> combList = new ArrayList<UFCCombination>();
    	
    	for (int i = 0; i < playerList.size(); i++)
    	{
    		playerList.get(i).Avarage = (double) Math.round(playerList.get(i).Avarage * 100) / 100;
    		playerList.get(i).Price = (double) Math.round(playerList.get(i).Price * 100) / 100;
    		
    		for (int j = i+1; j < playerList.size(); j++)
    		{
    			for (int k = j+1; k < playerList.size(); k++)
    			{
    				for (int l = k+1; l < playerList.size(); l++)
    				{
		    			UFCCombination comb = new UFCCombination(i, j, k, l, playerList);
		    			combList.add(comb);
    				}
    			}
    		}
    	}
    	
    	return combList;
    }
    
    public static PopulationSpace GetPopulationSpace(String guardPath, String forwardPath, String centerPath, boolean printInfo) throws FileNotFoundException {
    	Type PLAYER_TYPE = new TypeToken<List<UFCPlayer>>(){}.getType();
    	
    	JsonReader reader1 = new JsonReader(new FileReader(guardPath));
		Gson gson = new Gson();
		List<UFCPlayer> guardList = gson.fromJson(reader1, PLAYER_TYPE);
		
		JsonReader reader2 = new JsonReader(new FileReader(forwardPath));
		Gson gson2 = new Gson();
		List<UFCPlayer> forwardList = gson2.fromJson(reader2, PLAYER_TYPE);
		
		JsonReader reader3 = new JsonReader(new FileReader(centerPath));
		Gson gson3 = new Gson();
		List<UFCPlayer> centerList = gson3.fromJson(reader3, PLAYER_TYPE);
		
		guardList = guardList.stream().filter(p -> p.IsAvailable).collect(Collectors.toList());
		forwardList = forwardList.stream().filter(p -> p.IsAvailable).collect(Collectors.toList());
		centerList = centerList.stream().filter(p -> p.IsAvailable).collect(Collectors.toList());
		
		Collections.sort(guardList, new PlayerCompare());
		Collections.sort(forwardList, new PlayerCompare());
		Collections.sort(centerList, new PlayerCompare());
		
		List<UFCCombination> guardCombList = GetCombinationsWithTwo(guardList);
		List<UFCCombination> forwardCombList = GetCombinationsWithFour(forwardList);
		List<UFCCombination> centerCombList = GetCombinationsWithFour(centerList);
		
		PopulationSpace space = new PopulationSpace();
		space.setGuardSpace(guardCombList);
		space.setForwardSpace(forwardCombList);
		space.setCenterSpace(centerCombList);
		
		space.setCenterList(centerList);
		space.setGuardList(guardList);
		space.setForwardList(forwardList);
		
		if (printInfo) {
			System.out.println("Guard count:" + guardList.size());
			System.out.println("Forward count:" + forwardList.size());
			System.out.println("Center count:" + centerList.size());
			
			System.out.println("Guard comb count:" + guardCombList.size());
			System.out.println("Forward comb count:" + forwardCombList.size());
			System.out.println("Center comb count:" + centerCombList.size());
		}
		
		return space;
    }
}
