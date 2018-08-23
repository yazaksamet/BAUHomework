import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeans {
	private IrisData data;
	
	private int numberOfClusters;
	
	private List<Iris> centroids;
	
	private int numberOfIterations;
	
	public IrisData getData() {
		return data;
	}

	public void setData(IrisData data) {
		this.data = data;
	}
	
	public int getNumberOfClusters() {
		return numberOfClusters;
	}

	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}

	public KMeans(String dataPath, String fieldDelimeter, int k) throws FileNotFoundException {
		this.data = new IrisData(dataPath, fieldDelimeter);
		this.numberOfClusters = k;
	}
	
	public KMeansOutput GenerateClusters() throws Exception {
		this.numberOfIterations = 0;
		
		GetInitialCentroids();
		
		IterateClusters();
		
		return PrepareOutput();
	}
	
	private void IterateClusters() throws Exception {
		int clusterChanges = 1;
		
		while (clusterChanges > 0) {
			this.numberOfIterations++;
			
			clusterChanges = AssignClusters();
			
			if (clusterChanges != 0) {
				ReCalculateCentroids();
			}
		}
	}
	
	private KMeansOutput PrepareOutput() {
		KMeansOutput output = new KMeansOutput();
		
		output.setCentroids(this.centroids);
		output.setData(this.data);
		output.setNumberOfIterations(this.numberOfIterations);
		output.setSSE(CalculateSSE());
		
		return output;
	}
	
	private double CalculateSSE() {
		double sse = 0;
		
		for (int i = 0; i < this.data.getSize(); i++) {
			Iris iris = this.data.getIris(i);
			double distance = GetEucleideanDistance(iris, this.centroids.get(iris.getClusterIndex()));
			sse += distance;
		}
		
		return sse;
	}
	
	private void GetInitialCentroids() {
		List<Iris> initialCentroids = new ArrayList<Iris>();
		Random rn = new Random();
		
		for (int i = 0; i < this.numberOfClusters; i++) {
			int randomIndex = rn.nextInt(this.data.getSize());
			Iris randomIris = this.data.getIris(randomIndex);
			
			Iris centroid = new Iris();
			centroid.setClusterIndex(i);
			centroid.setPetalLength(randomIris.getPetalLength());
			centroid.setPetalWidth(randomIris.getPetalWidth());
			centroid.setSepalLength(randomIris.getSepalLength());
			centroid.setSepalWidth(randomIris.getSepalWidth());
			
			initialCentroids.add(centroid);
		}
		
		this.centroids = initialCentroids;
	}
	
	private void ReCalculateCentroids() {
		for (int i = 0; i < this.numberOfClusters; i++) {
			int clusterIndex = i;
			
			long clusterSize = this.data.getIrisList().stream()
					.filter(iris -> clusterIndex == iris.getClusterIndex()).count();
			
			if (clusterSize != 0) {
				double sepalLengthSum = this.data.getIrisList().stream()
						.filter(iris -> clusterIndex == iris.getClusterIndex()).mapToDouble(a -> a.getSepalLength()).sum();
				
				double sepalWidthSum = this.data.getIrisList().stream()
						.filter(iris -> clusterIndex == iris.getClusterIndex()).mapToDouble(a -> a.getSepalWidth()).sum();
				
				double petalLengthSum = this.data.getIrisList().stream()
						.filter(iris -> clusterIndex == iris.getClusterIndex()).mapToDouble(a -> a.getPetalLength()).sum();
				
				double petalWidthSum = this.data.getIrisList().stream()
						.filter(iris -> clusterIndex == iris.getClusterIndex()).mapToDouble(a -> a.getPetalWidth()).sum();
				
				double centroidSepalLength = (double) sepalLengthSum / clusterSize;
				double centroidSepalWidth = (double) sepalWidthSum / clusterSize;
				double centroidPetalLength = (double) petalLengthSum / clusterSize;
				double centroidPetalWidth = (double) petalWidthSum / clusterSize;
				
				this.centroids.get(clusterIndex).setSepalLength(centroidSepalLength);
				this.centroids.get(clusterIndex).setSepalWidth(centroidSepalWidth);
				this.centroids.get(clusterIndex).setPetalLength(centroidPetalLength);
				this.centroids.get(clusterIndex).setPetalWidth(centroidPetalWidth);
			}
		}
	}
	
	private double GetEucleideanDistance(Iris entity1, Iris entity2) {
		double distance = 0;
		
		distance += Math.pow((entity1.getPetalLength() - entity2.getPetalLength()), 2);
		distance += Math.pow((entity1.getPetalWidth() - entity2.getPetalWidth()), 2);
		distance += Math.pow((entity1.getSepalLength() - entity2.getSepalLength()), 2);
		distance += Math.pow((entity1.getSepalWidth() - entity2.getSepalWidth()), 2);
		
		return Math.sqrt(distance);
	}
	
	private int GetClosestCentroid(Iris entity) {
		int closestCentroid = -1;
		double minDistance = -1;
		
		for (int i = 0; i < this.numberOfClusters; i++) {
			double distance = GetEucleideanDistance(entity, this.centroids.get(i));
			
			if (minDistance == -1 || distance < minDistance) {
				closestCentroid = i;
				minDistance = distance;
			}
		}
		
		return closestCentroid;
	}
	
	private int AssignClusters() throws Exception {
		int clusterChanges = 0;
		
		for (int i = 0; i < this.data.getSize(); i++) {
			Iris currentIris = this.data.getIris(i);
			int clusterIndex = GetClosestCentroid(currentIris);
			
			if (clusterIndex >= 0) {
				if (currentIris.getClusterIndex() != clusterIndex) {
					clusterChanges++;
					currentIris.setClusterIndex(clusterIndex);
				}
			}
			else {
				throw new Exception("Invalid Cluster:" + clusterIndex);
			}	
		}
		
		return clusterChanges;
	}
}
