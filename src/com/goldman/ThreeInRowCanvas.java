package com.goldman;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class ThreeInRowCanvas extends Canvas implements Runnable {
	public static final int CHECK = 0;
	public static final int GEM = 1;
	public static final int TARGET = 2;
	public static final int FLAME = 3;
	public static final int ELECT = 4;
	public static final int HYPERCUBE = 5;

	public static final int OFFSET_X = 0;
	public static final int OFFSET_Y = 0;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 320;

	public static final int CELL_SIZE = 30;
	public static final int ANIM_DELAY = 30;

	private Sprite[] sprites;
	private boolean running = true;
	private boolean debug = false;
	private int frame = 0;

	ThreeInRowCanvas() {
		new ThreeInRow();

		sprites = new Sprite[SpriteData.DATA.length];
		for (int i = 0; i < SpriteData.DATA.length; i++) {
			SpriteData spriteData = SpriteData.DATA[i];
			Image spriteSheet = null;
			try {
				spriteSheet = Image.createImage(spriteData.imagePath);
				if (spriteSheet.getWidth() % spriteData.spriteWidth != 0
						|| spriteSheet.getHeight() % spriteData.spriteHeight != 0) {
					System.err.println("Sprite sizes are incorrect");
					System.err.println("Spritesheet size: " + spriteSheet.getWidth() + " " + spriteSheet.getHeight());
					System.err.println("Sprite size: " + spriteData.spriteWidth + " " + spriteData.spriteHeight);
					throw new Exception();
				}
			} catch (Exception e) {
				spriteSheet = getDefaultImage(i);
			}
			sprites[i] = new Sprite(spriteSheet, spriteData.spriteWidth, spriteData.spriteHeight);
		}

		setFullScreenMode(true);
		new Thread(this).start();
	}

	private Image getDefaultImage(int imageIndex) {
		SpriteData spriteData = SpriteData.DATA[imageIndex];

		int spriteWidth = spriteData.spriteWidth;
		int spriteHeight = spriteData.spriteHeight;
		int width = spriteData.width;
		int height = spriteData.height;

		Image result = Image.createImage(width, height);
		Graphics g = result.getGraphics();

		switch (imageIndex) {
		case CHECK:
			for (int x = 0, y = 0; x < width; x += spriteWidth) {
				g.setColor(0xAAAAAA);
				g.fillRect(x, y, spriteWidth - 1, spriteHeight - 1);
				g.setColor(0x808080);
				g.drawRect(x, y, spriteWidth - 1, spriteHeight - 1);
			}
			break;
		case GEM:
			int[] colors = new int[] { 0xFFFF00, 0xE0E0E0, 0x00FFFF, 0xFF0000, 0xFF00FF, 0xFF8000, 0x00FF00 };
			for (int x = 0; x < width; x += spriteWidth) {
				for (int y = 0; y < height; y += spriteHeight) {
					int color = colors[y / spriteHeight];
					g.setColor(color);
					g.fillArc(x, y, spriteWidth - 1, spriteHeight - 1, 0, 360);
					g.setColor(color & 0x808080);
					g.drawArc(x, y, spriteWidth - 1, spriteHeight - 1, 0, 360);
				}
			}
			break;
		case TARGET:
			for (int x = 0, y = 0; x < width; x += spriteWidth) {
				g.setColor(0xFF0000);
				g.drawRect(x, y, spriteWidth - 1, spriteHeight - 1);
			}
			break;
		default:
			g.setColor(0x000000);
			g.fillRect(0, 0, width, height);
			g.setColor(0xFF00FF);
			for (int x = 0; x < width; x += spriteWidth) {
				for (int y = 0; y < height; y += spriteHeight) {
					g.fillRect(x, y, spriteWidth / 2, spriteHeight / 2);
					g.fillRect(x + spriteWidth / 2, y + spriteHeight / 2, spriteWidth / 2, spriteHeight / 2);
				}
			}
		}

		int[] rgb = new int[width * height];
		result.getRGB(rgb, 0, width, 0, 0, width, height);
		for (int i = 0; i < rgb.length; i++) {
			if (rgb[i] == 0xFFFFFFFF) {
				rgb[i] = 0x00000000;
			}
		}

		return Image.createRGBImage(rgb, width, height, true);
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
				int bonus = cell.getBonus();

				switch (bonus) {
				case Bonus.FLAME:
					sprites[FLAME].setPosition(x, y);
					sprites[FLAME].paint(g);
					break;
				case Bonus.STAR:
					sprites[ELECT].setPosition(x, y);
					sprites[ELECT].paint(g);
					break;
				case Bonus.HYPERCUBE:
					sprites[HYPERCUBE].setPosition(x, y);
					sprites[HYPERCUBE].paint(g);
					break;
				}
				if (bonus != Bonus.HYPERCUBE) {
					sprites[GEM].setPosition(x, y);
					sprites[GEM].setFrame(cell.getFrameIndex());
					sprites[GEM].paint(g);
				}
			}
		}

		int row = ThreeInRow.getSelectedRow();
		int col = ThreeInRow.getSelectedCol();
		int x = OFFSET_X + col * CELL_SIZE;
		int y = OFFSET_Y + row * CELL_SIZE;

		sprites[TARGET].setPosition(x, y);
		sprites[TARGET].paint(g);

		row = ThreeInRow.getNextRow();
		col = ThreeInRow.getNextCol();
		x = OFFSET_X + col * CELL_SIZE;
		y = OFFSET_Y + row * CELL_SIZE;

		sprites[TARGET].setPosition(x, y);
		sprites[TARGET].paint(g);

		row = ThreeInRow.getCursorRow();
		col = ThreeInRow.getCursorCol();
		x = OFFSET_X + col * CELL_SIZE;
		y = OFFSET_Y + row * CELL_SIZE;

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

		g.drawString("Score: " + ThreeInRow.getScore(), WIDTH / 2, HEIGHT - 30, Graphics.HCENTER | Graphics.BOTTOM);
	}

	private void update() {
		frame = (frame + 1) % 60;
		sprites[TARGET].nextFrame();
		if (frame % 2 == 0) {
			sprites[FLAME].nextFrame();
			sprites[ELECT].nextFrame();
		}
		sprites[HYPERCUBE].nextFrame();
	}

	protected void pointerPressed(int x, int y) {
		ThreeInRow.resetCursor();
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
		int gameAction = getGameAction(keyCode);

		switch (keyCode) {
		case KEY_NUM2:
			gameAction = UP;
			break;
		case KEY_NUM4:
			gameAction = LEFT;
			break;
		case KEY_NUM6:
			gameAction = RIGHT;
			break;
		case KEY_NUM8:
			gameAction = DOWN;
			break;
		case KEY_NUM5:
			gameAction = FIRE;
			break;
		}

		ThreeInRow.moveCursor(gameAction);

		if (keyCode == Canvas.KEY_STAR) {
			debug ^= true;
		}
	}

	public void run() {
		while (running) {
			ThreeInRow.update();
			repaint();
			update();
			try {
				Thread.sleep(ANIM_DELAY);
			} catch (InterruptedException e) {
			}
		}
	}
}
