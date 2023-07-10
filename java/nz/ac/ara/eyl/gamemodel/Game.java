package nz.ac.ara.eyl.gamemodel;

import java.util.ArrayList;

public final class Game implements ILevelHolder, IGoalHolder, ISquareHolder, IEyeballHolder, IMoving {
	private ArrayList<Level> levels = new ArrayList<>();
	private int targetLevel;
	
	private final int SIZE_TO_INDEX_CONVERTER = 1;
	
	@Override
	public void addLevel(int height, int width) {
		Level newLevel = new Level(height, width);
		/*
		 * newLevel.setHeight(height); newLevel.setWidth(width);
		 * newLevel.setLevelGrid();
		 */
		this.levels.add(newLevel);
		int levelCount = getLevelCount();
		targetLevel = levelCount - SIZE_TO_INDEX_CONVERTER;		
	}
	
	@Override
	public int getLevelWidth() {		
		return levels.get(targetLevel).getWidth();
	}
	
	@Override
	public int getLevelHeight() {
		return levels.get(targetLevel).getHeight();
	}
	
	@Override
	public void setLevel(int levelNumber) {
		if (levelNumber >= levels.size()) {
			throw new IllegalArgumentException();
		}
		targetLevel = levelNumber;
		
	}
	
	@Override
	public int getLevelCount() {
		return levels.size();
	}
	
	@Override
	public void addGoal(int row, int column) {
		levels.get(targetLevel).addGoal(row, column);		
	}
	
	@Override
	public int getGoalCount() {
		return levels.get(targetLevel).getGoalCount();
	}
	
	@Override
	public boolean hasGoalAt(int targetRow, int targetColumn) {
		return levels.get(targetLevel).hasGoalAt(targetRow, targetColumn);
	}
	
	@Override
	public int getCompletedGoalCount() {
		return levels.get(targetLevel).getCompletedGoalCount();
	}

	@Override
	public void addSquare(Square square, int row, int column) {
		levels.get(targetLevel).addSquare(square, row, column);
		
	}

	@Override
	public Color getColorAt(int row, int column) {
		return levels.get(targetLevel).getColorAt(row, column);
	}

	@Override
	public Shape getShapeAt(int row, int column) {
		return levels.get(targetLevel).getShapeAt(row, column);
	}

	@Override
	public void addEyeball(int row, int column, Direction direction) {
		levels.get(targetLevel).addEyeball(row, column, direction);		
	}

	@Override
	public int getEyeballRow() {
		return levels.get(targetLevel).getEyeballRow();
	}

	@Override
	public int getEyeballColumn() {
		return levels.get(targetLevel).getEyeballColumn();
	}

	@Override
	public Direction getEyeballDirection() {
		return levels.get(targetLevel).getEyeballDirection();
	}

	@Override
	public boolean canMoveTo(int destinationRow, int destinationColumn) {
		return levels.get(targetLevel).canMoveTo(destinationRow, destinationColumn);
	}

	@Override
	public Message MessageIfMovingTo(int destinationRow, int destinationColumn) {		
		return levels.get(targetLevel).MessageIfMovingTo(destinationRow, destinationColumn);
	}

	@Override
	public boolean isDirectionOK(int destinationRow, int destinationColumn) {
		return levels.get(targetLevel).isDirectionOK(destinationRow, destinationColumn);
	}

	/*public boolean isShapeDirectionOK(int destinationRow, int destinationColumn) {
		return levels.get(targetLevel).isSameColorOrSameShape(destinationRow, destinationColumn);
	}*/

	@Override
	public Message checkDirectionMessage(int destinationRow, int destinationColumn) {
		return levels.get(targetLevel).checkDirectionMessage(destinationRow, destinationColumn);
	}

	@Override
	public boolean hasBlankFreePathTo(int destinationRow, int destinationColumn) {
		return levels.get(targetLevel).hasBlankFreePathTo(destinationRow, destinationColumn);
	}

	@Override
	public Message checkMessageForBlankOnPathTo(int destinationRow, int destinationColumn) {
		return levels.get(targetLevel).checkMessageForBlankOnPathTo(destinationRow, destinationColumn);
	}

	@Override
	public void moveTo(int destinationRow, int destinationColumn) {
		levels.get(targetLevel).moveTo(destinationRow, destinationColumn);
	}

}
