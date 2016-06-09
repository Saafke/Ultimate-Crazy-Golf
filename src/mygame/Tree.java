/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

public class Tree extends Collidables {
        private Spatial s;
        boolean rotation = false;
    
        public Tree(String name, AssetManager assetManager){
            s = assetManager.loadModel("Models/TreeA/TreeA.j3o");
            s.setName(name);
            s.setLocalScale(20f,20f,20f);
            setExtent(4f, 20f, 4f);
	}
        
        public Tree(String name, boolean rotation,AssetManager assetManager){
            s = assetManager.loadModel("Models/TreeA/TreeA.j3o");
            s.setName(name);
            s.setLocalScale(20f,20f,20f);
            this.rotation= rotation;
            setExtent(4f, 20f, 4f);
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
