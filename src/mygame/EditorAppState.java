/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicolagheza
 */
public class EditorAppState extends AbstractAppState {
    
    // Create normal app variables for ease of use
    private SimpleApplication app;
    private Node              rootNode;
    private Node              guiNode;
    private AssetManager      assetManager;
    private InputManager      inputManager;
    private FlyByCamera       flyCam;
    private Camera            cam;
    
    // Appstate specific fields
    private Hole hole;
    private Ball ball;
    private Tree tree;
    private Wall wall;
    private TerrainQuad terrain;
    private Material mat_terrain;
    private Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f);
    private DirectionalLight sun;
    public ArrayList<Collidables> obstaclesList;

    // Gui Stuff
    protected BitmapFont guiFont; 
    protected BitmapText hintText;
    protected BitmapText ch;
    
    private boolean editingHeightMap;
    private boolean raiseTerrain;
    private boolean lowerTerrain;

    private Geometry marker;
    private Geometry markerNormal;
    private Spatial ghost;
    
    private boolean placingWall;
    private boolean placingTree;
    private boolean placingBall;
    private boolean placingHole;
    private boolean place;
    private boolean rotate;
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.rootNode      = this.app.getRootNode();
        this.guiNode       = this.app.getGuiNode();
        this.assetManager  = this.app.getAssetManager();
        this.inputManager  = this.app.getInputManager();
        this.flyCam         = this.app.getFlyByCamera();
        this.cam            = this.app.getCamera();
        initMaterials();
        initKeys();
        setUpLight();
    }
    
    @Override
    public void update(float tpf) {
        if (editingHeightMap) {
            Vector3f intersection = getWorldIntersection();

            if (raiseTerrain) {
                if (intersection != null) {
                    adjustHeight(intersection, 64, tpf * 60, false);
                }
            } 
            else if (lowerTerrain){
                if (intersection != null) {
                    adjustHeight(intersection, 64, -tpf * 60, false);
                }
            }
                        
            if (terrain != null && intersection != null) {
                float h = terrain.getHeight(new Vector2f(intersection.x, intersection.z));
                Vector3f tl = terrain.getWorldTranslation();
                marker.setLocalTranslation(tl.add(new Vector3f(intersection.x, h, intersection.z)) );
                markerNormal.setLocalTranslation(tl.add(new Vector3f(intersection.x, h, intersection.z)) );

                Vector3f normal = terrain.getNormal(new Vector2f(intersection.x, intersection.z));
                ((Arrow)markerNormal.getMesh()).setArrowExtent(normal);
            }
        } 
        if (placingWall) {
            Vector3f intersection = getWorldIntersection();
            if (place) {
                placeWall(intersection);
                place=false;
            }
            if (terrain != null && intersection != null) {
                float h = terrain.getHeight(new Vector2f(intersection.x, intersection.z));
                Vector3f tl = terrain.getWorldTranslation();            
                ghost.setLocalTranslation(tl.add(new Vector3f(intersection.x, h, intersection.z)) );
            }
        }
        if (placingBall) {
            Vector3f intersection = getWorldIntersection();
            if (place) {
                placeBall(intersection);
                place = false;
            }
            if (terrain != null && intersection != null) {
                float h = terrain.getHeight(new Vector2f(intersection.x, intersection.z));
                Vector3f tl = terrain.getWorldTranslation();
                ghost.setLocalTranslation(tl.add(new Vector3f(intersection.x, h, intersection.z)));
            }
        }
        if (placingHole) {
            Vector3f intersection = getWorldIntersection();
            if (place) {
                placeHole(intersection);
                place = false;
            }            
            if (terrain != null && intersection != null) {
                float h = terrain.getHeight(new Vector2f(intersection.x, intersection.z));
                Vector3f tl = terrain.getWorldTranslation();
                ghost.setLocalTranslation(tl.add(new Vector3f(intersection.x, h, intersection.z)));
            }
        }
        if (placingTree) {
            Vector3f intersection = getWorldIntersection();
            if (place) {
                placeTree(intersection);
                place=false;
            }
            if (terrain != null && intersection != null) {
                float h = terrain.getHeight(new Vector2f(intersection.x, intersection.z));
                Vector3f tl = terrain.getWorldTranslation();            
                ghost.setLocalTranslation(tl.add(new Vector3f(intersection.x, h, intersection.z)) );
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        
         // Clear all mappings and listeners, detach all nodes and physics
        setEnabled(false);      
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        //Pause and unpause
        super.setEnabled(enabled);
        
        if(enabled) {       
            createTerrain(rootNode);
            rootNode.addLight(sun);
            obstaclesList = new ArrayList<Collidables>();
            resetCamera();        
        } else {
            rootNode.removeLight(sun);
            rootNode.detachAllChildren();
        }
    }
    
    private void resetCamera() {
        /*
        Quaternion q = new Quaternion();
        q.fromAngleAxis( FastMath.PI / 2 , new Vector3f(1,0,0) ); 
        cam.setLocation(new Vector3f(0f,285f,0f));
        cam.setRotation(q);
        */ 
        cam.setLocation(new Vector3f(0f, 96f, -64f));
        cam.lookAtDirection(new Vector3f(0f, -1f, 0f), Vector3f.UNIT_Y); 
    }
    
    private void setUpLight() {
        sun = new DirectionalLight();
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(1.7f)); 
    }
    
    /** Custom Keybinding: Map named actions to inputs. **/
    private void initKeys() {
        inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("Done", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Raise", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Place", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Lower", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        inputManager.addListener(actionListener, "Raise");
        inputManager.addListener(actionListener, "Click");
        inputManager.addListener(actionListener, "Lower");
        inputManager.addListener(actionListener,"Rotate");
        inputManager.addListener(actionListener,"Done");
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (editingHeightMap) {
                if (name.equals("Raise"))
                    raiseTerrain = keyPressed;
                else if (name.equals("Lower")) {
                    lowerTerrain = keyPressed;
                }
                else if (name.equals("Done") && !keyPressed) {
                    clearChangeHeightMap();
                }
            } 
            else if (placingWall) {
                if (name.equals("Click") && !keyPressed) {
                    place = true;
                }
                else if (name.equals("Rotate") && !keyPressed) {
                    ghost.rotate(0, FastMath.PI / 2, 0);
                    rotate = !rotate;
                }
                else if (name.equals("Done") && !keyPressed) {
                    //clearAddWall();
                    placingWall = false;
                    clearGhost();
                }
            }           
            else if (placingBall) {
                if (name.equals("Click") && !keyPressed) {
                    place = true;
                }
                else if (name.equals("Done") && !keyPressed) {
                    placingBall = false;
                    clearGhost();
                }
            }
            else if (placingTree) {
                if (name.equals("Click") && !keyPressed)
                    place = true;
                else if (name.equals("Rotate") && !keyPressed) {
                    ghost.rotate(0, FastMath.PI / 2, 0);
                    rotate = !rotate;
                }
                else if (name.equals("Done") && !keyPressed) {
                    clearGhost();
                }
            }
            else if (placingHole) {
                if (name.equals("Click") && !keyPressed) {
                    place = true;
                }
                else if (name.equals("Done") && !keyPressed) {
                    placingHole = false;
                    clearGhost();
                }
            }
        }
    };
      
    private void initMaterials() {
       mat_terrain = new Material(assetManager,
               "Common/MatDefs/Terrain/Terrain.j3md");
       mat_terrain.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap512.jpg"));
       Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
       grass.setWrap(Texture.WrapMode.Repeat);
       mat_terrain.setTexture("Tex1", grass);
       mat_terrain.setFloat("Tex1Scale", 64f);
    }
    
    private void createTerrain(Node rootNode) {
        float[] heightmap = new float[512*512];
        terrain = new TerrainQuad("terrain", 65, 513, heightmap); 
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0,-29f,0);
        terrain.setLocalScale(2f,1f,2f);
        terrain.setLocked(false); // unlock it so we can edit the height
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        TerrainLodControl lodControl =
        new TerrainLodControl(terrain, app.getCamera());
        terrain.addControl(lodControl);
        rootNode.attachChild(terrain);
    }
  
    public void addBall() {
        placingBall = true;
        createGhostView(assetManager.loadModel("Models/ball/ball.j3o"),"Press left mouse button to place a ball, P to save");
        resetCamera();   
    }
    
    public void placeBall(Vector3f intersection) {
        ball = new Ball("ball"+obstaclesList.size(),assetManager);
        rootNode.attachChild(ball.getSpatial());
        ball.getSpatial().setLocalTranslation(intersection);
        ball.setLocation(ball.getSpatial().getLocalTranslation());
        ball.setXYZLocations();
        obstaclesList.add(ball);
    }
         
    public void addWall() {
        placingWall = true;
        createGhostView(assetManager.loadModel("Models/wall/wall.j3o"),"Press left mouse button to place a wall, E to rotate it and P to save.");
        resetCamera();
    }

    public void placeWall(Vector3f intersection) {
        wall = new Wall("wall"+obstaclesList.size(),assetManager);
        rootNode.attachChild(wall.getSpatial());
        if (rotate) {
            System.out.println("setting rotation");
            wall.setRotation();
            wall.getSpatial().setLocalRotation(ghost.getLocalRotation());
        }
        wall.getSpatial().setLocalTranslation(intersection.x, terrain.getHeight(new Vector2f(intersection.x,intersection.z))+(terrain.getWorldTranslation().y), intersection.z);
        wall.setLocation(wall.getSpatial().getLocalTranslation());
        wall.setXYZLocations();
        obstaclesList.add(wall);
    }
    
    public void addTree() {
        placingTree = true;
        createGhostView(assetManager.loadModel("Models/TreeA/TreeA.j3o"), "Press left mouse button to place a tree, E to rotate it and P to go save.");
    }
    
    public void placeTree(Vector3f intersection) {
        tree = new Tree("tree"+obstaclesList.size(), assetManager);
        rootNode.attachChild(tree.getSpatial());
        if (rotate) {
            System.out.println("setting rotation");
            tree.setRotation();
            tree.getSpatial().setLocalRotation(ghost.getLocalRotation());
        }
        tree.getSpatial().setLocalTranslation(intersection.x, terrain.getHeight(new Vector2f(intersection.x,intersection.z))+(terrain.getWorldTranslation().y), intersection.z);
        tree.setLocation(tree.getSpatial().getLocalTranslation());
        tree.setXYZLocations();
        obstaclesList.add(tree);        
    }
    
    public void addHole() {
        placingHole = true;
        createGhostView(assetManager.loadModel("Models/Hole/Hole.j3o"),"Press left mouse button to place the hole, P to save");
        resetCamera();  
    }
    
    public void placeHole(Vector3f intersection) {
        hole = new Hole("hole",assetManager);
        adjustHeight(intersection.add(Vector3f.UNIT_XYZ), 2.5f, -100, true);
        rootNode.attachChild(hole.getSpatial());
        //terrain.setHeight(new Vector2f(intersection.x, intersection.z), -50);
        hole.getSpatial().setLocalTranslation(intersection.x, intersection.y, intersection.z);        
        hole.setLocation(hole.getSpatial().getLocalTranslation());
        hole.setXYZLocations();
        obstaclesList.add(hole);
    }

    public void changeHeightMap() {
        
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(false);
        loadHintText("Press left mouse button to raise terrain, press right mouse button to lower terrain. Press P to save.");
        initCrossHairs();
        createMarker();
        resetCamera();
        editingHeightMap = true;
   
    }
    
    private void clearChangeHeightMap() {
        editingHeightMap = false;
        flyCam.setEnabled(false);
        flyCam.setDragToRotate(true);
        clearHintText();
        clearCrossHairs();
        clearMarker(); 
        resetCamera();
    }
    
    protected void loadHintText(String hintString) {
        guiFont = assetManager.loadFont("Interface/Fonts/Arial.fnt");
        hintText = new BitmapText(guiFont, false);
        hintText.setLocalTranslation(0, app.getCamera().getHeight() - 30, 0); 
        hintText.setText(hintString);
        guiNode.attachChild(hintText);
    }
    
    protected void clearHintText() {
        guiNode.detachChild(hintText);
    }
    
    protected void initCrossHairs() {
        ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                app.getContext().getSettings().getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                app.getContext().getSettings().getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
    
    protected void clearCrossHairs() {
        guiNode.detachChild(ch);
    }
    
    protected void createGhostView(Spatial s, String hintText) {
        ghost = s;
        rootNode.attachChild(ghost);
        enableFlyCam();
        loadHintText("Press left mouse button to place the ball, P to exit");
        initCrossHairs();
    }
    
    protected void clearGhost() {
        flyCam.setEnabled(false);
        flyCam.setDragToRotate(true);
        clearHintText();
        clearCrossHairs();
        rootNode.detachChild(ghost);
        resetCamera();
    } 
    
    protected void createMarker() {
        // collision marker
        Sphere sphere = new Sphere(8, 8, 0.5f);
        marker = new Geometry("Marker");
        marker.setMesh(sphere);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(251f/255f, 130f/255f, 0f, 0.6f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        marker.setMaterial(mat);
        rootNode.attachChild(marker);
        
       
        // surface normal marker
        Arrow arrow = new Arrow(new Vector3f(0,1,0));
        markerNormal = new Geometry("MarkerNormal");
        markerNormal.setMesh(arrow);
        markerNormal.setMaterial(mat);
        rootNode.attachChild(markerNormal);
    }
    /** to fix **/ 
    protected void clearMarker() {
        rootNode.detachChild(marker);
        rootNode.detachChild(markerNormal);
    }
   
    /** Used to get the contact point to adjust the terrain heightmap **/
    private Vector3f getWorldIntersection() {
        Vector3f origin = cam.getWorldCoordinates(new Vector2f(app.getContext().getSettings().getWidth() / 2, app.getContext().getSettings().getHeight() / 2), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f(app.getContext().getSettings().getWidth() / 2, app.getContext().getSettings().getHeight() / 2), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        int numCollisions = terrain.collideWith(ray, results);
        if (numCollisions > 0) {
            CollisionResult hit = results.getClosestCollision();
            return hit.getContactPoint();
        }
        return null;
    }
    
    private void adjustHeight(Vector3f loc, float radius, float height, boolean hole) {

        // offset it by radius because in the loop we iterate through 2 radii
        int radiusStepsX = (int) (radius / terrain.getLocalScale().x);
        int radiusStepsZ = (int) (radius / terrain.getLocalScale().z);

        float xStepAmount = terrain.getLocalScale().x;
        float zStepAmount = terrain.getLocalScale().z;
        long start = System.currentTimeMillis();
        List<Vector2f> locs = new ArrayList<Vector2f>();
        List<Float> heights = new ArrayList<Float>();
        
        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                float locX = loc.x + (x * xStepAmount);
                float locZ = loc.z + (z * zStepAmount);

                if (isInRadius(locX - loc.x, locZ - loc.z, radius)) {
                    // see if it is in the radius of the tool
                    float h = calculateHeight(radius, height, locX - loc.x, locZ - loc.z);
                    locs.add(new Vector2f(locX, locZ));
                    heights.add(h);
                }
            }
        }
        if (!hole){
            terrain.adjustHeight(locs, heights);
            //System.out.println("Modified "+locs.size()+" points, took: " + (System.currentTimeMillis() - start)+" ms");
            terrain.updateModelBound();
        }
        else {
            for (int n = 0; n<heights.size(); n++){
                heights.set(n, height);
            }
            terrain.setHeight(locs, heights);
            //System.out.println("Modified "+locs.size()+" points, took: " + (System.currentTimeMillis() - start)+" ms");
            terrain.updateModelBound();
        }
    }

    private boolean isInRadius(float x, float y, float radius) {
        Vector2f point = new Vector2f(x, y);
        // return true if the distance is less than equal to the radius
        return point.length() <= radius;
    }

    private float calculateHeight(float radius, float heightFactor, float x, float z) {
        // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x, z);
        float val = point.length() / radius;
        val = 1 - val;
        if (val <= 0) {
            val = 0;
        }
        return heightFactor * val;
    }
       
    public void saveCourse() {
        int wallCount=0,
            treeCount=0,
            ballCount=0,
            holeCount=0;
        String userHome = System.getProperty("user.home");
        BinaryExporter exporter = BinaryExporter.getInstance();
        
        File file = new File(userHome+"/Models/"+"MyModel.j3o");
        File file2 = new File(userHome+"/Models/"+"ObstacleCount.txt");
        File file3 = new File(userHome+"/Models/"+"Rotations.txt");
        File file4 = new File(userHome+"/Models/"+"BallLocation.txt");
        File file5 = new File(userHome+"/Models/"+"WallLocation.txt");
        File file6 = new File(userHome+"/Models/"+"TreeLocation.txt");
        File file7 = new File(userHome+"/Models/"+"HoleLocation.txt");
        
        try {
            Writer wr = new FileWriter(file2);
            Writer wr2 = new FileWriter(file3);
            Writer wr3 = new FileWriter(file4);//balls
            Writer wr4 = new FileWriter(file5);//walls
            Writer wr5 = new FileWriter(file6);//trees
            Writer wr6 = new FileWriter(file7);//hole
            
            for (Collidables c: obstaclesList){
                if (c.getClass()== Wall.class) {
                    wallCount++;
                }else if(c.getClass() == Tree.class){
                    treeCount++;
                }else if(c.getClass() == Ball.class){
                    ballCount++;
                }else if(c.getClass() == Hole.class){
                    holeCount++;
                }
            }
            String wallString = Integer.toString(wallCount) + " " + Integer.toString(treeCount) 
                    + " " + Integer.toString(ballCount) + " " + Integer.toString(holeCount);
            System.out.println("Number of obstacles: " + wallString);
            
           String rotations = "";
            for (Collidables c: obstaclesList){
                if (c.getClass()==Wall.class) {
                    Wall w = (Wall)c;
                    rotations += w.getRotation() + " ";
                }
            }
            System.out.println("Rotations" + " " + rotations);
            
            String locations = "";
            for(Collidables x: obstaclesList){
                if (x.getClass() == Ball.class){
                        Ball b = (Ball) x;
                        locations = b.getSpatial().getLocalTranslation().toString();
                        wr3.write(locations);
                        wr3.write("/");
                } else if (x.getClass() == Wall.class){
                    locations = x.getLocation().toString();
                    wr4.write(locations);
                    wr4.write("/");
                } else if (x.getClass() == Tree.class){
                    locations = x.getLocation().toString();
                    wr5.write(locations);
                    wr5.write("/");
                } else if (x.getClass() == Hole.class){
                    Hole h = (Hole) x;
                    locations = h.getSpatial().getLocalTranslation().toString();
                    wr6.write(locations);
                }
            }
            
            wr.write(wallString);
            wr.close();
            wr2.write(rotations);
            wr2.close();
            
            wr3.close();
            wr4.close();
            wr5.close();
            wr6.close();
            exporter.save(rootNode, file);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error: Failed to save game!", ex);
        }
    }
  
    protected void enableFlyCam() {
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(false);
    }
    
}
