import java.util.List;

public class KMeansOutput {
	private IrisData data;
	
	private List<Iris> centroids;

	private int numberOfIterations;
	
	private double SSE;
	
	public double getSSE() {
		return SSE;
	}

	public void setSSE(double sSE) {
		SSE = sSE;
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	public IrisData getData() {
		return data;
	}

	public void setData(IrisData data) {
		this.data = data;
	}

	public List<Iris> getCentroids() {
		return centroids;
	}

	public void setCentroids(List<Iris> centroids) {
		this.centroids = centroids;
	}
	
	public void PrintOutput() {
		System.out.println("Number of inner iterations:" + this.getNumberOfIterations());
		for (int i = 0; i < this.centroids.size(); i++) {
			int clusterIndex = i;
			long clusterSize = this.data.getIrisList().stream()
					.filter(iris -> clusterIndex == iris.getClusterIndex()).count();
			
			System.out.print("Centroid:" + i + ";Cluster Size: " + clusterSize + ";SSE;" + this.getSSE() + ";");
			System.out.println(this.centroids.get(i));
		}
	}
}
