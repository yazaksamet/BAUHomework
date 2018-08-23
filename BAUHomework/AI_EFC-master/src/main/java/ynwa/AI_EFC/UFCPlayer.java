package ynwa.AI_EFC;

public class UFCPlayer {
	public boolean IsAvailable;
	
	public String Name;
	
	public String Team;
	
	public String Record;
	
	public Double Avarage;
	
	public Double Price;
	
	public String Up;
	
	public String Down;
	
	public String Keeps;
	
	public String Opponent;
	
	public int SelectCount;
	
	@Override
    public String toString() {
		return this.Name + "-" + this.Avarage + "-" + this.Price;
	}
}
