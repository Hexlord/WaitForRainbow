package com.waitforrainbow.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.waitforrainbow.game.GameScreen;
import com.waitforrainbow.game.WFRGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Wait for rainbow";
		config.width = 1280;
		config.height = 768;
		new LwjglApplication(new WFRGame(), config);
	}
}
