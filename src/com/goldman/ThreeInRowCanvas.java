package com.goldman;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class ThreeInRowCanvas extends Canvas implements Runnable {
	private static final int CHECK = 0;
	private static final int GEM = 1;
	private static final int TARGET = 2;

	private static final int OFFSET_X = 0;
	private static final int OFFSET_Y = 0;
	private static final int WIDTH = 240;
	private static final int HEIGHT = 320;

	private static final int CELL_SIZE = 30;
	private static final int ANIM_DELAY = 30;

	private static final String[] imagePaths = new String[] { "/checktextures.png", "/gemsheet.png", "/target.png", };
	private static final int[][] sizes = new int[][] { { CELL_SIZE * 30, CELL_SIZE, 30, 1 },
			{ CELL_SIZE * 15, CELL_SIZE * 14, 15, 14 }, { CELL_SIZE * 6, CELL_SIZE, 6, 1 } };

	private Sprite[] sprites;
	private boolean running = true;
	private boolean debug = false;

	ThreeInRowCanvas() {
		new ThreeInRow();

		sprites = new Sprite[imagePaths.length];
		for (int i = 0; i < imagePaths.length; i++) {
			Image spriteSheet;
			try {
				spriteSheet = Image.createImage(imagePaths[i]);
			} catch (Exception e) {
				spriteSheet = getDefaultImage(i);
			}

			int width = sizes[i][0];
			int height = sizes[i][1];
			int horizontalCount = sizes[i][2];
			int verticalCount = sizes[i][3];
			int spriteWidth = width / horizontalCount;
			int spriteHeight = height / verticalCount;

			sprites[i] = new Sprite(spriteSheet, spriteWidth, spriteHeight);
		}

		setFullScreenMode(true);
		new Thread(this).start();
	}

	private Image getDefaultImage(int imageIndex) {
		Image result = Image.createImage(sizes[imageIndex][0], sizes[imageIndex][1]);
		Graphics g = result.getGraphics();

		switch (imageIndex) {
		case CHECK:
			for (int x = 0, y = 0; x < CELL_SIZE * 30; x += CELL_SIZE) {
				g.setColor(0xAAAAAA);
				g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
				g.setColor(0x808080);
				g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
			}
			break;
		case GEM:
			int[] colors = new int[] { 0xFFFF00, 0xFFFFFF, 0x00FFFF, 0xFF0000, 0xFF00FF, 0xFF8000, 0x00FF00 };
			for (int x = 0; x < CELL_SIZE * 30; x += CELL_SIZE) {
				for (int y = 0; y < CELL_SIZE * 14; y += CELL_SIZE) {
					g.setColor(colors[y / (CELL_SIZE * 2)]);
					g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 0, 360);
					g.setColor(colors[y / (CELL_SIZE * 2)] / 2);
					g.drawArc(x, y, CELL_SIZE, CELL_SIZE, 0, 360);
				}
			}
			break;
		case TARGET:
			for (int x = 0, y = 0; x < CELL_SIZE * 6; x += CELL_SIZE) {
				g.setColor(0xFF0000);
				g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
			}
			break;
		}
		return result;
	}

	protected void paint(Graphics g) {
		g.setColor(0xFFFFFF);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		Cell[][] field = ThreeInRow.getField();

		for (int row = 0; row < field.length; row++) {
			for (int col = 0; col < field[row].length; col++) {
				int x = OFFSET_X + col * CELL_SIZE;
				int y = OFFSET_Y + row * CELL_SIZE;

				sprites[CHECK].setPosition(x, y);
				sprites[CHECK].paint(g);
			}
		}

		for (int row = 0; row < field.length; row++) {
			for (int col = 0; col < field[row].length; col++) {
				Cell cell = field[row][col];
				int x = OFFSET_X + cell.getX(CELL_SIZE);
				int y = OFFSET_Y + cell.getY(CELL_SIZE);
				int spriteRow = cell.getColor() * 2;

				sprites[GEM].setPosition(x, y);
				sprites[GEM].setFrame(spriteRow * 15);
				sprites[GEM].paint(g);
			}
		}

		int row = ThreeInRow.getSelectedRow();
		int col = ThreeInRow.getSelectedCol();
		int x = OFFSET_X + col * CELL_SIZE;
		int y = OFFSET_Y + row * CELL_SIZE;

		sprites[TARGET].setPosition(x, y);
		sprites[TARGET].paint(g);

		Font font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
		g.setFont(font);
		g.setColor(0x000000);

		if (debug) {
			int height = font.getHeight();
			String[] debugInfo = ThreeInRow.getDebugInfo();

			for (int i = 0; i < debugInfo.length; i++) {
				String s = debugInfo[i];
				if (s != null) {
					g.drawString(s, 0, i * height, Graphics.LEFT | Graphics.TOP);
				}
			}
		}

		g.drawString("Score: " + ThreeInRow.getScore(), WIDTH / 2, HEIGHT, Graphics.HCENTER | Graphics.BOTTOM);
	}

	protected void pointerPressed(int x, int y) {
		int row = (y - OFFSET_Y) / CELL_SIZE;
		int col = (x - OFFSET_X) / CELL_SIZE;
		ThreeInRow.select(row, col);
	}

	protected void pointerDragged(int x, int y) {
		int row = (y - OFFSET_Y) / CELL_SIZE;
		int col = (x - OFFSET_X) / CELL_SIZE;
		int selectedRow = ThreeInRow.getSelectedRow();
		int selectedCol = ThreeInRow.getSelectedCol();
		if (row != selectedRow || col != selectedCol) {
			ThreeInRow.select(row, col);
		}
	}

	protected void keyPressed(int keyCode) {
		debug ^= true;
	}

	public void run() {
		while (running) {
			ThreeInRow.update();
			repaint();
			try {
				Thread.sleep(ANIM_DELAY);
			} catch (InterruptedException e) {
			}
		}
	}
}
