
public class Iris {
	private double sepalLength;
	
	private double sepalWidth;
	
	private double petalLength;
	
	private double petalWidth;

	private int clusterIndex;
	
	public double getSepalLength() {
		return sepalLength;
	}

	public void setSepalLength(double sepalLength) {
		this.sepalLength = sepalLength;
	}

	public double getSepalWidth() {
		return sepalWidth;
	}

	public void setSepalWidth(double sepalWidth) {
		this.sepalWidth = sepalWidth;
	}

	public double getPetalLength() {
		return petalLength;
	}

	public void setPetalLength(double petalLength) {
		this.petalLength = petalLength;
	}

	public double getPetalWidth() {
		return petalWidth;
	}

	public void setPetalWidth(double petalwidth) {
		this.petalWidth = petalwidth;
	}
	
	public int getClusterIndex() {
		return clusterIndex;
	}

	public void setClusterIndex(int clusterIndex) {
		this.clusterIndex = clusterIndex;
	}

	@Override
	public String toString() {
		return "SepalLength;" + this.sepalLength + 
				"SepalWidth;" + this.sepalWidth + 
				"PetalLength;" + this.petalLength + 
				"PetalWidth;" + this.petalWidth +
				"Cluster Index;" + this.clusterIndex;
	}
	
	public String OutputToFile() {
		return this.sepalLength + 
				"	" + this.sepalWidth + 
				"	" + this.petalLength + 
				"	" + this.petalWidth +
				"	" + this.clusterIndex;
	}
}
