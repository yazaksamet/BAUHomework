package ynwa.AI_EFC;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class App 
{
	public static String GuardPath = "data/guard.txt";
	public static String ForwardPath = "data/forward.txt";
	public static String CenterPath = "data/center.txt";
	
	public static double MaxCost = 500.00;
	public static double MinScore = 150.0;
	public static int MaxEvolve = 100;
	public static int PopulationSize = 1000;
	
	public static String SearchAlgorithm = "GN"; // BF - brute force; GN - Genetic 
	public static String SelectionProcess = "AB"; // AB - alpha beta prunning, HC - hill climbing
	public static String InitializationType = "HR"; // HR - heuristic, RN - random
	
	public static long BFMaxIteration = 10000000000L;
	
    public static void main( String[] args ) throws Exception
    {
    	SetStartParameters(args);
    	PopulationSpace space = EFCHelper.GetPopulationSpace(GuardPath, ForwardPath, CenterPath, true);
    	
    	if (SearchAlgorithm.equals("GN")) {
	    	Population population = new Population(PopulationSize, true, space, MaxCost, InitializationType);
			
	    	if (SelectionProcess.equals("AB")) {
	    		population.setUseAphaBeta(true);
	    	}
	    	else if (SelectionProcess.equals("HC")) {
	    		population.setUseHillClimbing(true);
	    	}
	    	
			GeneticAlgorithm mainAlgorith = new GeneticAlgorithm(population, MaxEvolve, MinScore, true);
			Chromosome result = mainAlgorith.Start();
	    	
			SaveChromosomeResult(result);
    	}
    	else if (SearchAlgorithm.equals("BF")) {
    		System.out.println("Brute Force Started:" + BFMaxIteration);
    		List<UFCPlayer> resultList = EFCHelper.FindBestLists(space.guardSpace, space.forwardSpace, space.centerSpace, MaxCost, BFMaxIteration);
    		
    		System.out.println("Brute Force Result with iteration:" + BFMaxIteration);
    		EFCHelper.PrintPlayerList(resultList);
    		//SaveResult(resultList);
    	}
    }
    
    public static void SetStartParameters (String[] args ) throws Exception {
    	try {
    		if (args != null && args.length > 0) {
    			if (args.length % 2 == 1) {
    				throw new Exception("Invalid Exception");
    			}
    			
    			for (int i = 0; i < args.length; i= i+2) {
    				String command = args[i];
    				String value = args[i+1];
    				
    				switch (command) {
    					case "-i":
    						InitializationType = value;
    						break;
    					case "-s":
    						SelectionProcess = value;
    						break;
    					case "-a":
    						SearchAlgorithm = value;
    						break;
    					case "-c":
    						MaxCost = Double.parseDouble(value);
    						break;
    					case "-p":
    						MinScore = Double.parseDouble(value);
    						break;
    					case "-e":
    						MaxEvolve = Integer.parseInt(value);
    						break;
    				}
    			}
    		}
    		else {
    			System.out.println("No startup parameters");
    		}
    	}
    	catch (Exception ex) {
    		System.out.println("Invalid Usage");
    		PrintUsage();
    		throw new Exception(ex);
    	}
    }
    
    public static void PrintUsage() {
    	System.out.println("Sample usage: app -i HR -s AB -a GN -c 500.00 -p 150.00 -e 100");
    	System.out.println("-i: initialization type. Possible Values: HR - Heuristic Init, RN - Random Init. Default HR");
    	System.out.println("-s: selection process. Possible Values: AB - alpha beta prunning, HC - hill climbing. Default AB");
    	System.out.println("-a: algorithm type. Possible Values: GN - genetic algorithm, BF - brute force. Default GN");
    	System.out.println("-c: available cost. Default 500");
    	System.out.println("-p: terminates search when programme reaches this score. Default 150");
    	System.out.println("-e: generic algorithm max evolve number. Default 100");
    }
    
    public static void SaveChromosomeResult(Chromosome result) throws IOException {
    	List<UFCPlayer> resultList = result.GetPlayerList();
    	SaveResult(resultList);
    }
    
    public static void SaveResult(List<UFCPlayer> resultList) throws IOException {
    	Type PLAYER_TYPE = new TypeToken<List<UFCPlayer>>(){}.getType();
    	String countPath = "results/countList.txt";
    	File countList = new File(countPath);
    	
    	List<UFCPlayer> playerList = new ArrayList<UFCPlayer>();
    	if (countList.exists()) {
    		JsonReader reader1 = new JsonReader(new FileReader(countPath));
    		Gson gson = new Gson();
    		playerList = gson.fromJson(reader1, PLAYER_TYPE);
    	}
    	else {
    		JsonReader reader1 = new JsonReader(new FileReader(GuardPath));
    		Gson gson = new Gson();
    		List<UFCPlayer> guardList = gson.fromJson(reader1, PLAYER_TYPE);
    		
    		JsonReader reader2 = new JsonReader(new FileReader(ForwardPath));
    		Gson gson2 = new Gson();
    		List<UFCPlayer> forwardList = gson2.fromJson(reader2, PLAYER_TYPE);
    		
    		JsonReader reader3 = new JsonReader(new FileReader(CenterPath));
    		Gson gson3 = new Gson();
    		List<UFCPlayer> centerList = gson3.fromJson(reader3, PLAYER_TYPE);
    		
    		playerList.addAll(guardList);
    		playerList.addAll(forwardList);
    		playerList.addAll(centerList);
    	}
    	
    	for (int i = 0; i < resultList.size(); i++) {
    		for (int j = 0; j < playerList.size(); j++) {
    			if (playerList.get(j).Name.equals(resultList.get(i).Name)) {
    				playerList.get(j).SelectCount = playerList.get(j).SelectCount < 0 ?
    						1 : playerList.get(j).SelectCount + 1;
    			}
    		}
    	}
    	
		try (Writer writer = new FileWriter(countPath)) {
		    Gson gson = new GsonBuilder().create();
		    gson.toJson(playerList, writer);
		}
    }
    
    public static void TestIndex() {
    	int[] four = IndexHealer.FindBetterFour(2,3,6,7);
    	System.out.println(Arrays.toString(four));
    	
    	int[] two = IndexHealer.FindBetterTwo(8,9);
    	System.out.println(Arrays.toString(two));
    	
    }
}
