/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

public class Wall extends Collidables {
        private Spatial s;
        boolean rotation = false;
    
        public Wall(String name, AssetManager assetManager){
            s = assetManager.loadModel("Models/wall/wall.j3o");
            s.setName(name);
            setExtent(0.5, 0.7, 14);
	}
        
        public Wall(String name, boolean rotation,AssetManager assetManager){
            s = assetManager.loadModel("Models/wall/wall.j3o");
            s.setName(name);
            this.rotation= rotation;
            setExtent(0.5, 0.7, 14);
	}
        
        public void setRotation(){
            if(rotation == false){
                rotation = true;
            }else rotation = false;
        }
        public boolean getRotation(){
            return rotation;
        }
        
        public Spatial getSpatial() { return s; }
}