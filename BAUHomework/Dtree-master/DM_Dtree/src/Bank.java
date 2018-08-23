
public class Bank {
	public int age;
	
	public String job;
	
	public String marital;
	
	public String education;
	
	public String defaultCredit;
	
	public int balance;
	
	public String housing;
	
	public String loan;
	
	public String contact;
	
	public int day;
	
	public String quarter;
	
	public int duration;
	
	public int campaign;
	
	public int pdays;
	
	public int previous;
	
	public String poutcome;
	
	public String y;
	
	@Override
	public String toString() {
		return age + ";" + job + ";" +
				marital + ";" + education + ";" + defaultCredit + ";" + balance+ ";" + 
				housing+ ";" + loan+ ";" + contact + ";" + day + ";" + quarter + ";" + duration
				+ ";" + campaign + ";" + pdays + ";" + previous + ";" + poutcome + ";" + y;
	}
}
