package com.kamigaku.luna.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kamigaku.luna.MainClass;
import com.kamigaku.luna.entity.Player;
import com.kamigaku.luna.gameScreen.Level;
import com.kamigaku.luna.gameScreen.Level1_1;

public class TestScreen implements Screen {
        
        private Level levelDatas;
        private MainClass game;
        private Box2DDebugRenderer rendererBox2D;
        private boolean toLeft, toRight;
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

        @Override
        public void show() {
                levelDatas = new Level1_1("map/map2.tmx", -9.8f);
                this.player = levelDatas.player;
                this.toLeft = this.toRight = false;
                rendererBox2D = new Box2DDebugRenderer();
                Gdx.input.setInputProcessor(new InputProcessor() {
                        
                        @Override
                        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                    		if(toLeft)
                    			toLeft = false;
                    		if(toRight)
                    			toRight = false;
                    		if(player.wantToJump)
                    			player.wantToJump = false;
                            return false;
                        }
                        
                        @Override
                        public boolean touchDragged(int screenX, int screenY, int pointer) {
                        	int width = Gdx.graphics.getWidth();
                        	if(screenX < width / 3) {
                        		toLeft = true;
                        	}
                        	if(screenX >= (width / 3) && screenX < (width / 3) * 2) {
                        		player.wantToJump = true;
                        	}
                        	if(screenX >= (width / 3) * 2) {
                        		toRight = true;
                        	}
                            return false;
                        }
                        
                        @Override
                        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                            	int width = Gdx.graphics.getWidth();
                            	if(screenX < width / 3) {
                            		toLeft = true;
                            	}
                            	if(screenX >= (width / 3) && screenX < (width / 3) * 2) {
                            		player.wantToJump = true;
                            	}
                            	if(screenX >= (width / 3) * 2) {
                            		toRight = true;
                            	}
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
                                if(keycode == Keys.Q) {
                                        toLeft = false;
                                }
                                if(keycode == Keys.D) {
                                        toRight = false;                                        
                                }
                                return false;
                        }
                        
                        @Override
                        public boolean keyTyped(char character) {
                                if(character == 'd') {
                                        toRight = true;
                                }
                                if(character == 'q') {
                                        toLeft = true;
                                }
                                return false;
                        }
                        
                        @Override
                        public boolean keyDown(int keycode) {
                                if(keycode == Keys.SPACE) {
                                        player.wantToJump = true;
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
        public void resize(int width, int height) {
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
        	this.levelDatas.rendererMap.dispose();
        	this.levelDatas.world.dispose();
        	this.rendererBox2D.dispose();
        }

        private void mouvementPlayer() {
        	this.player.resetMouvement();
             Array<Contact> contacts = this.levelDatas.world.getContactList();
             try {
                 for(Contact contact : contacts) {
                     if(contact.getFixtureA().equals(player.playerBody.getFixtureList().get(0))) {
                             if(!this.player.againstWall)
                                     this.levelDatas.bodyIsWall(contact.getFixtureB().getBody());
                     }
                     if(contact.getFixtureB().equals(player.playerBody.getFixtureList().get(0))) {
                             if(!this.player.againstWall)
                                     this.levelDatas.bodyIsWall(contact.getFixtureA().getBody());
                     }
                 }
             } catch(GdxRuntimeException exp) {
             }
             
             if(!this.player.againstWall) {
                     this.player.jumpedOnce = false;
             }
             
             if(player.playerBody.getLinearVelocity().y < -0.01f) { // le joueur tombe
                     this.player.falling = true;
                     this.player.jumping = false;
             }
             else if(player.playerBody.getLinearVelocity().y > 0.01f) { // le joueur est en train de sauter
                     this.player.falling = false;
                     this.player.jumping = true;
             }
             else { // le joueur fait du surplace
                     this.player.falling = this.player.jumping = false;
             }
             
             if(this.player.againstWall && this.player.falling) { // il est contre un mur et tombe (chute ralentie)
            	 player.playerBody.applyLinearImpulse(new Vector2(player.playerBody.getLinearVelocity().x, player.playerBody.getLinearVelocity().y * 0.5f), player.playerBody.getWorldCenter(), true);
             }
             if(this.player.wantToJump && !this.player.againstWall && !this.player.jumping && !this.player.falling) {
            	 this.player.wantToJump = false;
                     if(toLeft) {
                             player.playerBody.applyLinearImpulse(new Vector2(50000 * -player.maxVectorX, 50000 * player.maxVectorY), player.playerBody.getWorldCenter(), true);
                     }
                     else if(toRight) {
                             player.playerBody.applyLinearImpulse(new Vector2(50000 * player.maxVectorX, 50000 * player.maxVectorY), player.playerBody.getWorldCenter(), true);
                     }
                     else {
                             player.playerBody.applyLinearImpulse(new Vector2(0, 50000 * player.maxVectorY), player.playerBody.getWorldCenter(), true);
                     }
             }
             else if(this.player.wantToJump && this.player.againstWall && !this.player.jumpedOnce) {
            	 	this.player.wantToJump = false;
                     if(toRight) {
                             player.playerBody.applyLinearImpulse(new Vector2(50000 * -player.maxVectorX, 50000 * player.maxVectorY * 2), player.playerBody.getWorldCenter(), true);
                     }
                     else if(toLeft) {
                             player.playerBody.applyLinearImpulse(new Vector2(50000 * player.maxVectorX, 50000 * player.maxVectorY * 2), player.playerBody.getWorldCenter(), true);
                     }
                     this.player.jumpedOnce = true;
             }
             else if((toRight || toLeft) && !this.player.jumping) {
                     if(toRight) {
                             player.playerBody.applyLinearImpulse(new Vector2(50 * player.maxVectorX, player.playerBody.getLinearVelocity().y), player.playerBody.getWorldCenter(), true);
                     }
                     else if(toLeft) {
                             player.playerBody.applyLinearImpulse(new Vector2(50 * -player.maxVectorX, player.playerBody.getLinearVelocity().y), player.playerBody.getWorldCenter(), true);
                     }         
             }
             this.player.wantToJump = false;
        }
}