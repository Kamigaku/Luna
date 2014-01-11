package com.kamigaku.luna.gameScreen;

import net.dermetfan.utils.libgdx.box2d.Box2DMapObjectParser;
import box2dLight.ConeLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.kamigaku.luna.entity.Player;

public class Level {
	
	private TiledMap map;
	public OrthographicCamera camera;
    public OrthogonalTiledMapRenderer rendererMap;
    public Player player;
    public ConeLight lune;
    private int mapWidth;
    private int mapHeight;
    private int tileHeight;
    private int tileWidth;
    private float screenWidth;
    private float screenHeight;
    public static final float SCALING = 0.8f;
    public World world;
    
    public Level(String pathForMap) {
		this.screenWidth = Gdx.graphics.getWidth() * SCALING;
		this.screenHeight = Gdx.graphics.getHeight() * SCALING;
		this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, screenWidth, screenHeight);
        this.camera.position.set((camera.viewportWidth / 2), (camera.viewportHeight / 2), 0);
		this.camera.update();
		this.world = new World(new Vector2(0, -9.8f), true);
    	this.map = new TmxMapLoader().load(pathForMap);
        this.mapHeight = map.getProperties().get("height", Integer.class);
        this.mapWidth = map.getProperties().get("width", Integer.class);
        this.tileHeight = map.getProperties().get("tileheight", Integer.class);
        this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
		this.rendererMap = new OrthogonalTiledMapRenderer(map);
		this.rendererMap.setView(camera);
		new Box2DMapObjectParser().load(world, map.getLayers().get("Body"));
		
		/* Players settings */
		this.player = new Player();
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		circleDef.position.set(100, (3 * tileHeight) + 50); // ici la position du joueur
		Body rectangleBody = world.createBody(circleDef);
		PolygonShape polygon = new PolygonShape();
		polygon.setAsBox(50, 50);
		FixtureDef fixture = new FixtureDef();
		fixture.shape = polygon;
		fixture.density = 0.4f;
		fixture.friction = 0.2f;
		fixture.restitution = 0.0f;
		rectangleBody.createFixture(fixture);
		this.player.playerBody = rectangleBody;
		this.player.playerBody.setFixedRotation(true);
		/* End Players settings */
    }
 
}
