
public class AppMain {

	public static void main(String[] args) throws Exception {
		ID3 tree = new ID3();		
		tree.generateTree();
		tree.PrintTree();
		
		tree.generateStatistics();
		tree.printStatistics();
	}

	
}
