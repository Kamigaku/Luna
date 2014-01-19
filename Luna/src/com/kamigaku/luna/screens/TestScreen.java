package com.kamigaku.luna.screens;

import net.dermetfan.utils.libgdx.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kamigaku.luna.MainClass;
import com.kamigaku.luna.entity.Player;
import com.kamigaku.luna.gameScreen.Level;

public class TestScreen implements Screen {
	
	private Level levelDatas;
	private MainClass game;
	private Box2DDebugRenderer rendererBox2D;
	private boolean toLeft, toRight, jumping, falling;
	private Player player;

	public TestScreen(MainClass game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		mouvementPlayer();
		this.levelDatas.rendererMap.setView(this.levelDatas.camera);
		this.levelDatas.rendererMap.render();
		this.rendererBox2D.render(levelDatas.world, levelDatas.camera.combined);
		this.levelDatas.world.step(delta * 3, 8, 3);
		this.levelDatas.camera.update();
		this.levelDatas.camera.apply(Gdx.gl10);
	}

	private void mouvementPlayer() {
	    Array<Contact> contacts = this.levelDatas.world.getContactList();
	    if(contacts.size == 0) {
	    	if(player.playerBody.getLinearVelocity().y < 0.0f) {
	    		this.falling = true;
	    		this.jumping = false;
	    	}
	    	else if(player.playerBody.getLinearVelocity().y > 0.0f) {
	    		this.falling = false;
	    		this.jumping = true;
	    	}
	    	else {
	    		this.falling = false;
	    		this.jumping = false;
	    	}
	    }
	    for(Contact contact : contacts) {
	    	if(contact.getFixtureA().equals(player.playerBody.getFixtureList().get(0)) && jumping) {
	    		bodyIsWall(contact, contact.getFixtureB().getBody());
	    	}
	    	if(contact.getFixtureB().equals(player.playerBody.getFixtureList().get(0)) && jumping) {
	    		bodyIsWall(contact, contact.getFixtureA().getBody());
	    	}
	    }
	    if(jumping) {
	    	jumping = false;
		    if(toRight) {
		    	player.playerBody.applyLinearImpulse(new Vector2(50000 * player.maxVectorX, 50000 * player.maxVectorY), player.playerBody.getWorldCenter(), true);
		    }
		    if(toLeft) {
		    	player.playerBody.applyLinearImpulse(new Vector2(50000 * -player.maxVectorX, 50000 * player.maxVectorY), player.playerBody.getWorldCenter(), true);
		    }
	    }
	    else {
		    if(toRight) {
		    	player.playerBody.applyLinearImpulse(new Vector2(50 * player.maxVectorX, player.playerBody.getLinearVelocity().y), player.playerBody.getWorldCenter(), true);
		    }
		    if(toLeft) {
		    	player.playerBody.applyLinearImpulse(new Vector2(50 * -player.maxVectorX, player.playerBody.getLinearVelocity().y), player.playerBody.getWorldCenter(), true);
		    }
	    }
	}

	private void bodyIsWall(Contact contact, Body body) {
		Box2DMapObjectParser parser = this.levelDatas.parser;
		ObjectMap<String, Body> bodies = parser.getBodies();
		String keyOfBody = bodies.findKey(body, false);
		if(keyOfBody.contains("Wall")) {
			contact.setRestitution(2f);
		}
	}
	


	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		levelDatas = new Level("map/map.tmx");
		this.player = levelDatas.player;
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
				toLeft = toRight = false;
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Keys.SPACE) {
					jumping = true;
				}
				if(keycode == Keys.Q) {
					toLeft = true;
					toRight = false;
				}
				if(keycode == Keys.D) {
					toLeft = false;
					toRight = true;					
				}
				if(keycode == Keys.D && keycode == Keys.Q) {
					toLeft = toRight = false;					
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
