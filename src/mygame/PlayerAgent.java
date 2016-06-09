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
        setBall(ball);
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
        if (intensity < 4f) {
            ball.getBallControl().setxVelocity(((xDir) / 10) * intensity);
            ball.getBallControl().setzVelocity(((yDir) / 10) * intensity);
        } else {
            ball.getBallControl().setxVelocity(((xDir) / 10) * 4f);
            ball.getBallControl().setzVelocity(((yDir) / 10) * 4f);
        }
    }
    
}
