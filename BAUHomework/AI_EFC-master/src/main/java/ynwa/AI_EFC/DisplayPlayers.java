package ynwa.AI_EFC;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class DisplayPlayers {

	public static void main(String[] args) throws FileNotFoundException {
		Type PLAYER_TYPE = new TypeToken<List<UFCPlayer>>(){}.getType();
		JsonReader reader1 = new JsonReader(new FileReader("results/countList.txt"));
		Gson gson = new Gson();
		List<UFCPlayer> playerList = gson.fromJson(reader1, PLAYER_TYPE);
		
		Collections.sort(playerList, new PlayerCountCompare());
		
		for (int i = 0; i < playerList.size(); i++) {
			System.out.println("Name: " + playerList.get(i).Name + " Count:" + playerList.get(i).SelectCount);
		}
	}

}
