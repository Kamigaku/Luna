package com.kamigaku.luna.gameScreen;

import net.dermetfan.utils.libgdx.box2d.Box2DMapObjectParser;
import box2dLight.ConeLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.kamigaku.luna.entity.Player;
import com.kamigaku.luna.listener.PlayerContactListener;

public class Level {
	
	private TiledMap map;
	public OrthographicCamera camera;
    public OrthogonalTiledMapRenderer rendererMap;
    public Box2DMapObjectParser parser;
    public Player player;
    public ConeLight lune;
    public int mapWidth;
    public int mapHeight;
    public int tileHeight;
    public int tileWidth;
    private float screenWidth;
    private float screenHeight;
    public static final float SCALING = 0.8f;
    public World world;
    
    public Level(String pathForMap, float gravity) {
		this.screenWidth = Gdx.graphics.getWidth() * SCALING;
		this.screenHeight = Gdx.graphics.getHeight() * SCALING;
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
		
		/* Players settings */
		this.player = new Player(50, 3, this.tileHeight, this.world);
    }
 
}
