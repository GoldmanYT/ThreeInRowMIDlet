package com.goldman;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class ThreeInRowMIDlet extends MIDlet {
	private ThreeInRowCanvas gameCanvas;
	private Display display;

	protected void destroyApp(boolean unconditional) {
	}

	protected void pauseApp() {
	}

	protected void startApp() {
		if (display == null) {
			gameCanvas = new ThreeInRowCanvas();
		}

		display = Display.getDisplay(this);
		display.setCurrent(gameCanvas);
	}
}
