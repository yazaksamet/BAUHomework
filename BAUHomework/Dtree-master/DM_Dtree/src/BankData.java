import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BankData {
	private List<Bank> bankList = new ArrayList<Bank>();
	private List<Bank> testList = new ArrayList<Bank>();
	public List<Bank> getTestList() {
		return testList;
	}

	public void setTestList(List<Bank> testList) {
		this.testList = testList;
	}

	private String dataPath = "data/bank-full.csv";
	private String fieldDelimeter = ";";
	private List<Field> fieldList;
	
	private String targetField = "y";
	private int targetFieldIndex = 16;
	
	public String getTargetField() {
		return targetField;
	}

	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

	public int getTargetFieldIndex() {
		return targetFieldIndex;
	}

	public void setTargetFieldIndex(int targetFieldIndex) {
		this.targetFieldIndex = targetFieldIndex;
	}

	public BankData() throws FileNotFoundException{
		Scanner scanner = new Scanner(new File(dataPath));
        scanner.useDelimiter("\n");
        
        Random rn = new Random();
        int rowCounter = 0;
        
        fieldList = new ArrayList<Field>();
        
        while(scanner.hasNext()){
        	String line = scanner.next();
        	String[] fields = line.split(fieldDelimeter);
        	
        	if (rowCounter == 0) {
        		for(int i = 0; i < fields.length; i++) {
        			String fieldName = fields[i].replace("\"", "");
        			
        			if (fieldName.equals("default")) {
        				fieldName = "defaultCredit";
        			}
        			
        			if (fieldName.equals("month")) {
        				fieldName = "quarter";
        			}
        			
        			Field newField = new Field();
        			newField.setName(fieldName);
        			
        			if (fieldName.equals("age")
        					|| fieldName.equals("balance")
        					|| fieldName.equals("day")
        					|| fieldName.equals("duration")
        					|| fieldName.equals("campaign")
        					|| fieldName.equals("pdays")
        					|| fieldName.equals("previous")) {
        				newField.setContinous(true);
        			}
        			else {
        				newField.setContinous(false);
        			}
        				
        			
        			fieldList.add(newField);
        		}
        	}
        	else {
        		Bank bank = new Bank();
        		
        		bank.age = Integer.parseInt(fields[0]);
        		bank.job = fields[1].replace("\"", "");
        		bank.marital = fields[2].replace("\"", "");
        		bank.education = fields[3].replace("\"", "");
        		bank.defaultCredit = fields[4].replace("\"", "");
        		bank.balance = Integer.parseInt(fields[5]);
        		bank.housing = fields[6].replace("\"", "");
        		bank.loan = fields[7].replace("\"", "");
        		bank.contact = fields[8].replace("\"", "");
        		bank.day = Integer.parseInt(fields[9]);
        		
        		if (fields[10] == "jan" || fields[10] == "feb" || fields[10] == "mar") {
        			bank.quarter = "first";
        		}
        		else if (fields[10] == "apr" || fields[10] == "may" || fields[10] == "jun") {
        			bank.quarter = "second";
        		}
        		else if (fields[10] == "jul" || fields[10] == "aug" || fields[10] == "sep") {
        			bank.quarter = "third";
        		}
        		else {
        			bank.quarter = "forth";
        		}
        		
        		bank.duration = Integer.parseInt(fields[11]);
        		bank.campaign = Integer.parseInt(fields[12]);
        		bank.pdays = Integer.parseInt(fields[13]);
        		bank.previous = Integer.parseInt(fields[14]);
        		bank.poutcome = fields[15].replace("\"", "");
        		bank.y = fields[16].replace("\"", "");
        		
        		int testOrTrain = rn.nextInt(10) + 1;
        		
        		if (testOrTrain < 7) {
        			bankList.add(bank);
        		}
        		else {
        			testList.add(bank);
        		}
        	}
        	
        	rowCounter++;
        }
        
        scanner.close();
	}

	public Bank getBank (int index) {
		return this.bankList.get(index);
	}
	
	public List<Bank> getBankList() {
		return bankList;
	}

	public String getDataPath() {
		return dataPath;
	}

	public String getFieldDelimeter() {
		return fieldDelimeter;
	}

	public List<Field> getFieldList() {
		return fieldList;
	}
	
}
