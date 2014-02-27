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
import com.kamigaku.luna.maingame.Level;

public class Player {

        public Body playerBody;
        public BodyDef bodyDefPlayer;
        public PolygonShape polygonPlayer;
        public FixtureDef fixturePlayer;
        public float vectorX;
        public float vectorY;
        public float maxVectorX;
        public float maxVectorY;
        public final int sizeX = 20;
        public final int sizeY = 20;
        public int hp, maxHp;
        public boolean contactBottomLeft, contactBottomRight, contactTopLeft, contactTopRight;
        public boolean jumping, wantToJump, falling, jumpedOnce;
        public boolean againstWall, againstObstacle;
        public boolean toRight, toLeft;
        public boolean contactLeft, contactRight, contactBot, contactTop;
        public boolean invincible;
        public float timeInvincible;
        
        
        public Player(int positionX, int positionY, int tileWidth, int tileHeight, World world) {
            this.maxHp = this.hp = 5;
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
            playerBody.setUserData("player");
            this.vectorX = 5f;
            this.vectorY = 5f;
            this.maxVectorX = 70f;
            this.maxVectorY = 70f;
            this.falling = this.jumping = this.wantToJump = this.jumpedOnce = false;
            this.contactBottomLeft = this.contactBottomRight = this.contactTopLeft = this.contactTopRight = false;
            this.againstWall = this.againstObstacle = false;
            this.toLeft = this.toRight = false;
            this.invincible = false;
            this.timeInvincible = 0f;
        }
                
        public void mouvementPlayer(Level levelDatas, float delta) {
            this.resetMouvement();
            this.timeInvincible += delta;
            if(this.timeInvincible >= 2f) {
                this.timeInvincible = 0f;
                this.invincible = false;                
            }
            Array<Contact> contacts = levelDatas.world.getContactList();
            try {
                for(int i = 0; i < contacts.size; i++) {
                    if(contacts.get(i).getFixtureA().equals(playerBody.getFixtureList().get(0))) {
                        levelDatas.bodyIsWall(contacts.get(i).getFixtureB().getBody(), contacts);
                    }
                    else if(contacts.get(i).getFixtureB().equals(playerBody.getFixtureList().get(0))) {
                        levelDatas.bodyIsWall(contacts.get(i).getFixtureA().getBody(), contacts);
                    }
                }
            } catch(GdxRuntimeException exp) {
                for(StackTraceElement ste : exp.getStackTrace())
                    System.err.println(ste);
            }
            if(this.playerBody.getLinearVelocity().y == 0f && !againstWall) {
            	this.againstObstacle = true;
            }
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
        		else if(toLeft && !toRight && !wantToJump && !jumping && !contactTop) {
        			this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 20) * -maxVectorX, (MainClass.PIXELS * 7) * -maxVectorY), playerBody.getWorldCenter(), true);
        		}
        		else if(!toLeft && toRight && !wantToJump && !jumping && !contactTop) {
        			this.playerBody.applyLinearImpulse(new Vector2((MainClass.PIXELS * 20) * maxVectorX, (MainClass.PIXELS * 7) * -maxVectorY), playerBody.getWorldCenter(), true);
        		}
        		else {
    				this.playerBody.applyLinearImpulse(new Vector2(this.playerBody.getLinearVelocity().x * -150, this.playerBody.getLinearVelocity().y), playerBody.getWorldCenter(), true);
        		}
        	}
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
        	}
        	
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
        
        public int getCurrentHp() {
            return this.hp;
        }
        
        public void getHit() {
            if(!this.invincible) {
                this.hp--;
                if(hp < 0)
                    hp = 0;
                this.invincible = true;
            }
        }
}