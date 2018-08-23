import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class AppMain {
	public static String dataPath = "data/iris.data.txt";
	public static String dataOutputPath = "output/data.txt";
	public static String dataCentroidPath = "output/centroid.txt";
	
	public static void main(String[] args) throws Exception {
		
		String fieldDelimeter = ",";
		int numberOfIterations = 5;
		int numberOfClusters = 3;
		
		KMeansOutput bestIteration = null;
		
		for (int i = 0; i < numberOfIterations; i++) {
			System.out.println("Outer iterations #:" + i);
			KMeans kmeans = new KMeans(dataPath, fieldDelimeter, numberOfClusters);
			KMeansOutput result = kmeans.GenerateClusters();
			result.PrintOutput();
			
			if (bestIteration == null || result.getSSE() < bestIteration.getSSE()) {
				bestIteration = result;
			}
		}
		
		PrintOutputFile(bestIteration);
	}
	
	public static void PrintOutputFile(KMeansOutput output) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writerData = new PrintWriter(dataOutputPath, "UTF-8");
		PrintWriter writerCentroid = new PrintWriter(dataCentroidPath, "UTF-8");
		
		for ( int i = 0; i < output.getData().getSize();i ++ ) {
			writerData.println(output.getData().getIris(i).OutputToFile());
		}
		
		for ( int i = 0; i < output.getCentroids().size();i ++ ) {
			writerCentroid.println(output.getCentroids().get(i).OutputToFile());
		}
		
		writerData.close();
		writerCentroid.close();
	}
}
