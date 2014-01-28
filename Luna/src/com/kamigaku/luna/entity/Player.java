package com.kamigaku.luna.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player {

        public Body playerBody;
        public BodyDef bodyDefPlayer;
        public PolygonShape polygonPlayer;
        public FixtureDef fixturePlayer;
        public float vectorX;
        public float vectorY;
        public float maxVectorX;
        public float maxVectorY;
        public final int sizeX = 30;
        public final int sizeY = 30;
        public boolean contactLeft, contactRight, contactTop, contactBot;
        public boolean againstWall, jumping, wantToJump, falling, jumpedOnce;
        
        
        public Player(int positionX, int positionY, int tileWidth, int tileHeight, World world) {
                bodyDefPlayer = new BodyDef();
                bodyDefPlayer.type = BodyType.DynamicBody;
                bodyDefPlayer.position.set((positionX * tileWidth), (positionY * tileHeight) + 50);
                playerBody = world.createBody(bodyDefPlayer);
                polygonPlayer = new PolygonShape();
                polygonPlayer.setAsBox(sizeX, sizeY);
                fixturePlayer = new FixtureDef();
                fixturePlayer.shape = polygonPlayer;
                fixturePlayer.density = 0.8f;
                fixturePlayer.friction = 0.5f;
                fixturePlayer.restitution = 0.0f;
                playerBody.createFixture(fixturePlayer);
                playerBody.setFixedRotation(true);
                playerBody.setUserData(new String("player"));
                this.vectorX = 5f;
                this.vectorY = 5f;
                this.maxVectorX = 70f;
                this.maxVectorY = 70f;
                this.falling = this.jumping = this.wantToJump = this.againstWall = this.jumpedOnce = false;
                this.contactLeft = this.contactRight = this.contactBot = this.contactTop = false;
        }
        
        public Vector2 getTargetPos() {
                return this.playerBody.getPosition();
        }

		public void resetMouvement() {
			this.againstWall = this.contactLeft = this.contactRight = this.contactBot = this.contactTop = false;
		}
}