package mygame;


/**
 *
 * @author nicolagheza
 */
public interface Agent {
    void setIsPlaying(boolean isPlaying);
    boolean isPlaying();
    void setBall(Ball ball);
    Ball getBall();
    void setScored(boolean scored);
	boolean scored();
}
