package ynwa.AI_EFC;

import java.util.Comparator;

public class PlayerCountCompare implements Comparator<UFCPlayer> {
	public int compare(UFCPlayer arg0, UFCPlayer arg1) {
		return arg0.SelectCount > arg1.SelectCount ? -1 : ( arg0.SelectCount < arg1.SelectCount ? 1 : 0);
	}
}
