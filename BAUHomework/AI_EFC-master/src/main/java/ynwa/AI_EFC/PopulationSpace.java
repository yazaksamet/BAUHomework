package ynwa.AI_EFC;

import java.util.List;

public class PopulationSpace {
	List<UFCCombination> guardSpace;
	
	List<UFCCombination> forwardSpace;
	
	List<UFCCombination> centerSpace;

	List<UFCPlayer> guardList;
	
	List<UFCPlayer> forwardList;
	
	List<UFCPlayer> centerList;
	
	public List<UFCPlayer> getGuardList() {
		return guardList;
	}

	public void setGuardList(List<UFCPlayer> guardList) {
		this.guardList = guardList;
	}

	public List<UFCPlayer> getForwardList() {
		return forwardList;
	}

	public void setForwardList(List<UFCPlayer> forwardList) {
		this.forwardList = forwardList;
	}

	public List<UFCPlayer> getCenterList() {
		return centerList;
	}

	public void setCenterList(List<UFCPlayer> centerList) {
		this.centerList = centerList;
	}

	public List<UFCCombination> getGuardSpace() {
		return guardSpace;
	}

	public void setGuardSpace(List<UFCCombination> guardSpace) {
		this.guardSpace = guardSpace;
	}

	public List<UFCCombination> getForwardSpace() {
		return forwardSpace;
	}

	public void setForwardSpace(List<UFCCombination> forwardSpace) {
		this.forwardSpace = forwardSpace;
	}

	public List<UFCCombination> getCenterSpace() {
		return centerSpace;
	}

	public void setCenterSpace(List<UFCCombination> centerSpace) {
		this.centerSpace = centerSpace;
	}
}
