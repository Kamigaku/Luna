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
        public boolean contactLeft, contactRight, contactTop, contactBot;
        public boolean jumping, wantToJump, falling, jumpedOnce;
        public boolean againstWall, againstObstacle;
        public boolean toRight, toLeft;
        
        
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
                this.contactLeft = this.contactRight = this.contactBot = this.contactTop = false;
                this.againstWall = this.againstObstacle = false;
        }
        
        public void mouvementPlayer(Level levelDatas, boolean toLeft, boolean toRight) {
        	this.resetMouvement();
             Array<Contact> contacts = levelDatas.world.getContactList();
             try {
                 for(Contact contact : contacts) {
                     if(contact.getFixtureA().equals(playerBody.getFixtureList().get(0))) {
                         levelDatas.bodyIsWall(contact.getFixtureB().getBody());
                     }
                     if(contact.getFixtureB().equals(playerBody.getFixtureList().get(0))) {
                         levelDatas.bodyIsWall(contact.getFixtureA().getBody());
                     }
                 }
             } catch(GdxRuntimeException exp) {
             }
             
             /* SETTER PRE-MOUVEMENT */             
             if(playerBody.getLinearVelocity().y < -1f) { // le joueur tombe
                     this.falling = true;
                     this.jumping = false;
             }
             else if(playerBody.getLinearVelocity().y > 1f) { // le joueur est en train de sauter
                     this.falling = false;
                     this.jumping = true;
             }
             else { // le joueur fait du surplace
                     this.falling = this.jumping = false;
             }
             
             if(!this.againstWall) {
                 this.jumpedOnce = false;
             }
             
             if((this.contactLeft || this.contactRight) && this.falling && (toLeft || toRight)) { 
            	 // il est contre un mur et tombe (chute ralentie)
            	 playerBody.setLinearVelocity(new Vector2(playerBody.getLinearVelocity().x, playerBody.getLinearVelocity().y * 0.9f));
             }
             /* FIN SETTER PRE-MOUVEMENT */
             
             /* BOUCLE PRINCIPALE DE MOUVEMENT */
             /*
              * Ici le joueur peut :
              *  - Sauter contre un mur et rebondir contre celui dans la direction oppos�
              *  - Se d�placer de gauche � droite
              *  - Sauter sur place, vers la gauche ou vers la droite
              *  - Le personnage s'arr�te imm�diatement si il ne saute pas ou ne tombe pas
              *  - "Glisse" vers la gauche ou vers la droite
              */
             if(this.wantToJump && !this.againstWall && !this.jumping && !this.falling) {
            	 this.wantToJump = false;
                 if(toLeft) {
                     playerBody.applyLinearImpulse(new Vector2(50000 * -maxVectorX, 50000 * maxVectorY), playerBody.getWorldCenter(), true);
                 }
                 else if(toRight) {
                     playerBody.applyLinearImpulse(new Vector2(50000 * maxVectorX, 50000 * maxVectorY), playerBody.getWorldCenter(), true);
                 }
                 else {
                	 playerBody.applyLinearImpulse(new Vector2(0, 50000 * maxVectorY), playerBody.getWorldCenter(), true);
                 }
             }
             else if(this.wantToJump && this.againstWall && !this.jumpedOnce) { 
            	 // le joueur veut sauter, est contre un mur et n'a pas deja saut�
				this.wantToJump = false;
				if(this.contactRight) {
					this.playerBody.applyLinearImpulse(new Vector2(50000 * -maxVectorX, 50000 * maxVectorY * 2), playerBody.getWorldCenter(), true);
				}
				else if(this.contactLeft) {
					this.playerBody.applyLinearImpulse(new Vector2(50000 * maxVectorX, 50000 * maxVectorY * 2), playerBody.getWorldCenter(), true);
				}
				this.jumpedOnce = true;
             }
             else if((toRight || toLeft)) { // le joueur va a gauche ou droite et ne saute pas
	             if(toRight) {
	            	 if(!this.againstWall || (this.againstWall && !this.contactRight))
	            		 this.playerBody.applyLinearImpulse(new Vector2(75 * maxVectorX, playerBody.getLinearVelocity().y), playerBody.getWorldCenter(), true);
	             }
	             else if(toLeft) {
	            	 if(!this.againstWall || (this.againstWall && !this.contactLeft))
	            		 playerBody.applyLinearImpulse(new Vector2(75 * -maxVectorX, playerBody.getLinearVelocity().y), playerBody.getWorldCenter(), true);
	             }
             }
             else if(!toRight && !toLeft && !this.jumping && !this.falling) {
            	 this.playerBody.setLinearVelocity(new Vector2(playerBody.getLinearVelocity().x * 0.9f, playerBody.getLinearVelocity().y));
             }
             /* FIN BOUCLE PRINCIPALE DE MOUVEMENT */

             this.wantToJump = false;
        }
        
        public void mouvementPlayer() {
        	this.resetMouvement();
        	// appel � la fonction des collisions
        	
        	//fin appel
        	
        	// 1er cas : il est contre un obstacle autre qu'un mur pour rebondir
        	if(this.againstObstacle) {
        		if(wantToJump) {
        			if(toLeft && !toRight) {
        				
        			}
        			else if(!toLeft && toRight) {
        				
        			}
        			else {
        				
        			}
        		}
        		else if(toLeft && !toRight) {
        			
        		}
        		else if(!toLeft && toRight) {
        			
        		}
        		// ???
        		else {
        			// slow le vitesse
        		}
        	}
        	// Fin 1er cas
        	
        	
        	
        	// 2�me cas : il est contre un mur qui lui permet de rebondir
        	else if(this.againstWall && !this.againstObstacle) {
        		if(this.wantToJump) {
        			if(this.contactRight) {
        				// jump vers la gauche
        			}
        			else if(this.contactLeft) {
        				// jump vers la droite
        			}
        		}
        		else if((this.toLeft || this.toRight) && this.contactLeft && this.falling) {
        			// slow
        		}
        		else if(this.falling) {
        			// tombe
        		}
        	}
        	// Fin 2�me cas
        	
        	
        	
        	// 3�me cas : il tombe, n'est pas contre un mur ni un obstacle
        	else {
        		// il tombe
        	}
        	// Fin 3�me cas
        	
        }
        
        public void resetMouvement() {
			this.againstWall = this.contactLeft = this.contactRight = this.contactBot = this.contactTop = false;
		}

		public Vector2 getTargetPos() {
		        return this.playerBody.getPosition();
		}
}