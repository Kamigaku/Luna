package com.kamigaku.luna.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.kamigaku.luna.MainClass;
import com.kamigaku.luna.gameScreen.Level;

public class TestScreen implements Screen {
	
	private Level levelDatas;
	private MainClass game;
	private Box2DDebugRenderer rendererBox2D;
	private boolean toLeft, toRight, jumping;

	public TestScreen(MainClass game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		this.levelDatas.rendererMap.setView(this.levelDatas.camera);
		this.levelDatas.rendererMap.render();
		this.rendererBox2D.render(levelDatas.world, levelDatas.camera.combined);
		this.levelDatas.world.step(delta * 3, 8, 3);
		this.levelDatas.camera.update();
		this.levelDatas.camera.apply(Gdx.gl10);
		mouvementPlayer();
	}

	private void mouvementPlayer() {
		Vector2 vel = this.levelDatas.player.playerBody.getLinearVelocity();
		vel.x += 5;
		this.levelDatas.player.playerBody.setLinearVelocity(vel);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		levelDatas = new Level("map/map.tmx");
		rendererBox2D = new Box2DDebugRenderer();
		Gdx.input.setInputProcessor(new InputProcessor() {

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return false;
			}
						
			@Override
			public boolean scrolled(int amount) {
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				return false;
			}
		});
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
	}
}
