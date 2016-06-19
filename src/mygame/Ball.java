package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author nicolagheza
 */
public class Ball extends Collidables {
    private Spatial s;
    private BallControl control;
    public Ball(String name, Spatial s) {
        this.s = s.clone();
        s.setName(name);
        setExtent(0.992, 0.995, 0.99);
        control = new BallControl(this);
        s.addControl(control);
    }
    public Ball(String name, AssetManager assetManager) {
        s = assetManager.loadModel("Models/ball/ball.j3o");
        s.setName(name);
        setExtent(0.992, 0.995, 0.99);
        control = new BallControl(this);
        s.addControl(control);
    }
    
    public Spatial getSpatial() { return s; }
    
    public void setSpatial(Spatial s) { this.s = s; } 
    
    public BallControl getBallControl() { return control; }
    
    public String getInfonullpointer(){
       return ":D not null";
    }
}
