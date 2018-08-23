package ynwa.AI_EFC;

import java.util.Comparator;

public class PlayerCompare implements Comparator<UFCPlayer> {
	public int compare(UFCPlayer arg0, UFCPlayer arg1) {
		return arg0.Avarage > arg1.Avarage ? -1 : ( arg0.Avarage < arg1.Avarage ? 1 : 0);
	}
}
