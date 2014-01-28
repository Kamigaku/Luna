package com.kamigaku.luna.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.kamigaku.luna.MainClass;

public class MenuScreen implements Screen {
        
        private Stage stage;
        private Table table;
        protected SpriteBatch batch;
        private BitmapFont font;
        private TextButton btnPlay;
        private MainClass game;
        
        public MenuScreen(MainClass game) {
                this.game = game;
        }

        @Override
        public void render(float delta) {
                Gdx.gl.glClearColor(0, 0, 0, 0);
                Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                batch.begin();
                batch.end();
                stage.act();
                stage.draw();
        }

        @Override
        public void resize(int width, int height) {
        }

        @Override
        public void show() {
                batch = new SpriteBatch();        
                stage = new Stage();
                font = new BitmapFont(Gdx.files.internal("font/impact25.fnt"));        
                TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.pack"));                
                Skin skin = new Skin(atlas);                
                TextButtonStyle btnStyle = new TextButtonStyle();
                btnStyle.up = skin.getDrawable("button");
                btnStyle.down = skin.getDrawable("buttonpressed");
                btnStyle.font = font;                
                btnPlay = new TextButton("Play", btnStyle);
                btnPlay.pad(10);
                table = new Table(skin);
                table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                table.add(btnPlay);
                stage.addActor(table);
                Gdx.input.setInputProcessor(stage);
                btnPlay.addListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y,
                                        int pointer, int button) {
                                game.setCurrentScreen(new TestScreen(game));
                                game.setScreen(game.getCurrentScreen());
                                return super.touchDown(event, x, y, pointer, button);
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
                this.batch.dispose();
                this.font.dispose();
                this.stage.dispose();
                this.table.getStage().dispose();
        }

}