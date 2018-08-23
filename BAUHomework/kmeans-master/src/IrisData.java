import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IrisData {
	private List<Iris> irisList = new ArrayList<Iris>();
	
	public int getSize() {
		return this.irisList.size();
	}
	
	public List<Iris> getIrisList() {
		return irisList;
	}

	public void setIrisList(List<Iris> irisList) {
		this.irisList = irisList;
	}

	public Iris getIris (int index) {
		return this.irisList.get(index);
	}
	
	public IrisData(String dataPath, String fieldDelimeter) throws FileNotFoundException{
		Scanner scanner = new Scanner(new File(dataPath));
        scanner.useDelimiter("\r\n");
        
        while(scanner.hasNext()){
        	String line = scanner.next();
        	
        	if (line != null && !line.isEmpty()) {
	        	String[] fields = line.split(fieldDelimeter);
	        	
	        	Iris irisEntity = new Iris();
	        	
	        	irisEntity.setSepalLength(Double.parseDouble(fields[0]));
	        	irisEntity.setSepalWidth(Double.parseDouble(fields[1]));
	        	
	        	irisEntity.setPetalLength(Double.parseDouble(fields[2]));
	        	irisEntity.setPetalWidth(Double.parseDouble(fields[3]));
	        	
	        	irisEntity.setClusterIndex(-1);
	        	
	        	this.irisList.add(irisEntity);
        	}
        }
        
        scanner.close();
	}
	
	public void PrintData() {
		for (int i = 0; i < this.irisList.size(); i++) {
			System.out.println(this.getIris(i));
		}
	}
}
