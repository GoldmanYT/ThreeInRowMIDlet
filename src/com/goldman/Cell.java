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

	public static final int CELL_SIZE = 100;
	public static final int CELL_SPEED = 10;

	private int color;
	private int bonus;
	private boolean empty;
	private int x;
	private int y;
	private int targetX;
	private int targetY;

	Cell(int color, int bonus, int row, int col) {
		setColor(color);
		setBonus(bonus);
		setPos(row, col);
		setTarget(row, col);
		empty = false;
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
		if (color < 0 || color >= COLOR_COUNT)
			return;

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

	public boolean update() {
		boolean moved = false;

		if (x > targetX) {
			x = Math.max(targetX, x - CELL_SPEED);
			moved = true;
		}
		if (x < targetX) {
			x = Math.min(targetX, x + CELL_SPEED);
			moved = true;
		}
		if (y > targetY) {
			y = Math.max(targetY, y - CELL_SPEED);
			moved = true;
		}
		if (y < targetY) {
			y = Math.min(targetY, y + CELL_SPEED);
			moved = true;
		}

		return moved;
	}
}