/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author nicolagheza
 */
public class BallControl extends AbstractControl implements Savable, Cloneable {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private float speed = 0f;
    private Vector3f velocity = new Vector3f(0f, 0f, 0f), normal, gVector = new Vector3f(0f, -1f, 0f);
    final float friction = 0.99f, fps = 1/60f;
    private final float WIDTH = 2f;
    private final float RADIUS = WIDTH / 2;
    private float slope = 0;
    private Ball ball;
    private float time = 0;
    private float gravity = 0.1f;
    private float wind1=1f;
    private float wind2=1f;
    private int upHill = 0; //-1 is downhill 0 is flat terrain, 
    // 1 is uphill, 2 is falling , 3 is flying

    /**
     * Optional custom constructor with arguments that can init custom fields.
     * Note: you cannot modify the spatial here yet!
     */
    public BallControl() {
    }

    ;
    
    public BallControl(Ball ball) {
        this.ball = ball;
    }

    public void computeMovement() {
        time += fps;

        //System.out.println(velocity.toString());
        
        if(upHill == 1 || upHill == -1){
            
            //System.out.println(upHill + "    " + velocity.getY());
            
            gVector.mult(time);
            velocity.mult((float) Math.pow(1, fps));
            
            Vector3f slopeAcceleration = gVector.subtract(gVector.project(normal));
            velocity = velocity.add(slopeAcceleration.mult(fps)).mult(friction);
        } else if (upHill == 0) {
//            System.out.println("flat" + velocity.toString());
            velocity.setX(velocity.getX() * friction * wind1);
            velocity.setZ(velocity.getZ() * friction * wind2);
        } else if (upHill == 2) {
//            System.out.println("falling" + velocity.toString());
            velocity.setX(velocity.getX() * friction);
            velocity.setY(velocity.getY() - gravity * time);
            velocity.setZ(velocity.getZ() * friction);
        } else if (upHill == 3) {
//            System.out.println("flying" + velocity.toString());
            velocity.setX(velocity.getX() * friction);
            velocity.setY(velocity.getY() - gravity * time);
            velocity.setZ(velocity.getZ() * friction);
        }

        if (Math.abs(velocity.getX()) < 0.05f && Math.abs(velocity.getY()) < 0.05f
                && Math.abs(velocity.getZ()) < 0.05f && upHill == 0) {
            velocity = new Vector3f(0f, 0f, 0f);
            resetTime();
        }
        //System.out.println("vel" + velocity.toString());
        //System.out.println("time: " + time);
    }
    public void changeWind(String wind, float xD, float yD){
        float newX = xD/yD;
        
        
       if(newX>=0 && newX<=15f){
        if(wind=="Left"){
            wind1=0.99f;
            wind2=0.98f;
            
        }
        else if(wind=="Right"){
            wind1=0.98f;
            wind2=0.99f;
           
        }
        else if(wind=="Up"){
            wind1=0.999f;
            wind2=0.999f;
            
        }
        else if(wind =="Down"){
            wind1=0.98f;
            wind2=0.98f;
            
        } 
       }
       else if(newX<=0 && newX>=-15f){
          if(wind=="Left"){
            wind1=0.98f;
            wind2=0.99f;
            
        }
        else if(wind=="Right"){
            wind1=0.99f;
            wind2=0.98f;
           
        }
        else if(wind=="Up"){
            wind1=0.999f;
            wind2=0.999f;
            
        }
        else if(wind =="Down"){
            wind1=0.98f;
            wind2=0.98f;
            
        }  
       }
    }
    
    public void setNormal(Vector3f normal){
        this.normal = normal;
    }
    public Vector3f getNormal(){
        return normal;
    }
    
    public float getSpeed(){
        return (float) Math.sqrt(Math.pow(velocity.getX(),2)  +  Math.pow(velocity.getZ(),2));
    }
    public void resetTime() {
        time = 0f;
    }

    public void setUpHill(int u) {
        this.upHill = u;
    }

    public int getUpHill() {
        return this.upHill;
    }

    public void setSlope(float slope) {
        this.slope = slope;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y, float z) {
        velocity = new Vector3f(x, y, z);
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public float getxVelocity() {
        return velocity.getX();
    }

    public float getyVelocity() {
        return velocity.getY();
    }

    public float getzVelocity() {
        return velocity.getZ();
    }

    public void setxVelocity(float x) {
        velocity.setX(x);
    }

    public void setyVelocity(float y) {
        velocity.setY(y);
    }
    
    public Vector3f getDistance(Vector3f v){
        float xValue = 0f;
        float zValue = 0f;
        
        while(Math.abs(v.getX()) > 0.05f || Math.abs(v.getZ()) > 0.05f){
            
            xValue += v.getX();
            zValue += v.getZ();
            
            v.setX(v.getX() * friction);
            v.setZ(v.getZ() * friction);
            
        }
        Vector3f distance = new Vector3f(xValue,0f,zValue);
        return distance;
    }
    
    /* 
     * return wether the ball is moving or not..
     */
    public boolean isMoving() {
        if (Math.abs(velocity.getX()) <= 0.01f && Math.abs(velocity.getZ()) <= 0.01f) {
            return false;
        }
        return true;
    }
    
    public void setzVelocity(float z) {
        velocity.setZ(z);
    }

    public float getFriction() {
        return this.friction;
    }

    public float getRadius() {
        return RADIUS;
    }

    public void rotateBall() {
        if (ball != null) {
//           ball.getSpatial().rotate(velocity.z * 0.1f,velocity.y * 10f, velocity.x * 0.1f);
//           ball.getSpatial().rotate(0f, 0f, FastMath.HALF_PI);
        }
    }

    /**
     * This method is called when the control is added to the spatial, and when
     * the control is removed from the spatial (setting a null value). It can be
     * used for both initialization and cleanup.
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        /* Example:
         if (spatial != null){
         // initialize
         }else{
         // cleanup
         }
         */
    }

    /**
     * Implement your spatial's behaviour here. From here you can modify the
     * scene graph and the spatial (transform them, get and set userdata, etc).
     * This loop controls the spatial while the Control is enabled. *
     */
    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        BallControl control = new BallControl();
        //TODO: copy parameters to new Control
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
