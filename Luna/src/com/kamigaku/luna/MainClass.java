package com.kamigaku.luna;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.kamigaku.luna.screens.MenuScreen;

public class MainClass extends Game {
	
	private Screen currentScreen;
	public static final int PIXELS = 64;

	@Override
	public void create() {
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
