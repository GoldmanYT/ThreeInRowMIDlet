package com.goldman;

import java.util.Random;

public class ThreeInRow {
	public static final int WAIT_INPUT = 0;
	public static final int ANIM_FIRST_MOVE = 1;
	public static final int CHECK_MOVE_CORRECT = 2;
	public static final int ANIM_UNDO_MOVE = 3;
	public static final int ANIM_DESTROY_AND_FALL = 4;
	public static final int CHECK_LINES = 5;

	private static Cell[][] field;
	private static Random random;
	private static int selectedRow;
	private static int selectedCol;
	private static int nextRow;
	private static int nextCol;
	private static int state;
	private static int score;

	ThreeInRow() {
		field = new Cell[8][8];
		random = new Random();
		resetSelected();
		state = WAIT_INPUT;
		score = 0;

		for (int row = 0; row < field.length; row++) {
			for (int col = 0; col < field[row].length; col++) {
				field[row][col] = getRandomCell(row, col);
			}
		}

		while (markLines()) {
			destroyAndFall();
		}

		for (int i = 0; i < field.length * Cell.CELL_SIZE / Cell.CELL_SPEED; i++) {
			for (int row = 0; row < field.length; row++) {
				for (int col = 0; col < field[row].length; col++) {
					field[row][col].update();
				}
			}
		}
	}

	public static Cell[][] getField() {
		return field;
	}

	public static void select(int row, int col) {
		if (row < 0 || row >= field.length || col < 0 || col >= field[0].length || state != WAIT_INPUT)
			return;

		int deltaRow = selectedRow - row;
		int deltaCol = selectedCol - col;
		if (Math.abs(deltaRow) + Math.abs(deltaCol) == 1) {
			nextRow = row;
			nextCol = col;

			makeMove();
			return;
		}

		if (row == selectedRow && col == selectedCol) {
			resetSelected();
			return;
		}
		selectedRow = row;
		selectedCol = col;
	}

	public static void update() {
		boolean animFinished = true;

		for (int row = 0; row < field.length; row++) {
			for (int col = 0; col < field[row].length; col++) {
				Cell cell = field[row][col];
				animFinished &= !cell.update();
			}
		}

		switch (state) {
		case ANIM_FIRST_MOVE:
			if (animFinished) {
				state = CHECK_MOVE_CORRECT;
			}
			break;
		case CHECK_MOVE_CORRECT:
			if (markLines()) {
				destroyAndFall();
				state = ANIM_DESTROY_AND_FALL;
			} else {
				swap();
				state = ANIM_UNDO_MOVE;
			}
			resetSelected();
			break;
		case ANIM_UNDO_MOVE:
			if (animFinished) {
				state = WAIT_INPUT;
			}
			break;
		case ANIM_DESTROY_AND_FALL:
			if (animFinished) {
				state = CHECK_LINES;
			}
			break;
		case CHECK_LINES:
			if (markLines()) {
				destroyAndFall();
				state = ANIM_DESTROY_AND_FALL;
			} else {
				state = WAIT_INPUT;
			}
			break;
		}
	}

	public static int getState() {
		return state;
	}

	public static int getSelectedRow() {
		return selectedRow;
	}

	public static int getSelectedCol() {
		return selectedCol;
	}

	public static int getScore() {
		return score;
	}

	public static String getDebugField() {
		String[] colors = new String[] { "33", "37", "34", "31", "35", "36", "32" };
		String[] types = new String[] { "O", "|", "—", "6", "@" };
		String res = "[\\]";

		for (int col = 0; col < field[0].length; col++) {
			res += "[" + col + "]";
		}
		res += "\n";
		for (int row = 0; row < field.length; row++) {
			res += "[" + row + "]";
			for (int col = 0; col < field[row].length; col++) {
				Cell cell = field[row][col];
				if (selectedRow == row && selectedCol == col) {
					res += "<\u001B[" + colors[cell.getColor()] + "m" + types[cell.getBonus()] + "\u001B[0m>";
				} else {
					res += "[\u001B[" + colors[cell.getColor()] + "m" + types[cell.getBonus()] + "\u001B[0m]";
				}
			}
			res += "\n";
		}

		return res;
	}

