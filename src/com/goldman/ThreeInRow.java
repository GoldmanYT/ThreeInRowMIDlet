package com.goldman;

import java.util.Random;

import javax.microedition.lcdui.Canvas;

public class ThreeInRow {
	public static final int WAIT_INPUT = 0;
	public static final int ANIM_FIRST_MOVE = 1;
	public static final int CHECK_MOVE_CORRECT = 2;
	public static final int ANIM_UNDO_MOVE = 3;
	public static final int ANIM_DESTROY = 4;
	public static final int ANIM_FALL = 5;
	public static final int CHECK_LINES = 6;

	private static Cell[][] field;
	private static Random random;
	private static int selectedRow;
	private static int selectedCol;
	private static int nextRow;
	private static int nextCol;
	private static int cursorRow;
	private static int cursorCol;
	private static int state;
	private static int score;
	private static int scoreCoeff;
	private static int scoreChain;
	private static boolean debugAnimFinised;
	private static boolean debugDestroyFinised;
	private static int[] debugCellInfo = new int[8];

	ThreeInRow() {
		field = new Cell[8][8];
		random = new Random();
		resetSelected();
		cursorRow = -1;
		cursorCol = -1;
		state = WAIT_INPUT;
		scoreCoeff = 10;
		scoreChain = 0;

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
					field[row][col].setBonus(Bonus.NONE);
				}
			}
		}

		score = 0;
	}

	public static Cell[][] getField() {
		return field;
	}

	public static void moveCursor(int gameAction) {
		if (cursorRow == -1 || cursorCol == -1) {
			cursorRow = 0;
			cursorCol = 0;
			return;
		}
		switch (gameAction) {
		case Canvas.UP:
			cursorRow--;
			if (cursorRow < 0) {
				cursorRow += field.length;
			}
			if (isSelected()) {
				select(cursorRow, cursorCol);
			}
			break;
		case Canvas.DOWN:
			cursorRow++;
			if (cursorRow >= field.length) {
				cursorRow -= field.length;
			}
			if (isSelected()) {
				select(cursorRow, cursorCol);
			}
			break;
		case Canvas.LEFT:
			cursorCol--;
			if (cursorCol < 0) {
				cursorCol += field[0].length;
			}
			if (isSelected()) {
				select(cursorRow, cursorCol);
			}
			break;
		case Canvas.RIGHT:
			cursorCol++;
			if (cursorCol >= field[0].length) {
				cursorCol -= field[0].length;
			}
			if (isSelected()) {
				select(cursorRow, cursorCol);
			}
			break;
		case Canvas.FIRE:
			select(cursorRow, cursorCol);
			break;
		}
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
		if (isSelected()) {
			Cell cell = field[selectedRow][selectedCol];
			debugCellInfo[0] = cell.getX();
			debugCellInfo[1] = cell.getY();
			debugCellInfo[2] = cell.getTargetX();
			debugCellInfo[3] = cell.getTargetY();
		}
		if (nextRow != -1 && nextCol != -1) {
			Cell cell = field[nextRow][nextCol];
			debugCellInfo[4] = cell.getX();
			debugCellInfo[5] = cell.getY();
			debugCellInfo[6] = cell.getTargetX();
			debugCellInfo[7] = cell.getTargetY();
		}

		boolean animFinished = true;
		boolean destroyFinished = true;

		for (int row = 0; row < field.length; row++) {
			for (int col = 0; col < field[row].length; col++) {
				Cell cell = field[row][col];
				cell.update();
				if (!cell.animFinished()) {
					animFinished = false;
				}
				if (!cell.destroyFinished()) {
					destroyFinished = false;
				}
			}
		}

		debugAnimFinised = animFinished;
		debugDestroyFinised = destroyFinished;

		switch (state) {
		case ANIM_FIRST_MOVE:
			if (animFinished) {
				state = CHECK_MOVE_CORRECT;
			}
			break;
		case CHECK_MOVE_CORRECT:
			if (markLines()) {
				state = ANIM_DESTROY;
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
		case ANIM_DESTROY:
			if (destroyFinished) {
				destroyAndFall();
				state = ANIM_FALL;
			}
			break;
		case ANIM_FALL:
			if (animFinished) {
				state = CHECK_LINES;
			}
			break;
		case CHECK_LINES:
			if (markLines()) {
				state = ANIM_DESTROY;
			} else {
				state = WAIT_INPUT;
			}
			break;
		}
	}

	public static int[] getHint() {
		for (int i = 0; i < MoveData.DATA.length; i++) {
			MoveData moveData = MoveData.DATA[i];
			byte[][] template = moveData.template;
			for (int row = 0; row < field.length; row++) {
				for (int col = 0; col < field[row].length; col++) {
					if (row + template.length - 1 < field.length && col + template[0].length - 1 < field[0].length) {
						boolean isMoveCorrect = true;
						int color = -1;

						for (int dRow = 0; dRow < template.length; dRow++) {
							for (int dCol = 0; dCol < template[dRow].length; dCol++) {
								Cell cell = field[row + dRow][col + dCol];

								if (template[dRow][dCol] == 1 && color == -1) {
									color = cell.getColor();
								} else if (template[dRow][dCol] == 1 && color != cell.getColor()) {
									isMoveCorrect = false;
								}
							}
						}

						if (isMoveCorrect) {
							return new int[] { row + moveData.row, col + moveData.col };
						}
					}
				}
			}
		}

		return new int[] { -1, -1 };
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

	public static int getNextRow() {
		return nextRow;
	}

	public static int getNextCol() {
		return nextCol;
	}

	public static int getCursorRow() {
		return cursorRow;
	}

	public static int getCursorCol() {
		return cursorCol;
	}

	public static int getScore() {
		return score;
	}

	public static boolean isSelected() {
		return selectedRow != -1 && selectedCol != -1;
	}

	public static boolean isNextSelected() {
		return nextRow != -1 && nextCol != -1;
	}

	public static void resetCursor() {
		cursorRow = -1;
		cursorCol = -1;
	}

	public static String getDebugField() {
		String[] colors = new String[] { "33", "37", "34", "31", "35", "36", "32" };
		String[] types = new String[] { "O", "6", "+", "@" };
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
		String[] types = new String[] { "O", "6", "+", "@" };
		String[] states = new String[] { "WAIT_INPUT", "ANIM_FIRST_MOVE", "CHECK_MOVE_CORRECT", "ANIM_UNDO_MOVE",
				"ANIM_DESTROY", "ANIM_FALL", "CHECK_LINES" };
		String[] res = new String[20];

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
		int line = field.length;
		res[line++] = "State: " + states[state];
		res[line++] = "animFinished: " + debugAnimFinised;
		res[line++] = "destroyFinished: " + debugDestroyFinised;
		res[line] = "selected: ";
		for (int i = 0; i < 4; i++) {
			res[line] += debugCellInfo[i] + " ";
		}
		line++;
		res[line] = "next: ";
		for (int i = 4; i < 8; i++) {
			res[field.length] += debugCellInfo[i] + " ";
		}
		line++;

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

		field[selectedRow][selectedCol].setTarget(selectedRow, selectedCol);
		field[nextRow][nextCol].setTarget(nextRow, nextCol);

		int tmpInt = selectedRow;
		selectedRow = nextRow;
		nextRow = tmpInt;

		tmpInt = selectedCol;
		selectedCol = nextCol;
		nextCol = tmpInt;
	}

	private static boolean markLines() {
		boolean destroyNeeded = false;
		boolean formedBonuses[][] = new boolean[field.length][field[0].length];

		if (isSelected() && isNextSelected()) {
			if (field[selectedRow][selectedCol].getBonus() == Bonus.HYPERCUBE) {
				activateBonus(selectedRow, selectedCol);
				field[selectedRow][selectedCol].setEmpty(true);
				destroyNeeded = true;
			}
			if (field[nextRow][nextCol].getBonus() == Bonus.HYPERCUBE) {
				activateBonus(nextRow, nextCol);
				field[nextRow][nextCol].setEmpty(true);
				destroyNeeded = true;
			}
		}

		for (int i = 0; i < Bonus.DATA.length; i++) {
			Bonus bonus = Bonus.DATA[i];
			for (int row = 0; row < field.length; row++) {
				for (int col = 0; col < field[row].length; col++) {
					if (row + bonus.template.length - 1 < field.length
							&& col + bonus.template[0].length - 1 < field[0].length) {
						boolean lineMatch = true;
						boolean formBonus = bonus.type != Bonus.NONE;
						int color = -1;

						for (int dRow = 0; dRow < bonus.template.length; dRow++) {
							for (int dCol = 0; dCol < bonus.template[dRow].length; dCol++) {
								if (bonus.template[dRow][dCol] == 1 && color == -1) {
									color = field[row + dRow][col + dCol].getColor();
								} else if (bonus.template[dRow][dCol] == 1
										&& (field[row + dRow][col + dCol].getColor() != color
												|| field[row + dRow][col + dCol].getColor() == Cell.NONE)) {
									lineMatch = false;
								}
								if (bonus.template[dRow][dCol] == 1 && field[row + dRow][col + dCol].isEmpty()) {
									formBonus = false;
								}
							}
						}

						if (lineMatch) {
							scoreChain++;
							score += scoreCoeff * bonus.score * scoreChain;
							destroyNeeded = true;
							boolean bonusFormed = !formBonus;
							for (int dRow = 0; dRow < bonus.template.length; dRow++) {
								for (int dCol = 0; dCol < bonus.template[dRow].length; dCol++) {
									Cell cell = field[row + dRow][col + dCol];

									if (bonus.template[dRow][dCol] == 1 && !formedBonuses[row + dRow][col + dCol]) {
										cell.setEmpty(true);
										if (cell.getBonus() != Bonus.NONE) {
											activateBonus(row + dRow, col + dCol);
										}
									}
									if (!bonusFormed && bonus.template[dRow][dCol] == 1
											&& (row + dRow == selectedRow && col + dCol == selectedCol
													|| row + dRow == nextRow && col + dCol == nextCol
													|| dRow == bonus.pos[0] && dCol == bonus.pos[1])) {
										bonusFormed = true;
										cell.setEmpty(false);
										cell.setBonus(bonus.type);
										formedBonuses[row + dRow][col + dCol] = true;

										if (bonus.type == Bonus.HYPERCUBE) {
											cell.setColor(Cell.NONE);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (!destroyNeeded) {
			scoreChain = 0;
		}

		return destroyNeeded;
	}

	private static void activateBonus(int row, int col) {
		switch (field[row][col].getBonus()) {
		case Bonus.FLAME:
			for (int dRow = -1; dRow <= 1; dRow++) {
				for (int dCol = -1; dCol <= 1; dCol++) {
					if (row + dRow >= 0 && row + dRow < field.length && col + dCol >= 0
							&& col + dCol < field[0].length) {
						Cell cell = field[row + dRow][col + dCol];
						if (!cell.isEmpty()) {
							cell.setEmpty(true);
							if (cell.getBonus() != Bonus.NONE) {
								activateBonus(row + dRow, col + dCol);
							}
						}
					}
				}
			}
			break;
		case Bonus.STAR:
			for (int dRow = 0; dRow < field.length; dRow++) {
				Cell cell = field[dRow][col];
				if (!cell.isEmpty()) {
					cell.setEmpty(true);
					if (cell.getBonus() != Bonus.NONE) {
						activateBonus(dRow, col);
					}
				}
			}
			for (int dCol = 0; dCol < field[0].length; dCol++) {
				Cell cell = field[row][dCol];
				if (!cell.isEmpty()) {
					cell.setEmpty(true);
					if (cell.getBonus() != Bonus.NONE) {
						activateBonus(row, dCol);
					}
				}
			}
			break;
		case Bonus.HYPERCUBE:
			int color;
			if (isSelected() && isNextSelected()) {
				if (row == selectedRow && col == selectedCol) {
					color = field[nextRow][nextCol].getColor();
				} else if (row == nextRow && col == nextCol) {
					color = field[selectedRow][selectedCol].getColor();
				} else {
					color = getRandomColor();
				}
			} else {
				color = getRandomColor();
			}
			for (int dRow = 0; dRow < field.length; dRow++) {
				for (int dCol = 0; dCol < field[dRow].length; dCol++) {
					if (field[dRow][dCol].getColor() == color && color < Cell.COLOR_COUNT) {
						field[dRow][dCol].setEmpty(true);
					}
				}
			}
			break;
		}
	}

	private static void destroyAndFall() {
		boolean[][] newCells = new boolean[field.length][field[0].length];

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
					newCells[row][col] = true;
				}
				field[row][col].setTarget(row, col);
				field[row][col].setDeltaRow(deltaRow);
			}
		}

		boolean hasMoves = checkMoves();

		while (!hasMoves) {
			for (int row = 0; row < field.length; row++) {
				for (int col = 0; col < field[row].length; col++) {
					if (newCells[row][col]) {
						field[row][col] = getRandomCell(field[row][col]);
					}
				}
			}
			hasMoves = checkMoves();
		}
	}

	private static boolean checkMoves() {
		int[] hint = getHint();
		return hint[0] != -1 && hint[1] != -1;
	}

	private static Cell getRandomCell(int row, int col) {
		int color = getRandomColor();
		return new Cell(color, row, col);
	}

	private static Cell getRandomCell(Cell cell) {
		int color = getRandomColor();
		return new Cell(color, cell);
	}

	private static int getRandomColor() {
		return (random.nextInt() % Cell.COLOR_COUNT + Cell.COLOR_COUNT) % Cell.COLOR_COUNT;
	}
}