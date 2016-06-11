package mygame;


/**
 *
 * @author nicolagheza
 */
public interface Agent {
    void performShot(float intensity,float xDir, float yDir);
    void setIsPlaying(boolean isPlaying);
    boolean isPlaying();
    void setBall(Ball ball);
    Ball getBall();
}
