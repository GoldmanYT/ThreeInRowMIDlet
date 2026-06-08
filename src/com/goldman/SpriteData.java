package com.goldman;

public class SpriteData {
	public static final SpriteData[] DATA = new SpriteData[] {
			new SpriteData("/checktextures.png", ThreeInRowCanvas.CELL_SIZE, ThreeInRowCanvas.CELL_SIZE, 10, 1),
			new SpriteData("/gemsheet.png", ThreeInRowCanvas.CELL_SIZE, ThreeInRowCanvas.CELL_SIZE, 8, 7),
			new SpriteData("/target.png", ThreeInRowCanvas.CELL_SIZE, ThreeInRowCanvas.CELL_SIZE, 7, 1),
			new SpriteData("/flame.png", ThreeInRowCanvas.CELL_SIZE, ThreeInRowCanvas.CELL_SIZE, 8, 1),
			new SpriteData("/elect.png", ThreeInRowCanvas.CELL_SIZE, ThreeInRowCanvas.CELL_SIZE, 8, 1),
			// TODO: Fix hypercube texture
			new SpriteData("/hypercube.png", 57, 60, 10, 6), };

	public final String imagePath;
	public final int spriteWidth;
	public final int spriteHeight;
	public final int horizontalCount;
	public final int verticalCount;
	public final int width;
	public final int height;

	SpriteData(String imagePath, int spriteWidth, int spriteHeight, int horizontalCount, int verticalCount) {
		this.imagePath = imagePath;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		this.horizontalCount = horizontalCount;
		this.verticalCount = verticalCount;
		this.width = spriteWidth * horizontalCount;
		this.height = spriteHeight * verticalCount;
	}
}
