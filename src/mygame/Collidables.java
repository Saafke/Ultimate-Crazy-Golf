package mygame;

import com.jme3.math.Vector3f;

public abstract class Collidables{
     private Vector3f location;
     private double Xlocation;
     private double Ylocation; //y & z 'reversed as always in JME
     private double Zlocation;
     private double xExtent;
     private double yExtent;
     private double zExtent;
     
     public String getInfo(){
         return "wall not empty";
     }
     public void setLocation(Vector3f location){
         this.location = location;
     }
     public Vector3f getLocation(){
        return this.location;
     }
     public void setXYZLocations(){
        this.Xlocation = location.getX();
        this.Ylocation = location.getY();
        this.Zlocation = location.getZ();
     }
     public void setExtent(double x, double y, double z){
         this.xExtent = x;
         this.yExtent = y;
         this.zExtent = z;
     }
     
     public double getX(){
         return this.Xlocation;
     }
     public double getY(){
         return this.Ylocation;
     }
     public double getZ(){
         return this.Zlocation;
     }
     public double getXExtent(){
         return this.xExtent;
     }
     public double getYExtent(){
         return this.yExtent;
     }
     public double getZExtent(){
         return this.zExtent;
     }
}