package com.goldman;

public class Bonus {
	public static final int NONE = 0;
	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 2;
	public static final int BOMB = 3;
	public static final int RAINBOW = 4;
	public static final int COUNT = 5;

	public static final Bonus[] DATA = {
			new Bonus(Bonus.RAINBOW, new byte[][] { { 1 }, { 1 }, { 1 }, { 1 }, { 1 }, }, new int[] { 2, 0 }, 3),
			new Bonus(Bonus.RAINBOW, new byte[][] { { 1, 1, 1, 1, 1 }, }, new int[] { 0, 2 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 0, 0 }, }, new int[] { 0, 0 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 1, 0, 0 }, { 1, 1, 1 }, { 1, 0, 0 }, }, new int[] { 1, 0 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 1, 0, 0 }, { 1, 0, 0 }, { 1, 1, 1 }, }, new int[] { 2, 0 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 1, 1, 1 }, { 0, 1, 0 }, { 0, 1, 0 }, }, new int[] { 0, 1 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 1, 0 }, }, new int[] { 1, 1 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 0, 1, 0 }, { 0, 1, 0 }, { 1, 1, 1 }, }, new int[] { 2, 1 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, }, new int[] { 0, 2 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 0, 0, 1 }, { 1, 1, 1 }, { 0, 0, 1 }, }, new int[] { 1, 2 }, 3),
			new Bonus(Bonus.BOMB, new byte[][] { { 0, 0, 1 }, { 0, 0, 1 }, { 1, 1, 1 }, }, new int[] { 2, 2 }, 3),
			new Bonus(Bonus.HORIZONTAL, new byte[][] { { 1 }, { 1 }, { 1 }, { 1 }, }, new int[] { 1, 0 }, 2),
			new Bonus(Bonus.VERTICAL, new byte[][] { { 1, 1, 1, 1 }, }, new int[] { 0, 1 }, 2),
			new Bonus(Bonus.NONE, new byte[][] { { 1 }, { 1 }, { 1 }, }, new int[] { 0, 0 }, 1),
			new Bonus(Bonus.NONE, new byte[][] { { 1, 1, 1 }, }, new int[] { 0, 0 }, 1), };

	public final int type;
	public final byte[][] template;
	public final int[] pos;
	public final int score;

	Bonus(int type, byte[][] template, int[] pos, int score) {
		this.type = type;
		this.template = template;
		this.pos = pos;
		this.score = score;
	}
}