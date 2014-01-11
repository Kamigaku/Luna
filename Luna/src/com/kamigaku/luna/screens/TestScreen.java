package com.kamigaku.luna.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
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
    	GL10 gl = Gdx.graphics.getGL10();
		this.levelDatas.rendererMap.setView(this.levelDatas.camera);
		this.levelDatas.rendererMap.render();
		this.rendererBox2D.render(levelDatas.world, levelDatas.camera.combined);
		this.levelDatas.world.step(delta * 3, 8, 3);
		this.levelDatas.camera.apply(gl);
		this.levelDatas.camera.update();
		mouvementPlayer();
	}

	private void mouvementPlayer() {
		Vector2 vel = levelDatas.player.playerBody.getLinearVelocity();
		if(vel.y >= 0.1 || vel.y <= -0.1) {
			jumping = false;
		}
		float impulse = levelDatas.player.playerBody.getMass();
		Body player = levelDatas.player.playerBody;
		if((toLeft && toRight) || (!toLeft && !toRight) && !jumping) {
			player.applyLinearImpulse(new Vector2(vel.x * (-100), 0), player.getWorldCenter(), true);
		}
		else {
			if(!jumping) {
				if(toLeft) {
					float velChange = -(levelDatas.player.vectorX) - vel.x;
					player.applyLinearImpulse(new Vector2(impulse * velChange, 0), player.getWorldCenter(), true);
				}
				else {
					float velChange = (levelDatas.player.vectorX) - vel.x;
					player.applyLinearImpulse(new Vector2(impulse * velChange, 0), player.getWorldCenter(), true);
				}
			}
			else {
				if(toLeft) {
					  levelDatas.player.playerBody.applyLinearImpulse(new Vector2(0, impulse * 50), levelDatas.player.playerBody.getWorldCenter(), true);
					  jumping = false;
				}
				else {
					  levelDatas.player.playerBody.applyLinearImpulse(new Vector2(0, impulse * 50), levelDatas.player.playerBody.getWorldCenter(), true);
					  jumping = false;
				}
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		this.toLeft = this.toRight = this.jumping = false;
		levelDatas = new Level("map/map.tmx");
		rendererBox2D = new Box2DDebugRenderer();
		Gdx.input.setInputProcessor(new InputProcessor() {

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				toLeft = toRight = false;
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				touchDown(screenX, screenY, pointer, 0);
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if(screenX >= Gdx.graphics.getWidth() / 2) {
					toLeft = false;
					toRight = true;
				}
				else {
					toLeft = true;
					toRight = false;
				}
				return false;
			}
						
			@Override
			public boolean scrolled(int amount) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				jumping = false;
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				if(Keys.SPACE == keycode) {
					jumping = true;
				}
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
