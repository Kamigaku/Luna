package com.kamigaku.luna.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.kamigaku.luna.MainClass;
import com.kamigaku.luna.entity.Player;
import com.kamigaku.luna.gameScreen.Level;
import com.kamigaku.luna.gameScreen.Level1_1;
import com.kamigaku.luna.logger.Log;

public class TestScreen implements Screen {
        
        private Level levelDatas;
        private MainClass game;
        private Box2DDebugRenderer rendererBox2D;
        private FPSLogger logger;
        private boolean toLeft, toRight;
        private Player player;

        public TestScreen(MainClass game) {
                this.game = game;
        }

        @Override
        public void render(float delta) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                this.levelDatas.rendererMap.render();
//                this.levelDatas.player.mouvementPlayer(this.levelDatas, this.toLeft, this.toRight);
                this.levelDatas.player.mouvementPlayer(this.levelDatas);
                this.levelDatas.rendererMap.setView(this.levelDatas.camera);
                this.rendererBox2D.render(levelDatas.world, levelDatas.camera.combined);
                this.levelDatas.world.step(delta * 3, 8, 3);
                this.levelDatas.mouvementCamera();
                this.levelDatas.camera.update();
                this.levelDatas.lune.getHandler().updateAndRender();
                this.levelDatas.lune.updateLune(delta, this.levelDatas.camera, this.levelDatas.camera.position.x, this.levelDatas.camera.position.y);
//                this.logger.log();
                Log.logState(player);
                try {
                this.levelDatas.camera.apply(Gdx.gl10);
                } catch(NullPointerException exp) {
                }
        }

		@Override
        public void show() {
                levelDatas = new Level1_1("map/map3.tmx", -9.8f);
                this.logger = new FPSLogger();
                this.player = levelDatas.player;
                rendererBox2D = new Box2DDebugRenderer();
                Gdx.input.setInputProcessor(new InputProcessor() {
                        
                        @Override
                        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                    		if(toLeft)
                    			player.toLeft = false;
                    		if(toRight)
                    			player.toRight = false;
                    		if(player.wantToJump)
                    			player.wantToJump = false;
                            return false;
                        }
                        
                        @Override
                        public boolean touchDragged(int screenX, int screenY, int pointer) {
                        	int width = Gdx.graphics.getWidth();
                        	if(screenX < width / 3) {
                        		player.toLeft = true;
                        	}
                        	if(screenX >= (width / 3) && screenX < (width / 3) * 2) {
                        		player.wantToJump = true;
                        	}
                        	if(screenX >= (width / 3) * 2) {
                        		player.toRight = true;
                        	}
                            return false;
                        }
                        
                        @Override
                        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                            	int width = Gdx.graphics.getWidth();
                            	if(screenX < width / 3) {
                            		player.toLeft = true;
                            	}
                            	if(screenX >= (width / 3) && screenX < (width / 3) * 2) {
                            		player.wantToJump = true;
                            	}
                            	if(screenX >= (width / 3) * 2) {
                            		player.toRight = true;
                            	}
                                    return false;
                        }
                                                
                        @Override
                        public boolean scrolled(int amount) {
                                return false;
                        }
                        
                        @Override
                        public boolean mouseMoved(int screenX, int screenY) {
//                            levelDatas.lune.getHandler().s
                            return false;
                        }
                        
                        @Override
                        public boolean keyUp(int keycode) {
                                if(keycode == Keys.Q) {
                            		player.toLeft = false;
                                }
                                if(keycode == Keys.D) {
                            		player.toRight = false;                                        
                                }
                                if(keycode == Keys.SPACE)
                                	player.wantToJump = false;
                                return false;
                        }
                        
                        @Override
                        public boolean keyTyped(char character) {
                                if(character == 'd') {
                                		player.toRight = true;
                                }
                                if(character == 'q') {
                                		player.toLeft = true;
                                }
                                return false;
                        }
                        
                        @Override
                        public boolean keyDown(int keycode) {
                                if(keycode == Keys.SPACE) {
                                        player.wantToJump = true;
                                }
                                if(keycode == Keys.Q) {
                                        player.toLeft = true;
                                        player.toRight = false;
                                }
                                if(keycode == Keys.D) {
                                		player.toLeft = false;
                                		player.toRight = true;                                        
                                }
                                if(keycode == Keys.D && keycode == Keys.Q) {
                                		player.toLeft = player.toRight = false;                                        
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
        	this.player.fixturePlayer.shape.dispose();
        }
        
}