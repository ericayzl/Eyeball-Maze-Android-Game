package nz.ac.ara.eyl.gamemodel;

public class Eyeball {
	private int row;
	private int column;
	private Direction direction;
	
	public Eyeball(int newRow, int newColumn, Direction newDirection) {
		row = newRow;
		column = newColumn;
		direction = newDirection;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void setRow(int newRow) {
		row = newRow;
	}
	
	public void setColumn(int newColumn) {
		column = newColumn;
	}
	
	public void setDirection(Direction newDirection) {
		direction = newDirection;
	}
}