	public static String[] getDebugInfo() {
		String[] colors = new String[] { "Y", "W", "B", "R", "P", "O", "G" };
		String[] types = new String[] { "O", "|", "—", "6", "@" };
		String[] res = new String[10];

		for (int row = 0; row < field.length; row++) {
			res[row] = "";
			for (int col = 0; col < field[row].length; col++) {
				Cell cell = field[row][col];
				if (selectedRow == row && selectedCol == col) {
					res[row] += "<" + colors[cell.getColor()] + ":" + types[cell.getBonus()] + ">";
				} else {
					res[row] += "[" + colors[cell.getColor()] + ":" + types[cell.getBonus()] + "]";
				}
			}
		}
		res[8] = "State: " + state;

		return res;
	}

	private static void makeMove() {
		if (selectedRow == -1 || selectedCol == -1 || nextRow == -1 || nextCol == -1)
			return;

		swap();
		state = ANIM_FIRST_MOVE;
	}

	private static void resetSelected() {
		selectedRow = -1;
		selectedCol = -1;
		nextRow = -1;
		nextCol = -1;
	}

	private static void swap() {
		Cell tmpCell = field[selectedRow][selectedCol];
		field[selectedRow][selectedCol] = field[nextRow][nextCol];
		field[nextRow][nextCol] = tmpCell;

		field[selectedRow][selectedCol].setTarget(nextRow, nextCol);
		field[nextRow][nextCol].setTarget(selectedRow, selectedCol);

		int tmpInt = selectedRow;
		selectedRow = nextRow;
		nextRow = tmpInt;

		tmpInt = selectedCol;
		selectedCol = nextCol;
		nextCol = tmpInt;
	}

	private static boolean markLines() {
		boolean destroyNeeded = false;

		for (int i = 0; i < Bonus.DATA.length; i++) {
			Bonus bonus = Bonus.DATA[i];
			for (int row = 0; row < field.length; row++) {
				for (int col = 0; col < field[row].length; col++) {
					if (row + bonus.template.length - 1 < field.length
							&& col + bonus.template[0].length - 1 < field[0].length) {
						boolean lineMatch = true;
						boolean formBonus = true;
						int color = -1;

						for (int dRow = 0; dRow < bonus.template.length; dRow++) {
							for (int dCol = 0; dCol < bonus.template[dRow].length; dCol++) {
								if (bonus.template[dRow][dCol] == 1 && color == -1) {
									color = field[row + dRow][col + dCol].getColor();
								} else if (bonus.template[dRow][dCol] == 1
										&& field[row + dRow][col + dCol].getColor() != color) {
									lineMatch = false;
								}
								if (bonus.template[dRow][dCol] == 1 && field[row + dRow][col + dCol].isEmpty()) {
									formBonus = false;
								}
							}
						}

						if (lineMatch) {
							destroyNeeded = true;
							if (formBonus) {
								int dRow = bonus.pos[0];
								int dCol = bonus.pos[1];
								field[row + dRow][col + dCol].setEmpty(false);
								field[row + dRow][col + dCol].setBonus(bonus.type);
								;
							}
							for (int dRow = 0; dRow < bonus.template.length; dRow++) {
								for (int dCol = 0; dCol < bonus.template[dRow].length; dCol++) {
									if (bonus.template[dRow][dCol] == 1
											&& field[row + dRow][col + dCol].getBonus() == Bonus.NONE) {
										field[row + dRow][col + dCol].setEmpty(true);
									}
								}
							}
						}
					}
				}
			}
		}

		return destroyNeeded;
	}

	private static void destroyAndFall() {
		for (int col = 0; col < field[0].length; col++) {
			int deltaRow = 0;
			for (int row = field.length - 1; row >= 0; row--) {
				while (row + deltaRow >= 0 && field[row + deltaRow][col].isEmpty()) {
					deltaRow--;
				}
				if (row + deltaRow >= 0) {
					field[row][col] = field[row + deltaRow][col];
				} else {
					field[row][col] = getRandomCell(row, col);
				}
				field[row][col].setTarget(row, col);
				field[row][col].setDeltaRow(deltaRow);
			}
		}
	}

	private static Cell getRandomCell(int row, int col) {
		int color = (random.nextInt() % Cell.COLOR_COUNT + Cell.COLOR_COUNT) % Cell.COLOR_COUNT;
		int bonus = Bonus.NONE;
		return new Cell(color, bonus, row, col);
	}
}