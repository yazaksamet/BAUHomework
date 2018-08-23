import java.util.ArrayList;
import java.util.List;

public class Tree {
	private Tree parent;

	private Field field;
	
	private boolean isLeaf;
	
	private String leafValue;
	
	public String getLeafValue() {
		return leafValue;
	}

	public void setLeafValue(String leafValue) {
		this.leafValue = leafValue;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	private List<Tree> children;

	private String parentCondition;

	private List<String> possibleSelection;
	
	public void addPossibleSelection(String value) {
		if (this.possibleSelection == null) {
			this.possibleSelection = new ArrayList<String>();
		}
		
		this.possibleSelection.add(value);
	}
	
	public void addChild(Tree child) {
		if (this.children == null) {
			this.children = new ArrayList<Tree>();
		}
		
		this.children.add(child);
	}
	
	public List<String> getPossibleSelection() {
		return possibleSelection;
	}

	public void setPossibleSelection(List<String> possibleSelection) {
		this.possibleSelection = possibleSelection;
	}

	public String getParentCondition() {
		return parentCondition;
	}

	public void setParentCondition(String parentCondition) {
		this.parentCondition = parentCondition;
	}

	private String value;

	public Tree getParent() {
		return parent;
	}

	public void setParent(Tree parent) {
		this.parent = parent;
	}

	public List<Tree> getChildren() {
		return children;
	}

	public void setChildren(List<Tree> children) {
		this.children = children;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
