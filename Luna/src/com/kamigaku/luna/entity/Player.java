package com.kamigaku.luna.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player {

	public Body playerBody;
	private float x;
	private float y;
	public float vectorX;
	
	
	public Player() {
		this.vectorX = 30f;
	}
}
