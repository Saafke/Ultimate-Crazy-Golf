/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.IOException;


public class Hole extends Collidables{
    
	private float radius;
	private Spatial s;
        
        public Hole(java.lang.String name, AssetManager assetManager){
		s = assetManager.loadModel("Models/Hole/Hole.j3o");
                s.setName(name);
                radius = 5f;
	}
	public float getRadius(){
		return radius;
        }
        
        public void setRadius(float radius) {
            this.radius = radius;
        }
        public Spatial getSpatial(){
            return this.s;
        }
        public void setSpatial(Spatial s){
            this.s = s;
        }
}