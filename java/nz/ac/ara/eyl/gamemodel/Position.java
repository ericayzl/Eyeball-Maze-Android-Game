package nz.ac.ara.eyl.gamemodel;

public class Position {
	private int row;
	private int column;
	
	public Position(int newRow, int newColumn) {
		row = newRow;
		column = newColumn;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
}
