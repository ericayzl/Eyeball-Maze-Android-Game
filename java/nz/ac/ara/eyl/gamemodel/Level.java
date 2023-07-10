package nz.ac.ara.eyl.gamemodel;

import java.util.ArrayList;

public class Level implements IGoalHolder, ISquareHolder, IEyeballHolder, IMoving {
	private int width;
	private int height;
	private ArrayList<Goal> goals = new ArrayList<>();
	private ArrayList<Goal> completedGoals = new ArrayList<>();
	private Square[][] levelGrid;
	private Eyeball eyeball;
	private boolean eyeballIsOnGoal = false;
	
	// final int SIZE_TO_INDEX_CONVERTER = 1;
	
	public Level(int newHeight, int newWidth) {
		width = newWidth;
		height = newHeight;
		levelGrid = new Square[height][width];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/*
	 * public void setWidth(int levelWidth) { width = levelWidth; }
	 * 
	 * public void setHeight(int levelHeight) { height = levelHeight; }
	 * 
	 * public void setLevelGrid() { levelGrid = new Square[height][width]; }
	 */
	
	private void incorrectDimensionsChecker(int row, int column) {
		if (row >= height || column >= width) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void addGoal(int row, int column) {		
		incorrectDimensionsChecker(row, column);
		Goal newGoal = new Goal(row, column);
		goals.add(newGoal);		
	}

	@Override
	public int getGoalCount() {
		return goals.size();
	}

	@Override
	public boolean hasGoalAt(int targetRow, int targetColumn) {
		boolean hasGoal = false;
		for (Goal targetGoal : goals) {
			if ((targetGoal.getRow() == targetRow) && (targetGoal.getColumn() == targetColumn)) {
				hasGoal = true;
				break;
			}
		}
		return hasGoal;
	}
	
	private Goal getGoalFromGoals(int targetRow, int targetColumn) {
		Goal theGoal = null;
		for (Goal targetGoal : goals) {
			if ((targetGoal.getRow() == targetRow) && (targetGoal.getColumn() == targetColumn)) {
				theGoal = targetGoal;
				break;
			}
		}
		return theGoal;
	}
	
	/*
	 * private void removeGoalFromUncompletedGoals(Goal targetGoal) { int goalIndex
	 * = goals.indexOf(targetGoal); goals.remove(goalIndex); }
	 * 
	 * private void addGoalToCompletedGoals(Goal targetGoal) {
	 * goals.add(targetGoal); }
	 */
	
	private void updateGoalArrays(Goal targetGoal) {
		int indexOfCurrentGoal = goals.indexOf(targetGoal);
		Goal removedGoal = goals.remove(indexOfCurrentGoal);
		completedGoals.add(removedGoal);
	}
	
	private void actionsIfOnGoal(int targetRow, int targetColumn) {
		if (hasGoalAt(targetRow, targetColumn)) {
			Goal targetGoal = getGoalFromGoals(targetRow, targetColumn);
			updateGoalArrays(targetGoal);
			eyeballIsOnGoal = true;
		}
	}

	@Override
	public int getCompletedGoalCount() {
		/*
		 * int completedGoalCount = 0; for (Goal targetGoal : goals) { if
		 * (targetGoal.isComplete) { completedGoalCount += 1; } }
		 */
		return completedGoals.size();
	}

	@Override
	public void addSquare(Square square, int row, int column) {
		incorrectDimensionsChecker(row, column);
		levelGrid[row][column] = square;		
	}

	@Override
	public Color getColorAt(int row, int column) {
		return levelGrid[row][column].getColor();
	}

	@Override
	public Shape getShapeAt(int row, int column) {
		return levelGrid[row][column].getShape();
	}

	@Override
	public void addEyeball(int row, int column, Direction direction) {
		incorrectDimensionsChecker(row, column);
		eyeball = new Eyeball(row, column, direction);		
	}

	@Override
	public int getEyeballRow() {
		return eyeball.getRow();
	}

	@Override
	public int getEyeballColumn() {
		return eyeball.getColumn();
	}

	@Override
	public Direction getEyeballDirection() {
		// TODO Auto-generated method stub
		return eyeball.getDirection();
	}
	
	
	private Square obtainSquare(int row, int column) { 
		return levelGrid[row][column]; 
	}

	
	/*
	 * private boolean isCorrectDirection(int destinationRow, int destinationColumn)
	 * { int sourceRow = eyeball.getRow(); int sourceColumn = eyeball.getColumn();
	 * Direction sourceDirection = eyeball.getDirection();
	 * 
	 * boolean correctDirection = false; switch (sourceDirection) { case UP: if
	 * (destinationRow <= sourceRow) { correctDirection = true; } break; case DOWN:
	 * if (destinationRow >= sourceRow) { correctDirection = true; } break; case
	 * LEFT: if (destinationColumn <= sourceColumn) { correctDirection = true; }
	 * break; case RIGHT: if (destinationColumn >= sourceColumn) { correctDirection
	 * = true; } break; } return correctDirection; }
	 */
	
	private boolean isSameShape(int destinationRow, int destinationColumn) {
		boolean sameShape = false;
		Square destinationSquare = obtainSquare(destinationRow, destinationColumn);
		Square sourceSquare = obtainSquare(eyeball.getRow(), eyeball.getColumn());
		if (destinationSquare.getShape() == sourceSquare.getShape()) {
			sameShape = true;
		}
		return sameShape;
	}
	private boolean isSameColor(int destinationRow, int destinationColumn) {
		boolean sameColor = false;
		Square destinationSquare = obtainSquare(destinationRow, destinationColumn);
		Square sourceSquare = obtainSquare(eyeball.getRow(), eyeball.getColumn());
		if (destinationSquare.getColor() == sourceSquare.getColor()) {
			sameColor = true;
		}
		return sameColor;
	}
	
	private boolean isSameColorOrSameShape(int destinationRow, int destinationColumn) {
		boolean correctCondition = false;
		if (isSameShape(destinationRow, destinationColumn) || isSameColor(destinationRow, destinationColumn)) {
			correctCondition = true;
		}
		return correctCondition;
	}
	

	@Override
	public boolean canMoveTo(int destinationRow, int destinationColumn) {
		boolean canMove = false;
		if (isSameColorOrSameShape(destinationRow, destinationColumn) && isDirectionOK(destinationRow, destinationColumn) && hasBlankFreePathTo(destinationRow, destinationColumn)) {
			canMove = true;
		}
		return canMove;
	}

	@Override
	public Message MessageIfMovingTo(int destinationRow, int destinationColumn) {
		Message returnMessage;
		if (isSameShape(destinationRow, destinationColumn) || isSameColor(destinationRow, destinationColumn)) {
			returnMessage = Message.OK;
		} else {
			returnMessage = Message.DIFFERENT_SHAPE_OR_COLOR;
		}
		return returnMessage;
	}
	
	private boolean isNotBackwards(int destinationRow, int destinationColumn) {
		int sourceRow = eyeball.getRow();
		int sourceColumn = eyeball.getColumn();		
		Direction sourceDirection = eyeball.getDirection();
		
		boolean notBackwards = false;
		switch (sourceDirection) {
			case UP:
				if (destinationRow <= sourceRow) {
					notBackwards = true;
				}
				break;
			case DOWN:
				if (destinationRow >= sourceRow) {
					notBackwards = true;
				}
				break;
			case LEFT:
				if (destinationColumn <= sourceColumn) {
					notBackwards = true;
				}
				break;
			case RIGHT:
				if (destinationColumn >= sourceColumn) {
					notBackwards = true;
				}
				break;
		}		
		return notBackwards;
	}
	
	private boolean isNotDiagonal(int destinationRow, int destinationColumn) {
		boolean notDiagonal = false;
		if (destinationRow == eyeball.getRow() || destinationColumn == eyeball.getColumn()) {
			notDiagonal = true;
		}
		return notDiagonal;
	}

	@Override
	public boolean isDirectionOK(int destinationRow, int destinationColumn) {
		boolean notBackwards = isNotBackwards(destinationRow, destinationColumn);
		boolean notDiagonal = isNotDiagonal(destinationRow, destinationColumn);
		boolean correctDirection = false;
		if (notBackwards && notDiagonal) {
			correctDirection = true;
		}
		return correctDirection;
	}

	@Override
	public Message checkDirectionMessage(int destinationRow, int destinationColumn) {
		Message returnMessage;
		if (isNotDiagonal(destinationRow, destinationColumn)) {
			if (isNotBackwards(destinationRow, destinationColumn)) {
				returnMessage = Message.OK;
			} else {
				returnMessage = Message.BACKWARDS_MOVE;
			}
		} else {
			returnMessage = Message.MOVING_DIAGONALLY;
		}
		return returnMessage;
	} 
	
	private boolean isRowNoBlank(int destinationRow) {
		boolean rowNoBlank = false;
		if (destinationRow == eyeball.getRow()) {
			rowNoBlank = true;
		} else if (destinationRow > eyeball.getRow()) {
			for (int i = eyeball.getRow() + 1; i <= destinationRow; i++) {
				Square currentSquare = obtainSquare(i, eyeball.getColumn());
				if (currentSquare.getColor() == Color.BLANK) {
					rowNoBlank = false;
					break;
				} else {
					rowNoBlank = true;
				}
			}
		} else {
			for (int i = eyeball.getRow() - 1; i >= destinationRow; i--) {
				Square currentSquare = obtainSquare(i, eyeball.getColumn());
				if (currentSquare.getColor() == Color.BLANK) {
					rowNoBlank = false;
					break;
				} else {
					rowNoBlank = true;
				}
			}
		}
		return rowNoBlank;
	}
	private boolean isColumnNoBlank(int destinationColumn) {
		boolean columnNoBlank = false;
		if (destinationColumn == eyeball.getColumn()) {
			columnNoBlank = true;
		} else if (destinationColumn > eyeball.getColumn()) {
			for (int i = eyeball.getColumn() + 1; i <= destinationColumn; i++) {
				Square currentSquare = obtainSquare(eyeball.getRow(), i);
				if (currentSquare.getColor() == Color.BLANK) {
					columnNoBlank = false;
					break;
				} else {
					columnNoBlank = true;
				}
			}
		} else {
			for (int i = eyeball.getColumn() - 1; i >= destinationColumn; i--) {
				Square currentSquare = obtainSquare(eyeball.getRow(), i);
				if (currentSquare.getColor() == Color.BLANK) {
					columnNoBlank = false;
					break;
				} else {
					columnNoBlank = true;
				}
			}
		}
		return columnNoBlank;
	}

	@Override
	public boolean hasBlankFreePathTo(int destinationRow, int destinationColumn) {
		boolean noBlankRowAndColumn = false;
		boolean rowNoBlank = isRowNoBlank(destinationRow);
		boolean columnNoBlank = isColumnNoBlank(destinationColumn);
		if (rowNoBlank && columnNoBlank) {
			noBlankRowAndColumn = true;
		}		
		return noBlankRowAndColumn;
	}

	@Override
	public Message checkMessageForBlankOnPathTo(int destinationRow, int destinationColumn) {
		Message returnMessage;
		if (hasBlankFreePathTo(destinationRow, destinationColumn)) {
			returnMessage = Message.OK;
		} else {
			returnMessage = Message.MOVING_OVER_BLANK;
		}
		return returnMessage;
	}
	
	private Direction determineDirection(int destinationRow, int destinationColumn) {
		Direction newDirection;
		if (destinationRow < eyeball.getRow()) {
			newDirection = Direction.UP;
		} else if (destinationRow > eyeball.getRow()) {
			newDirection = Direction.DOWN;
		} else if (destinationColumn < eyeball.getColumn()) {
			newDirection = Direction.LEFT;
		} else {
			newDirection = Direction.RIGHT;
		}
		return newDirection;
	}
	
	private void setSquareToBlankColorAndShape(int targetRow, int targetColumn) {
		levelGrid[targetRow][targetColumn] = new BlankSquare();
	}

	@Override
	public void moveTo(int destinationRow, int destinationColumn) {
		if (eyeballIsOnGoal) {
			setSquareToBlankColorAndShape(eyeball.getRow(), eyeball.getColumn());
			eyeballIsOnGoal = false;
		}
		
		if (canMoveTo(destinationRow, destinationColumn)) {			
			eyeball.setDirection(determineDirection(destinationRow, destinationColumn));
			eyeball.setRow(destinationRow);
			eyeball.setColumn(destinationColumn);
			actionsIfOnGoal(destinationRow, destinationColumn);
		}		
	}
}
