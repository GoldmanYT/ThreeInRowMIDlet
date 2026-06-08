package com.goldman;

public class Cell {
	public static final int YELLOW = 0;
	public static final int WHITE = 1;
	public static final int BLUE = 2;
	public static final int RED = 3;
	public static final int PURPLE = 4;
	public static final int ORANGE = 5;
	public static final int GREEN = 6;
	public static final int COLOR_COUNT = 7;
	public static final int NONE = 8;

	public static final int CELL_SIZE = 100;
	public static final int CELL_SPEED = 20;

	public static final int DESTROY_SIZE = 80;
	public static final int DESTROY_SPEED = 10;

	private int color;
	private int bonus;
	private boolean empty;
	private int x;
	private int y;
	private int targetX;
	private int targetY;
	private int destroyProgress;

	Cell(int color, int row, int col) {
		setColor(color);
		setBonus(Bonus.NONE);
		setPos(row, col);
		setTarget(row, col);
		empty = false;
		destroyProgress = 0;
	}

	Cell(int color, Cell cell) {
		setColor(color);
		setBonus(Bonus.NONE);
		setX(cell.getX());
		setY(cell.getY());
		setTargetX(cell.getTargetX());
		setTargetY(cell.getTargetY());
		empty = false;
		destroyProgress = 0;
	}

	public void setPos(int row, int col) {
		x = col * CELL_SIZE;
		y = row * CELL_SIZE;
	}

	public void setTarget(int targetRow, int targetCol) {
		targetX = targetCol * CELL_SIZE;
		targetY = targetRow * CELL_SIZE;
	}

	public void setDeltaRow(int deltaRow) {
		this.y = targetY + deltaRow * CELL_SIZE;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setBonus(int bonus) {
		if (bonus < 0 || bonus >= Bonus.COUNT)
			return;

		this.bonus = bonus;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setTargetX(int targetX) {
		this.targetX = targetX;
	}

	public void setTargetY(int targetY) {
		this.targetY = targetY;
	}

	public int getColor() {
		return color;
	}

	public int getBonus() {
		return bonus;
	}

	public int getX(int cellSize) {
		return x * cellSize / CELL_SIZE;
	}

	public int getY(int cellSize) {
		return y * cellSize / CELL_SIZE;
	}

	public boolean isEmpty() {
		return empty;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public boolean animFinished() {
		return x == targetX && y == targetY;
	}

	public boolean destroyFinished() {
		return destroyProgress >= DESTROY_SIZE || !empty;
	}

	public int getFrameIndex() {
		int horizontalCount = SpriteData.DATA[ThreeInRowCanvas.GEM].horizontalCount;
		return color * horizontalCount
				+ Math.min(horizontalCount * destroyProgress / DESTROY_SIZE, horizontalCount - 1);
	}

	public boolean update() {
		boolean animFinished = true;

		if (x > targetX) {
			x = Math.max(targetX, x - CELL_SPEED);
			animFinished = false;
		}
		if (x < targetX) {
			x = Math.min(targetX, x + CELL_SPEED);
			animFinished = false;
		}
		if (y > targetY) {
			y = Math.max(targetY, y - CELL_SPEED);
			animFinished = false;
		}
		if (y < targetY) {
			y = Math.min(targetY, y + CELL_SPEED);
			animFinished = false;
		}
		if (animFinished && empty && destroyProgress < DESTROY_SIZE) {
			destroyProgress += DESTROY_SPEED;
			animFinished = false;
		}

		return animFinished;
	}
}