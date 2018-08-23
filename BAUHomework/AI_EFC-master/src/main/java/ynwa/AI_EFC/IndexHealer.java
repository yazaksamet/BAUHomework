package ynwa.AI_EFC;

public class IndexHealer {
	public static int[] FindBetterFour(int i, int j, int k, int l) {
		int[] indexes = new int[] {i, j, k, l};
		int k1 = k, j1 = j, i1 = i;
		
		boolean isLocalStart = (k != -1 && l != -1 && j != -1 && i != -1 && (l - k) == 1)
								|| (k != -1 && j != -1 && i != -1 && l == -1 && (k - j) == 1)
								|| (k == -1 && j != -1 && i != -1 && l == -1 && (j - i) == 1);
		
		if (!isLocalStart) {
			indexes[3]--;
		}
		else {
			k1--;
			
			if (k1 == j1) {
				j1--;
			}
			
			if (i1 == j1) {
				if (i1 > 0) { 
					i1--;
				}
				else {
					return indexes;
				}
			}
			
			indexes = new int[]{i1, j1, k1, l};
		}
		
		return indexes;
	}
	
	public static int[] FindBetterTwo(int i, int j) {
		int[] indexes = new int[4];
		
		boolean isLocalStart = (j - i) == 1;
		
		if (!isLocalStart) {
			indexes = new int[]{i, j - 1};
		}
		else {
			if (i > 0) { 
				i--;
			}
			
			indexes = new int[]{i, j};
		}
		
		return indexes;
	}
}
