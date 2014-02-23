package com.kamigaku.luna.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.kamigaku.luna.MainClass;
import com.kamigaku.luna.background.Background;
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
        private Background background;
        
        private static final int bg[] = { 0 };
        private static final int tree[] = { 1 };
        private static final int front[] = { 3 };

        public TestScreen(MainClass game) {
                this.game = game;
        }

        @Override
        public void render(float delta) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                this.background.update(this.levelDatas.camera, this.levelDatas.mapHeight, this.levelDatas.mapWidth);
                this.levelDatas.batch.begin();
                this.background.draw(this.levelDatas.batch);
                this.levelDatas.batch.end();
//                this.levelDatas.rendererMap.render(bg);
                this.levelDatas.rendererMap.render(tree);
                this.levelDatas.lune.getHandler().updateAndRender();
                this.levelDatas.lune.updateLune(delta, this.levelDatas.camera, this.levelDatas.camera.position.x, this.levelDatas.camera.position.y);
                this.levelDatas.player.mouvementPlayer(this.levelDatas);
                this.levelDatas.rendererMap.setView(this.levelDatas.camera);
                this.rendererBox2D.render(levelDatas.world, levelDatas.camera.combined);
                this.levelDatas.world.step(delta * 3, 8, 3);
                this.levelDatas.mouvementCamera();
                this.levelDatas.rendererMap.render(front);
//                this.logger.log();
//                Log.logState(player, this.levelDatas.lune);
                try {
                	this.levelDatas.camera.update();
                	this.levelDatas.camera.apply(Gdx.gl10);
                } catch(NullPointerException exp) {
                }
        }

		@Override
        public void show() {
                levelDatas = new Level1_1("map/map3.tmx", -9.8f);
                background = new Background("tilesets/bg.jpg");
                this.logger = new FPSLogger();
                this.player = levelDatas.player;
                rendererBox2D = new Box2DDebugRenderer();
        }

        @Override
        public void resize(int width, int height) {
        	this.levelDatas.scaling = this.levelDatas.screenWidth / width;
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
        	this.background.getTexture().dispose();
        	this.levelDatas.batch.dispose();
        	this.background.getTexture().dispose();
        }
        
}