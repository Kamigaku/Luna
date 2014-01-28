package com.kamigaku.luna.screens;

import box2dLight.ConeLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.kamigaku.luna.MainClass;

public class PlayScreen implements Screen {
        
        private MainClass game;
        private World world;
        private OrthographicCamera camera;
        private Box2DDebugRenderer renderer;
        private FPSLogger fps;
        private float width, height;
        private static final float SCALING = 0.6f;
        private RayHandler handler;
        private ConeLight lightTest;
        private Body playerBody;
        private Boolean renderOnce = false;
        private float xPlayer;
        private float yPlayer;
        private int heightBox = 10;
        private int widthBox = 5;

        public PlayScreen(MainClass game) {
                this.game = game;
        }

        @Override
        public void render(float delta) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//                world.getBodies(bodies);
//                for(int i = 0; i < bodies.size; i++) {
//                        System.out.println(bodies.get(i).localPoint2);
//                }
                if(!renderOnce) {
                        Vector2 positionPlayer = playerBody.getPosition();
                        xPlayer = positionPlayer.x;
                        System.out.println(xPlayer);
                        System.out.println(yPlayer);
                        yPlayer = positionPlayer.y;
                        renderOnce = true;
                }
                renderer.render(world, camera.combined);
                handler.updateAndRender();
                if(lightContainsPlayer(xPlayer, yPlayer, widthBox, heightBox)) {
                        System.out.println("Player inside the light");
                }
                world.step(1/60f, 6, 2);
//                fps.log();
        }

        private boolean lightContainsPlayer(float x, float y, int width, int height) {
                if(lightTest.contains(x, y + height))
                        return true;
                else if(lightTest.contains(x, y - height))
                        return true;
                else if(lightTest.contains(x + width, y + height))
                        return true;
                else if(lightTest.contains(x + width, y - height))
                        return true;
                return false;
        }

        @Override
        public void resize(int width, int height) {
        }

        @Override
        public void show() {
                this.width = Gdx.graphics.getWidth() * SCALING;
                this.height = Gdx.graphics.getHeight() * SCALING;
                this.camera = new OrthographicCamera();
                Gdx.input.setInputProcessor(new InputProcessor() {
                        
                        @Override
                        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                                lightTest.setPosition((screenX * SCALING) - (camera.viewportWidth / 2), camera.viewportHeight - (screenY * SCALING));
                                return false;
                        }
                        
                        @Override
                        public boolean touchDragged(int screenX, int screenY, int pointer) {
                                lightTest.setPosition((screenX * SCALING) - (camera.viewportWidth / 2), camera.viewportHeight - (screenY * SCALING));
                                return false;
                        }
                        
                        @Override
                        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                                // TODO Auto-generated method stub
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
                                // TODO Auto-generated method stub
                                return false;
                        }
                        
                        @Override
                        public boolean keyTyped(char character) {
                                // TODO Auto-generated method stub
                                return false;
                        }
                        
                        @Override
                        public boolean keyDown(int keycode) {
                                // TODO Auto-generated method stub
                                return false;
                        }
                });
        camera.setToOrtho(false, width, height);
        camera.position.set(0, (camera.viewportHeight / 2), 0);
                this.camera.update();
                
                /*
                 * Creation d'un monde, le premier paramètre est un vecteur avec : la gravite en X et la gravite en Y
                 * L'autre paramètre permet la mise à jour ou non des élèments inactifs
                 */
                world = new World(new Vector2(0, -9.8f), true);
                renderer = new Box2DDebugRenderer();
                fps = new FPSLogger();
                
                /* Ici se feront la creation des bodys, pour cela il va falloir tout organiser dans le tiled. Créer un calque
                 * de Bodys et gérer chaque éléments.
                 */
                /*
                 * 3 types différents :
                 * DynamicBody = Fonctionnement d'une balle de tennis, subit la gravité, les chocs, ...
                 * KyneticBody = Fonctionnement d'une plateforme dans Mario, bouge mais ne subit pas la gravite
                 * StaticBody = Le sol
                 *
                 * La creation d'une entite se fait en plusieurs parties :
                 * 1 / La définition de l'entite (avec son type) et sa position
                 * 2 / La creation du body
                 * 3 / La forme de la collision, plusieurs cas possibles (voir toutes les extensions de Shape)
                 * 4 / Le frottement de l'element.
                 * 5 / L'association de la friction au body
                 * EXEMPLE :
                 *
                 * 1 :
                 */
                BodyDef circleDef = new BodyDef();
                circleDef.type = BodyType.DynamicBody;
                circleDef.position.set(10, 50); // ici la position du joueur
                /*
                 * 2 :
                 */
                Body rectangleBody = world.createBody(circleDef);
                /*
                 * 3 :
                 */
                CircleShape circle = new CircleShape();
                circle.setRadius(3f);
                /*
                 * ou
                 */
                PolygonShape polygon = new PolygonShape();
                polygon.setAsBox(camera.viewportWidth * 2, 3.0f);
                /*
                 * 4 :
                 */
                FixtureDef fixture = new FixtureDef();
                fixture.shape = circle;
                fixture.density = 0.4f;
                fixture.friction = 0.2f;
                fixture.restitution = 0.8f;                
                /*
                 * 5 :
                 */
                rectangleBody.createFixture(fixture);
                
                /* ========================= */
                BodyDef ground = new BodyDef();
                ground.position.set(0, 3);
                Body groundBody = world.createBody(ground);
//                PolygonShape polygon = new PolygonShape();
//                polygon.setAsBox(camera.viewportWidth * 2, 3.0f);
                groundBody.createFixture(polygon, 0.0f);        
                
                BodyDef wallDef = new BodyDef();
                wallDef.type = BodyType.StaticBody;
                wallDef.position.set(10, 30);
                Body wallBody = world.createBody(wallDef);
                PolygonShape polygonWall = new PolygonShape();
                polygonWall.setAsBox(this.widthBox, this.heightBox);
                wallBody.createFixture(polygonWall, 0.0f);
                
                BodyDef playerDef = new BodyDef();
                playerDef.type = BodyType.StaticBody;
                playerDef.position.set(45, 30);
                this.playerBody = world.createBody(playerDef);
                PolygonShape polygonPlayer = new PolygonShape();
                polygonPlayer.setAsBox(5, 10); // Attention le setAsBox est traitre, il créer une boite de 5 * 2 et 10 * 2 !!
                this.playerBody.createFixture(polygonPlayer, 0.0f);
                                
                this.handler = new RayHandler(world);
                handler.setCombinedMatrix(camera.combined);
                this.lightTest = new ConeLight(handler, 500, Color.BLUE, 30 / SCALING, 50, 50, 0, 45);
        }

        @Override
        public void hide() {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void pause() {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void resume() {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void dispose() {
                // TODO Auto-generated method stub
                world.dispose();
        }

}