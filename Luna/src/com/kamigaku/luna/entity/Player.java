package com.kamigaku.luna.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kamigaku.luna.MainClass;
import com.kamigaku.luna.gameScreen.Level;

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
        public boolean contactBottomLeft, contactBottomRight, contactTopLeft, contactTopRight;
        public boolean jumping, wantToJump, falling, jumpedOnce;
        public boolean againstWall, againstObstacle;
        public boolean toRight, toLeft;
		public boolean contactLeft;
		public boolean contactRight;
		public boolean contactBot;
		public boolean contactTop;
        
        
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
                this.falling = this.jumping = this.wantToJump = this.jumpedOnce = false;
                this.contactBottomLeft = this.contactBottomRight = this.contactTopLeft = this.contactTopRight = false;
                this.againstWall = this.againstObstacle = false;
                this.toLeft = this.toRight = false;
        }
                
        public void mouvementPlayer(Level levelDatas) {
        	this.resetMouvement();
        	// appel à la fonction des collisions
            Array<Contact> contacts = levelDatas.world.getContactList();
            try {
                for(Contact contact : contacts) {
                    if(contact.getFixtureA().equals(playerBody.getFixtureList().get(0))) {
                        levelDatas.bodyIsWall(contact.getFixtureB().getBody(), contacts);
                    }
                    if(contact.getFixtureB().equals(playerBody.getFixtureList().get(0))) {
                        levelDatas.bodyIsWall(contact.getFixtureA().getBody(), contacts);
                    }
                }
            } catch(GdxRuntimeException exp) {
            }
            if(this.playerBody.getLinearVelocity().y == 0f && !againstWall) {
            	this.againstObstacle = true;
            }
        	//fin appel
        	// 1er cas : il est contre un obstacle autre qu'un mur pour rebondir
        	if(this.againstObstacle) {
        		this.falling = false;
        		if(this.playerBody.getLinearVelocity().y < 0f) {
        			this.jumping = false;
        			this.falling = true;
        		}
        		if(!this.contactLeft && !this.contactRight)
        			this.jumpedOnce = false;
        		if(wantToJump) {
        			if(toLeft && !toRight) {
        				if(!this.contactLeft && !this.contactRight)
        					this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 70) * -maxVectorX, (MainClass.PIXELS * 70) * maxVectorY), playerBody.getWorldCenter(), true);
        			}
        			else if(!toLeft && toRight) {
        					if(!this.contactLeft && !this.contactRight)
    							this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 70) * maxVectorX, (MainClass.PIXELS * 70) * maxVectorY), playerBody.getWorldCenter(), true);
        			}
        			else if(!jumpedOnce) {
        				this.jumpedOnce = true;
        				this.playerBody.applyLinearImpulse(new Vector2(this.playerBody.getLinearVelocity().x, (MainClass.PIXELS * 20) * maxVectorY), playerBody.getWorldCenter(), true);
        			}
        			this.wantToJump = false;
        			this.jumping = true;
        		}
        		else if(toLeft && !toRight && !wantToJump && !jumping) {
        			this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 20) * -maxVectorX, (MainClass.PIXELS * 7) * -maxVectorY), playerBody.getWorldCenter(), true);
        		}
        		else if(!toLeft && toRight && !wantToJump && !jumping) {
        			this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 20) * maxVectorX, (MainClass.PIXELS * 7) * -maxVectorY), playerBody.getWorldCenter(), true);
        		}
        		else {
    				this.playerBody.applyLinearImpulse(new Vector2(this.playerBody.getLinearVelocity().x * -150, this.playerBody.getLinearVelocity().y), playerBody.getWorldCenter(), true);
        		}
        	}
        	// Fin 1er cas
        	
        	
        	
        	// 2ème cas : il est contre un mur qui lui permet de rebondir
        	else if(this.againstWall && !this.againstObstacle) {
        		this.againstObstacle = false;
        		this.falling = false;
        		if(this.playerBody.getLinearVelocity().y < 0f)
        			this.falling = true;
        		if(this.wantToJump) {
        			if(this.contactRight || (this.contactBot && this.toLeft)) {
        				this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 70) * -maxVectorX, (MainClass.PIXELS * 70) * maxVectorY), playerBody.getWorldCenter(), true);
        			}
        			else if(this.contactLeft || (this.contactBot && this.toRight)) {
        				this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 70) * maxVectorX, (MainClass.PIXELS * 70) * maxVectorY), playerBody.getWorldCenter(), true);
        			}
        			else if(this.contactBot) {
        				this.playerBody.applyLinearImpulse(new Vector2(this.playerBody.getLinearVelocity().x, (MainClass.PIXELS * 10) * maxVectorY), playerBody.getWorldCenter(), true);
        			}
        		}
        		else if(this.toLeft || this.toRight) {
        			if(falling) {
        				if(this.toLeft) {
        					if(this.contactLeft)
        						this.playerBody.setLinearVelocity(new Vector2(this.playerBody.getLinearVelocity().x, -10f));
        					else
        						this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS) * -maxVectorX, (MainClass.PIXELS * 2) * -maxVectorY), playerBody.getWorldCenter(), true);
        				}
        				if(this.toRight) {
        					if(this.contactRight)
        						this.playerBody.setLinearVelocity(new Vector2(this.playerBody.getLinearVelocity().x, -10f));
        					else
        						this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS) * maxVectorX, (MainClass.PIXELS * 2) * -maxVectorY), playerBody.getWorldCenter(), true);
        				}
        			}
        			else {
        				if(toLeft && !toRight) {
    						this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 20) * -maxVectorX, (MainClass.PIXELS * 7) * -maxVectorY), playerBody.getWorldCenter(), true);
                		}
                		else if(!toLeft && toRight) {
            				this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 20) * maxVectorX, (MainClass.PIXELS * 7) * -maxVectorY), playerBody.getWorldCenter(), true);
                		}
        			}
        		}
           		else {
    				this.playerBody.applyLinearImpulse(new Vector2(this.playerBody.getLinearVelocity().x * -150, this.playerBody.getLinearVelocity().y), playerBody.getWorldCenter(), true);
        		}	
        	}
        	// Fin 2ème cas
        	
        	
        	
        	// 3ème cas : il tombe, n'est pas contre un mur ni un obstacle
        	else {
        		if(this.playerBody.getLinearVelocity().y > 0f) {
        			this.jumping = true;
        			this.falling = false;
        		}
        		else if(this.playerBody.getLinearVelocity().y < 0f) {
        			this.falling = true;
        			this.jumping = false;
            		if(toLeft && !toRight) {
            			this.playerBody.applyLinearImpulse(new Vector2(-maxVectorX * 40, this.playerBody.getLinearVelocity().y), playerBody.getWorldCenter(), true);
            		}
            		else if(!toLeft && toRight) {
            			this.playerBody.applyLinearImpulse(new Vector2(maxVectorX * 40, this.playerBody.getLinearVelocity().y), playerBody.getWorldCenter(), true);
            		}
        		}
        		
        		// il tombe
        	}
        	// Fin 3ème cas
        	
        }
        
        public void resetMouvement() {
			this.againstObstacle = false;
			this.againstWall = false;
			this.contactBot = this.contactLeft = this.contactRight = this.contactTop = false;
			this.contactBottomLeft = this.contactBottomRight = this.contactTopLeft = this.contactTopRight = false;
		}

		public Vector2 getTargetPos() {
		        return this.playerBody.getPosition();
		}
}