/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author nicolagheza
 */
public class BotAgent implements Agent {
    private PhysicsEngine physics;
    private Ball ball;
    private Vector3f holePosition;
    private boolean isPlaying, scored = false;
    private BotStrategy bot;
    
    public BotAgent(Ball ball, Hole hole) {
        this.ball = ball;
        this.holePosition = hole.getLocation();
    }
    
    public void computeShot() {
        bot = new BinarySearchBot(physics,ball,holePosition);
        Vector3f shot = bot.computeShot();
        ball.getBallControl().setVelocity(shot);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }
    
    public void setScored(boolean scored){
    	this.scored = scored;
    }
    
    public boolean scored(){
    	return scored;
    }
    
    public Vector3f getHolePosition() {
        return holePosition;
    }

    public void setHolePosition(Vector3f holePosition) {
        this.holePosition = holePosition;
    }
    
    public void addPhysics(PhysicsEngine physics) {
        this.physics = physics;
    }
    
}
