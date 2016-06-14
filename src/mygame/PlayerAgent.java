/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author nicolagheza
 */
public class PlayerAgent implements Agent {
    
    private Ball ball;
    private boolean isPlaying;
    
    public PlayerAgent(Ball ball) {
        this.ball = ball;
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

    public void performShot(float intensity, float xDir, float yDir) {
        intensity = intensity /10;
        if (intensity < 0.4f) {
            ball.getBallControl().setxVelocity(xDir * intensity);
            ball.getBallControl().setzVelocity(yDir * intensity);
        } else {
            ball.getBallControl().setxVelocity(xDir * 4f);
            ball.getBallControl().setzVelocity(yDir * 4f);
        }
    }
    
}
