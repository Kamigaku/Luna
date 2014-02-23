package com.kamigaku.luna.gameScreen;

import java.util.ArrayList;

import net.dermetfan.utils.libgdx.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kamigaku.luna.entity.Lune;
import com.kamigaku.luna.entity.Player;

public class Level {
        
	public SpriteBatch batch;
    public TiledMap map;
    public OrthographicCamera camera;
    public OrthogonalTiledMapRenderer rendererMap;
    public Box2DMapObjectParser parser;
    public Player player;
    public Lune lune;
    public int mapWidth;
    public int mapHeight;
    public int tileHeight;
    public int tileWidth;
    public float screenWidth, screenHeight;
    public float scaling;
    public World world;
    
    public Level(String pathForMap, float gravity, int playerPositionX, int playerPositionY, float scaling, float posXLune, float posYLune) {
    	this.batch = new SpriteBatch();
    	this.scaling = scaling;
		this.screenWidth = Gdx.graphics.getWidth() * this.scaling;
		this.screenHeight = Gdx.graphics.getHeight() * this.scaling;
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, screenWidth, screenHeight);
		this.camera.position.set((camera.viewportWidth / 2), (camera.viewportHeight / 2), 0);
		this.camera.update();
		this.world = new World(new Vector2(0, gravity), true);
		this.map = new TmxMapLoader().load(pathForMap);
		this.mapHeight = map.getProperties().get("height", Integer.class);
		this.mapWidth = map.getProperties().get("width", Integer.class);
		this.tileHeight = map.getProperties().get("tileheight", Integer.class);
		this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
		this.rendererMap = new OrthogonalTiledMapRenderer(map);
		this.rendererMap.setView(camera);
		this.parser = new Box2DMapObjectParser();
		this.parser.load(world, map.getLayers().get("Body"));
		this.lune = new Lune(posXLune, posYLune, world, camera);
		this.player = new Player(playerPositionX, playerPositionY, this.tileWidth, this.tileHeight, this.world);
        Gdx.input.setInputProcessor(new InputProcessor() {
            
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        		if(player.toLeft)
        			player.toLeft = false;
        		if(player.toRight)
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
    
    public void bodyIsWall(Body body, Array<Contact> contacts) {
        ObjectMap<String, Body> bodies = parser.getBodies();
        String keyOfBody = bodies.findKey(body, false);
        getPositionContact(contacts);
        if(keyOfBody.contains("Wall")) {
        	this.player.againstWall = true;
        }
        else {
            this.player.againstObstacle = true;
        }
    }	
    
    private void getPositionContact(Array<Contact> contacts) {
        Vector2 positionPlayer = this.player.getTargetPos();
        float relPosPlayerXMin = positionPlayer.x - this.player.sizeX;
        float relPosPlayerXMax = positionPlayer.x + this.player.sizeX;
        float relPosPlayerYMin = positionPlayer.y - this.player.sizeY;
        float relPosPlayerYMax = positionPlayer.y + this.player.sizeY;
        ArrayList<Vector2> pointsContact = new ArrayList<Vector2>();
        for(Contact contact : contacts) {
        	for (int i = 0; i < contact.getWorldManifold().getNumberOfContactPoints(); i++) {
        		   pointsContact.add(new Vector2(contact.getWorldManifold().getPoints()[i].x, contact.getWorldManifold().getPoints()[i].y));
    		}
        }        
        for(Vector2 pointContact : pointsContact) {
        	if(pointContact.x <= relPosPlayerXMin && pointContact.y >= relPosPlayerYMin && pointContact.y <= relPosPlayerYMax) {
        		this.player.contactLeft = true;
        	}
        	if(pointContact.x >= relPosPlayerXMax && pointContact.y >= relPosPlayerYMin && pointContact.y <= relPosPlayerYMax) {
        		this.player.contactRight = true;
        	}
        	if(pointContact.y <= relPosPlayerYMin && pointContact.x >= relPosPlayerXMin && pointContact.x <= relPosPlayerXMax) {
        		this.player.contactBot = true;
        	}
        	if(pointContact.y >= relPosPlayerYMax && pointContact.x >= relPosPlayerXMin && pointContact.x <= relPosPlayerXMax) {
        		this.player.contactTop = true;
        	}
        }    
    }
    
    public void mouvementCamera() {
		if(player.getTargetPos().x >= ((Gdx.graphics.getWidth() * this.scaling) / 2) && 
				player.getTargetPos().x <= (this.mapWidth * tileWidth) - ((Gdx.graphics.getWidth() * this.scaling) / 2)) {
			camera.position.x = player.getTargetPos().x;
		}
		if(player.getTargetPos().y >= ((Gdx.graphics.getHeight() * this.scaling) / 2) && 
				player.getTargetPos().y <= (mapHeight * tileHeight) - ((Gdx.graphics.getHeight() * this.scaling) / 2)) {
			camera.position.y = player.getTargetPos().y;
		}
	}
 
}
