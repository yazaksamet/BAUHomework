import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class StatisticsHelper {
	public static double logBase(double n, int base) {
		return Math.log10(n) / Math.log10(base);
	}
	
	public static double log2(double n) {
		return logBase(n,2);
	}
	
	public static double calcEntropy(List<String> inputList) {
		int inputLength = inputList.size();
		Hashtable<String, Integer> frequency = getFrequency(inputList);
		
		double entropy = 0;
		
		for (int attributeFreq : frequency.values()) {
			double attrProb = (double) attributeFreq / inputLength;
		    entropy -= attrProb * log2(attrProb);
		}
		
		return entropy;
	}
	
	public static Hashtable<String, Integer> getFrequency(List<String> inputList) {
		Hashtable<String, Integer> frequency = new Hashtable<String, Integer>();
		int inputLength = inputList.size();
		
		for(int i = 0; i < inputLength; i++) {
			String attributeVal = inputList.get(i);
			if (frequency.containsKey(attributeVal)) {
				frequency.put(attributeVal, frequency.get(attributeVal) + 1);
			}
			else {
				frequency.put(attributeVal, 1);
			}
		}
		return frequency;
	}
	
	public static Hashtable<String, Integer> getFrequencyIndex(List<String> inputList) {
		Hashtable<String, Integer> frequency = new Hashtable<String, Integer>();
		int inputLength = inputList.size();
		int frequencyCounter = 0;
		
		for(int i = 0; i < inputLength; i++) {
			String attributeVal = inputList.get(i);
			if (!frequency.containsKey(attributeVal)) {
				frequency.put(attributeVal, frequencyCounter);
				frequencyCounter++;
			}
		}
		return frequency;
	}
	
	public static int[][] getFrequencyTable(List<String> inputList, List<String> targetClass) {
		int inputLength = inputList.size();
		Hashtable<String, Integer> targetFrequency = getFrequencyIndex(targetClass);
		Hashtable<String, Integer> inputFrequency = getFrequencyIndex(inputList);
		
		int[][] frequencyTable = new int[inputFrequency.size()][targetFrequency.size() + 1];
		
		for (int i = 0; i < inputLength; i++) {
			int inputIndex = inputFrequency.get(inputList.get(i));
			int targetIndex = targetFrequency.get(targetClass.get(i));
			
			frequencyTable[inputIndex][targetIndex]++;
			frequencyTable[inputIndex][targetFrequency.size()]++;
		}
		
		return frequencyTable;
	}
	
	public static double calcEntropyWithTarget(List<String> inputList, List<String> targetClass) throws Exception {
		int inputLength = inputList.size();
		int targetLength = targetClass.size();
		
		if (inputLength != targetLength) {
			throw new Exception("attribute and target list size does not match");
		}
		
		int[][] frequencyTable = getFrequencyTable(inputList, targetClass);
		
		double entropy = calcEntropyWithFrequencyTable(frequencyTable, inputLength);
				
		return entropy;
	}
	
	public static double calcEntropyWithFrequencyTable(int[][] frequencyTable, int inputLength) {
		double entropy = 0;
		
		for (int i = 0; i < frequencyTable.length; i++) {
			int[] inputRow = frequencyTable[i];
			int targetFreqLength = inputRow.length - 1;
			int targetTotal = inputRow[targetFreqLength];
			double inputEntropy = 0;
			
			for (int j = 0; j < targetFreqLength; j++) {
				double attrProb = (double) inputRow[j] / targetTotal;
				inputEntropy -= attrProb == 0 ? 0 : attrProb * log2(attrProb);
			}
			
			double classPercent = (double) targetTotal / inputLength;
			inputEntropy = (double) classPercent * inputEntropy;
			entropy += inputEntropy;
		}
				
		return entropy;
	}
	
	public static ContinousEntopy calcContinousEntropyWithTarget(List<Integer> inputList, List<String> targetClass) throws Exception {
		double entropy = 0;
		double splitter = 0;
		
		int inputLength = inputList.size();
		int targetLength = targetClass.size();
		
		if (inputLength != targetLength) {
			throw new Exception("attribute and target list size does not match");
		}
		
		List<Double> avarageList = new ArrayList<Double>();
		
		Collections.sort(inputList);
		
		for (int i = 0; i < inputLength - 1; i++) {
			if (inputList.get(i) != inputList.get(i + 1)) {
				double avarage = (double) (inputList.get(i) + inputList.get(i + 1)) / 2;
				
				if (!avarageList.contains(avarage)) {
					avarageList.add(avarage);
				}
			}
		}
		
		Hashtable<String, Integer> targetFrequency = getFrequencyIndex(targetClass);
		
		for (int i = 0; i < avarageList.size(); i++) {
			int[][] frequencyTable = new int[2][targetFrequency.size() + 1];
			double avarageValue = avarageList.get(i);
			
			for (int input = 0; input < inputLength; input++) {
				int inputValue = inputList.get(input);
				int inputIndex = 0;
				
				String targetValue = targetClass.get(input);
				int targetIndex = targetFrequency.get(targetValue);
				
				if (inputValue > avarageValue) {
					inputIndex = 1;
				}
				
				frequencyTable[inputIndex][targetIndex]++;
				frequencyTable[inputIndex][targetFrequency.size()]++;	
			}
			
			double innerEntropy = calcEntropyWithFrequencyTable(frequencyTable, inputLength);
			
			if (entropy == 0 || innerEntropy < entropy) {
				entropy = innerEntropy;
				splitter = avarageValue;
			}
		}
		
		return new ContinousEntopy(entropy, splitter);
	}
	
}
