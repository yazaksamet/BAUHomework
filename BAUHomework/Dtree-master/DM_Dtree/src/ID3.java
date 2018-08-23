import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class ID3 {
	private BankData data;
	
	private Tree root;
	
	private String defaultLeaf;
	
	private int maxDepth = 7;
	
	private int correctTrainCount;
	
	private int correctTestCount;
	
	private int truePositive;
	
	private int trueNegative;
	
	private int falsePositive;
	
	private int falseNegative;
	
	public BankData getData() {
		return data;
	}

	public void setData(BankData data) {
		this.data = data;
	}

	public Tree getRoot() {
		return root;
	}

	public void setRoot(Tree root) {
		this.root = root;
	}

	public ID3() throws FileNotFoundException {
		this.data = new BankData();
	}
	
	public void generateTree() throws Exception {
		double targetEntropy = getEntropy(this.data.getTargetField());
		System.out.println("Target Class Entropy:" + targetEntropy);
		
		defaultLeaf = getDefaultLeaf(this.data.getBankList());
		this.root = generateRecursively(this.data.getBankList(), this.data.getFieldList(), 0, "");
	}
	
	private String getDefaultLeaf(List<Bank> dataSet) throws Exception {
		List<String> targetValues = getTargetValues(dataSet);
		Hashtable<String, Integer> targetFrequencyMap = StatisticsHelper.getFrequency(targetValues);
		String defaultKey = "";
		int maxFieldNum = 0;
		
		Enumeration<String> enumKeys = targetFrequencyMap.keys();
		
		while (enumKeys.hasMoreElements()) {
			String key = enumKeys.nextElement();
			
			if (targetFrequencyMap.get(key) > maxFieldNum) {
				defaultKey = key;
				maxFieldNum = targetFrequencyMap.get(key);
			}
		}
		
		return defaultKey;
	}
	
	private List<Bank> filterDataSet(List<Bank> dataSet, String fieldName, String filterValue,int operator) throws Exception {
		List<Bank> newList = new ArrayList<Bank>();
		
		for(Bank b : dataSet) {
			boolean addToNewList = false;
			
			if (operator == 3) {
				String fieldValue = b.getClass().getDeclaredField(fieldName).get(b).toString();
				
				if (fieldValue.equals(filterValue)) {
					addToNewList = true;
				}
			}
			else {
				Integer fieldValue = b.getClass().getDeclaredField(fieldName).getInt(b);
				double filterValueInt = Double.parseDouble(filterValue);
				
				if (operator == 2) {
					if (fieldValue > filterValueInt) {
						addToNewList = true;
					}
				}
				else {
					if (fieldValue <= filterValueInt) {
						addToNewList = true;
					}
				}
			}
			
			if (addToNewList) {
				newList.add(b);
			}
		}
		return newList;
	}
	
	private Tree generateRecursively(List<Bank> dataSet, List<Field> fieldList, int level, String parentSelection) throws Exception {
		if (dataSet == null || dataSet.size() == 0) {
			return null;
		}
		
		int newLevel = ++level;
		
		Tree node = new Tree();
		node.setParentCondition(parentSelection);
		
		List<String> targetValues = getTargetValues(dataSet);
		Hashtable<String, Integer> targetFrequencyMap = StatisticsHelper.getFrequencyIndex(targetValues);
		
		if (targetFrequencyMap.size() == 1) {
			node.setLeaf(true);
			node.setLeafValue(targetFrequencyMap.keys().nextElement());
			return node;
		}
		else if (targetFrequencyMap.size() == 0) {
			throw new Exception("No element found");
		}
		
		if (level > maxDepth) {
			node.setLeaf(true);
			node.setLeafValue(defaultLeaf);
			return node;
		}
		
		Field bestMatch = findBestSplitter(dataSet, fieldList);
		
		if (bestMatch == null) {
			node.setLeaf(true);
			node.setLeafValue(defaultLeaf);
			return node;
		}
		
		node.setField(bestMatch);
		
		if (bestMatch.isContinous()) {
			node.addPossibleSelection("Yes");
			node.addPossibleSelection("No");
			
			List<Field> newFieldList = fieldList.stream().filter(p -> !p.getName().equals(bestMatch.getName())).collect(Collectors.toList());
			
			List<Bank> yesList = filterDataSet(dataSet, bestMatch.getName(), String.valueOf(bestMatch.getSplitter()), 2);
			List<Bank> noList = filterDataSet(dataSet, bestMatch.getName(), String.valueOf(bestMatch.getSplitter()), 1);
			
			Tree yesNode = generateRecursively(yesList, newFieldList, newLevel, "yes");
			Tree noNode = generateRecursively(noList, newFieldList, newLevel, "no");
			
			node.addChild(yesNode);
			node.addChild(noNode);
		}
		else {
			List<String> fieldValues = new ArrayList<String>();
			
			for(Bank b : dataSet) {
				String fieldValue = b.getClass().getDeclaredField(bestMatch.getName()).get(b).toString();
				fieldValues.add(fieldValue);
			}
			
			Hashtable<String, Integer> frequencyMap = StatisticsHelper.getFrequencyIndex(fieldValues);
			Enumeration<String> enumKey = frequencyMap.keys();
			while (enumKey.hasMoreElements()) {
				String key = enumKey.nextElement();
				node.addPossibleSelection(key);
				
				List<Field> newFieldList = fieldList.stream().filter(p -> !p.getName().equals(bestMatch.getName())).collect(Collectors.toList());
				List<Bank> subDataSet = filterDataSet(dataSet, bestMatch.getName(), key, 3);

				Tree child = generateRecursively(subDataSet, newFieldList, newLevel, key);
				node.addChild(child);
			}
		}
		
		return node;
	}
	
	private List<String> getTargetValues(List<Bank> dataSet) throws Exception {
		List<String> fieldValues = new ArrayList<String>();
		
		for(Bank b : dataSet) {
			String fieldValue = b.getClass().getDeclaredField(this.data.getTargetField()).get(b).toString();
			fieldValues.add(fieldValue);
		}
		
		return fieldValues;
	}
	
	private Field findBestSplitter(List<Bank> dataSet, List<Field> fieldList) throws Exception {
		double entropy = 0;
		Field bestMatch = null;
		
		for (Field f:fieldList) {
			if (!f.getName().equals(this.data.getTargetField())) {
				if (f.isContinous()) {
					/*ContinousEntopy cEntropy = getContinousEntropyWithTarget(f.getName());
					
					if (entropy == 0 || cEntropy.getEntropy() < entropy) {
						entropy = cEntropy.getEntropy();
						bestMatch = f;
						bestMatch.setSplitter(cEntropy.getSplitter());
					}*/
				}
				else {
					double nEntropy = getEntropyWithTarget(f.getName());
					
					if (entropy == 0 || nEntropy < entropy) {
						entropy = nEntropy;
						bestMatch = f;
					}
				}
			}
		}
		
		return bestMatch;
	}
	
	private double getEntropy(String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		List<String> fieldValues = new ArrayList<String>();
		
		for(Bank b : this.data.getBankList()) {
			String fieldValue = b.getClass().getDeclaredField(fieldName).get(b).toString();
			fieldValues.add(fieldValue);
		}
		
		return StatisticsHelper.calcEntropy(fieldValues);
	}
	
	private double getEntropyWithTarget(String fieldName) throws Exception {
		String targetField = this.data.getTargetField();
		if (fieldName.equals(targetField)) {
			return getEntropy(fieldName);
		}
		
		List<String> fieldValues = new ArrayList<String>();
		List<String> targetValues = new ArrayList<String>();
		
		for(Bank b : this.data.getBankList()) {
			String fieldValue = b.getClass().getDeclaredField(fieldName).get(b).toString();
			fieldValues.add(fieldValue);
			
			String targetValue = b.getClass().getDeclaredField(targetField).get(b).toString();
			targetValues.add(targetValue);
		}
		
		return StatisticsHelper.calcEntropyWithTarget(fieldValues, targetValues);
	}
	
	public ContinousEntopy getContinousEntropyWithTarget(String fieldName) throws Exception {
		String targetField = this.data.getTargetField();
		if (fieldName.equals(targetField)) {
			throw new Exception("continous attr equals to target");
		}
		
		List<Integer> fieldValues = new ArrayList<Integer>();
		List<String> targetValues = new ArrayList<String>();
		
		for(Bank b : this.data.getBankList()) {
			Integer fieldValue = b.getClass().getDeclaredField(fieldName).getInt(b);
			fieldValues.add(fieldValue);
			
			String targetValue = b.getClass().getDeclaredField(targetField).get(b).toString();
			targetValues.add(targetValue);
		}
		
		return StatisticsHelper.calcContinousEntropyWithTarget(fieldValues, targetValues);
	}

	public void PrintTree() {
		PrintTreeRecursively(this.root, 0);
	}
	
	private void PrintTreeRecursively(Tree node, int level) {
		int newLevel = ++level;
		
		if (node.isLeaf()) {
			for (int j = 1; j < level; j++) {
				System.out.print("|	");
			}
			System.out.print(this.data.getTargetField() + "=" + node.getLeafValue());
			System.out.println();
		}
		else {
			
			for (int i = 0; i < node.getChildren().size(); i++) {
				for (int j = 1; j < level; j++) {
					System.out.print("|	");
				}
				
				System.out.println(node.getField().getName() + "=" + node.getPossibleSelection().get(i));
				PrintTreeRecursively(node.getChildren().get(i), newLevel);
			}
		}
	}
	
	public boolean evaluateRecord(Bank record) throws Exception {
		return evaluateRecordRecursively(this.root, record);
	}
	
	private boolean evaluateRecordRecursively(Tree node, Bank record) throws Exception {
		if (node.isLeaf()) {
			return node.getLeafValue().equals(record.y);
		}
		else {
			String nodeValue = record.getClass().getDeclaredField(node.getField().getName()).get(record).toString();
			int nodeIndex = node.getPossibleSelection().indexOf(nodeValue);
			return nodeIndex < 0 ? defaultLeaf.equals(record.y) : evaluateRecordRecursively(node.getChildren().get(nodeIndex), record);
		}
	}
	
	public void generateStatistics() throws Exception {
		int correctTrainRecordCount = 0;
		int correctTestRecordCount = 0;
		
		int trainSize = this.getData().getBankList().size();
		int testSize = this.getData().getTestList().size();
		
		for (int i = 0; i < trainSize; i++) {
			Bank record = this.getData().getBankList().get(i);
			boolean isCorrect = evaluateRecord(record);
			
			if (isCorrect) {
				correctTrainRecordCount++;
			}
			
			//System.out.println(isCorrect + "=>" + record);
		}
		
		for (int i = 0; i < testSize; i++) {
			Bank record = this.getData().getTestList().get(i);
			boolean isCorrect = evaluateRecord(record);
			
			if (isCorrect) {
				correctTestRecordCount++;
			}
			
			if (isCorrect && record.y.equals("yes")) {
				truePositive++;
			}
			else if (isCorrect && record.y.equals("no")) {
				trueNegative++;
			}
			else if (!isCorrect && record.y.equals("yes")) {
				falsePositive++;
			}
			else if (!isCorrect && record.y.equals("no")) {
				falseNegative++;
			}
			
			//System.out.println(isCorrect + "=>" + record);
		}
		
		this.correctTrainCount = correctTrainRecordCount;
		this.correctTestCount = correctTestRecordCount;
	}
	
	public void printStatistics() {
		int trainSize = this.getData().getBankList().size();
		int testSize = this.getData().getTestList().size();
		
		double classificationError = (double) (trainSize - correctTrainCount) / trainSize;
		double classificationCorrect = (double) correctTrainCount / trainSize;
		
		double testError = (double) (testSize - correctTestCount) / testSize;
		double testCorrect = (double) correctTestCount / testSize;
		
		double testPrecision = (double) truePositive / (truePositive + falsePositive);
		double testRecall = (double) truePositive / (truePositive + falseNegative);
		
		System.out.println("Total training record count:" + trainSize );
		System.out.println("Correctly classified training record count:" + correctTrainCount);
		System.out.println("Training Classification error %:" + classificationError * 100);
		System.out.println("Training Classification correct %:" + classificationCorrect * 100);
		System.out.println("");
		
		System.out.println("Total test record count:" + testSize );
		System.out.println("Correctly classified test record count:" + correctTestCount);
		System.out.println("Test Classification error %:" + testError * 100);
		System.out.println("Test Classification correct %:" + testCorrect * 100);
		
		System.out.println("Test Precision:" + testPrecision);
		System.out.println("Test Recall:" + testRecall);
		
	}
}
