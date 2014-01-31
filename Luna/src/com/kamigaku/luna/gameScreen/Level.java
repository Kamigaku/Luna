package com.kamigaku.luna.gameScreen;

import net.dermetfan.utils.libgdx.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kamigaku.luna.entity.Lune;
import com.kamigaku.luna.entity.Player;

public class Level {
        
    private TiledMap map;
    public OrthographicCamera camera;
    public OrthogonalTiledMapRenderer rendererMap;
    public Box2DMapObjectParser parser;
    public Player player;
    public Lune lune;
    public int mapWidth;
    public int mapHeight;
    public int tileHeight;
    public int tileWidth;
    private float screenWidth;
    private float screenHeight;
    public float scaling;
    public World world;
    
    public Level(String pathForMap, float gravity, int playerPositionX, int playerPositionY, float scaling, float posXLune, float posYLune) {
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
		
		/* Players settings */
		this.player = new Player(playerPositionX, playerPositionY, this.tileWidth, this.tileHeight, this.world);
    }
    
    public void bodyIsWall(Body body) {
        ObjectMap<String, Body> bodies = parser.getBodies();
        String keyOfBody = bodies.findKey(body, false);
        if(keyOfBody.contains("Wall")) {
                getPositionContact();
                if(this.player.contactTop || this.player.contactBot) {
                	this.player.againstWall = false;
                }
                else {
                	this.player.againstWall = true;
                }
        }
        else {
                this.player.againstWall = false;
        }
    }	
    
    private void getPositionContact() {
        Array<Contact> contacts = this.world.getContactList();
        Vector2 positionPlayer = null;
        Vector2 positionObstacle = null;
        Shape shape = null;
        Vector2 tailleObstacle = new Vector2();
        float xObstacle = 0, yObstacle = 0;
        for(Contact contact : contacts) {
                if(contact.getFixtureA().getBody().getPosition() == this.player.getTargetPos()) {
                        positionPlayer = contact.getFixtureA().getBody().getPosition();
                        positionObstacle = contact.getFixtureB().getBody().getPosition();
                        shape = contact.getFixtureA().getShape();
                }
                else {
                        positionPlayer = contact.getFixtureB().getBody().getPosition();
                        positionObstacle = contact.getFixtureA().getBody().getPosition();
                        shape = contact.getFixtureA().getShape();
                }
        }
        switch(shape.getType()) {
            case Chain :
            	break;
            case Circle : 
            	break;
            case Edge : 
            	break;
            case Polygon : 
            	for(int i = 0; i < ((PolygonShape)shape).getVertexCount(); i++) {
            		((PolygonShape)shape).getVertex(i, tailleObstacle);
            		if(tailleObstacle.x > xObstacle)
            			xObstacle = tailleObstacle.x;
            		if(tailleObstacle.y > yObstacle)
            			yObstacle = tailleObstacle.y;
            	}
            	break;
        }
        float yMaxPlayer = positionPlayer.y + (this.player.sizeY);
        float yMinPlayer = positionPlayer.y - (this.player.sizeY);
        float yMaxObstacle = positionObstacle.y + yObstacle;
        float yMinObstacle = positionObstacle.y;
        float xMinPlayer = positionPlayer.x - (this.player.sizeX);
        float xMaxPlayer = positionPlayer.x + (this.player.sizeX);
        float xMaxObstacle = positionObstacle.x + xObstacle;
        float xMinObstacle = positionObstacle.x;
//    	System.out.println("yMaxPlayer :" + yMaxPlayer + " | yMaxObstacle : " + yMaxObstacle);
//    	System.out.println("yMinPlayer :" + yMinPlayer + " | yMinObstacle : " + yMinObstacle);
//    	System.out.println("xMinPlayer :" + xMinPlayer + " | xMinObstacle : " + xMinObstacle);
//    	System.out.println("xMaxPlayer :" + xMaxPlayer + " | xMaxObstacle : " + xMaxObstacle);
//    	System.out.println("==========================================");
        if(
        		((yMaxPlayer <= yMaxObstacle && yMaxPlayer >= yMinObstacle) || (yMinPlayer <= yMaxObstacle && yMinPlayer >= yMinObstacle)) &&
        		xMinPlayer >= xMaxObstacle
		) {
                this.player.contactLeft = true;
        }
        else if(
        		((yMaxPlayer <= yMaxObstacle && yMaxPlayer >= yMinObstacle) || (yMinPlayer <= yMaxObstacle && yMinPlayer >= yMinObstacle)) &&
        		xMaxPlayer <= xMinObstacle               		
		) {
        	this.player.contactRight = true;
        }
        else if(
        		((xMaxPlayer <= xMaxObstacle && xMaxPlayer >= xMinObstacle) || (xMinPlayer <= xMaxObstacle && xMinPlayer >= xMinObstacle)) &&
        		yMinPlayer >= yMaxObstacle  
		) { 
        	this.player.contactBot = true;
        }
        else if(
        		((xMaxPlayer <= xMaxObstacle && xMaxPlayer >= xMinObstacle) || (xMinPlayer <= xMaxObstacle && xMinPlayer >= xMinObstacle)) &&
        		yMaxPlayer <= yMinObstacle  
		) { 
        	this.player.contactTop = true;
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