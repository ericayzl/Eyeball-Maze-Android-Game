package nz.ac.ara.eyl.gamemodel;

public class Goal {
	private int row;
	private int column;
	// public boolean isComplete = false;
	
	public Goal(int newRow, int newColumn) {
		row = newRow;
		column = newColumn;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.column;
	}
}
