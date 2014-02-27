package com.kamigaku.luna;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.kamigaku.luna.screens.MenuScreen;

public class MainClass extends Game {
	
	private Screen currentScreen;
	public static final int PIXELS = 64;
        public static final int ORIGINAL_WIDTH = 1280;
        public static final int ORIGINAL_HEIGHT = 720;
        public static boolean gl10 = false;

	@Override
	public void create() {
            if(Gdx.gl10 != null) {
                gl10 = true;
            }
            Texture.setEnforcePotImages(false);
            setCurrentScreen(new MenuScreen(this));
            setScreen(getCurrentScreen());
	}
	
	public void setCurrentScreen(Screen screen) {
		this.currentScreen = screen;
	}
	
	public Screen getCurrentScreen() {
		return this.currentScreen;
	}

}
