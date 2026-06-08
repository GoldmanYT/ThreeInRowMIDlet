package com.goldman;

public class MoveData {
	public static final MoveData[] DATA = new MoveData[] {
			new MoveData(new byte[][] { { 1, 0, 0 }, { 0, 1, 1 } }, 0, 0),
			new MoveData(new byte[][] { { 1, 0, 1, 1 } }, 0, 0),
			new MoveData(new byte[][] { { 0, 1, 1 }, { 1, 0, 0 } }, 1, 0),
			new MoveData(new byte[][] { { 0, 1, 0 }, { 1, 0, 1 } }, 0, 1),
			new MoveData(new byte[][] { { 1, 0, 1 }, { 0, 1, 0 } }, 1, 1),
			new MoveData(new byte[][] { { 0, 0, 1 }, { 1, 1, 0 } }, 0, 2),
			new MoveData(new byte[][] { { 1, 1, 0, 1 } }, 0, 3),
			new MoveData(new byte[][] { { 1, 1, 0 }, { 0, 0, 1 } }, 1, 2),
			new MoveData(new byte[][] { { 0, 1 }, { 1, 0 }, { 1, 0 } }, 0, 1),
			new MoveData(new byte[][] { { 1 }, { 0 }, { 1 }, { 1 } }, 0, 0),
			new MoveData(new byte[][] { { 1, 0 }, { 0, 1 }, { 0, 1 } }, 0, 0),
			new MoveData(new byte[][] { { 1, 0 }, { 0, 1 }, { 1, 0 } }, 1, 1),
			new MoveData(new byte[][] { { 0, 1 }, { 1, 0 }, { 0, 1 } }, 1, 0),
			new MoveData(new byte[][] { { 1, 0 }, { 1, 0 }, { 0, 1 } }, 2, 1),
			new MoveData(new byte[][] { { 1 }, { 1 }, { 0 }, { 1 } }, 3, 0),
			new MoveData(new byte[][] { { 0, 1 }, { 0, 1 }, { 1, 0 } }, 2, 0),
			new MoveData(new byte[][] { { 1 }, { 1 }, { 1 } }, 0, 0),
			new MoveData(new byte[][] { { 1, 1, 1 } }, 0, 0), };

	public final byte[][] template;
	public final int row;
	public final int col;

	MoveData(byte[][] template, int row, int col) {
		this.template = template;
		this.row = row;
		this.col = col;
	}
}
