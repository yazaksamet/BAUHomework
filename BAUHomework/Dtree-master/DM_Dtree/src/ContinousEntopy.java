
public class ContinousEntopy {
	private double entropy;
	
	private double splitter;
	
	public ContinousEntopy(double entropy, double splitter) {
		this.entropy = entropy;
		this.splitter = splitter;
	}
	
	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getSplitter() {
		return splitter;
	}

	public void setSplitter(double splitter) {
		this.splitter = splitter;
	}
	
	@Override
	public String toString() {
		return "Entropy:" + this.entropy + ", Splitter:" + this.splitter; 
	}
}
