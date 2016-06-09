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
    private Ball ball;
    private Vector3f holePosition;
    private boolean isPlaying;
    
    public BotAgent(Ball ball, Hole hole) {
        this.ball = ball;
        this.holePosition = hole.getLocation();
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
    
    public Vector3f getHolePosition() {
        return holePosition;
    }

    public void setHolePosition(Vector3f holePosition) {
        this.holePosition = holePosition;
    }

    public void performShot(float intensity, float xDir, float yDir) {
        if (intensity < 4f) {
            ball.getBallControl().setxVelocity(((xDir) / 10) * intensity);
            ball.getBallControl().setzVelocity(((yDir) / 10) * intensity);
        } else {
            ball.getBallControl().setxVelocity(((xDir) / 10) * 4f);
            ball.getBallControl().setzVelocity(((yDir) / 10) * 4f);
        }
    }
    
    public void computeShot() {
        Vector3f newVelocity = holePosition.subtract(ball.getLocation());
        Ball testBall = new Ball("testBall",ball.getSpatial());
        Vector3f shot = newVelocity.normalize();
        testBall.setLocation(ball.getLocation());
        
        System.out.println("newVelocity: " + newVelocity);
        System.out.println("shot: " + shot);
        System.out.println("newVelocity/shot:" + newVelocity.divide(shot));
        float point1=0f, point2=4f;
        while (!testBall.getBallControl().isMoving())
        {
            testBall.getBallControl().setxVelocity(shot.getX()*point2);
            testBall.getBallControl().setxVelocity(shot.getY()*point2);
            
        }
        
        shot.scaleAdd(0.2f, shot);
        ball.getBallControl().setxVelocity(shot.getX());
        ball.getBallControl().setzVelocity(shot.getZ());
    }
    
}
