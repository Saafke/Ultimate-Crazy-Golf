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
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import com.jme3.scene.shape.Quad;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.util.SkyFactory;

/**
 *
 * @author nicolagheza
 */
public class PlayAppState extends AbstractAppState {

    // Create normal app variables for ease of use
    private SimpleApplication app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private FlyByCamera flyCam;
    private Camera cam;
    private ChaseCamera chaseCam;
    private PhysicsEngine physics;
    private boolean done = false;
    float yDirection = 0;
    float xDirection = -15;
    private Geometry gLine;
    private Geometry gPower;
    private Line line;
    private int degrees = 0;
    private double timeWhenClicked;
    private double timeWhenReleased;
    private float power = 0;
    private boolean shooted = false;
    private float shootIntensity;
    private Hole hole;
    // Appstate specific fields
    private Ball ball;
    private ArrayList<Ball> balls;
    private ArrayList<Collidables> obstaclesList;
    private TerrainQuad terrain;
    private AgentManager agentManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.guiNode = this.app.getGuiNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.flyCam = this.app.getFlyByCamera();
        this.cam = this.app.getCamera();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            balls = new ArrayList<Ball>();
            obstaclesList = new ArrayList<Collidables>();

            loadCourse();
            physics = new PhysicsEngine(balls, obstaclesList, terrain);

            flyCam.setEnabled(false);
        } else {
            rootNode.detachAllChildren();
        }
    }

    @Override
    public void update(float tpf) {
        if (done) {
            checkBallAndVector();
            physics.checkBallCollision();
            for (Ball b : balls) {
                //rotating the ball
                b.getBallControl().rotateBall();
                //FOR MOVING THE BALL
                physics.moveBall(b);
                physics.checkCollisions(b);
                physics.checkTerrainCollisions(b);

                if (hole != null) {
                    checkIfScored();
                    botTurn();
                    checkIfStill();
                }
            }
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        setEnabled(false);
    }

    public void addGrass() {
        for (int i = 1; i < 100; i++) {
            Spatial grass;
            float x = (float) ((Math.random() * 512) * (-0.5 + Math.random()));
            float z = (float) ((Math.random() * 512) * (-0.5 + Math.random()));
            float terrainHeight = terrain.getHeight(new Vector2f(x, z));
            grass = assetManager.loadModel("Models/Grass/Grass.j3o");
            rootNode.attachChild(grass);
            grass.rotate((float) (Math.random() * 360), 0f, 0f);
            grass.setLocalTranslation(x, terrainHeight - 29f, z);
        }
    }

    protected void loadCourse() {
        int counter1 = 0,
                counter2 = 0,
                counter3 = 0,
                counter4 = 0;
        String userHome = System.getProperty("user.home");
        assetManager.registerLocator(userHome, FileLocator.class);
        Node loadedNode = (Node) assetManager.loadModel("Models/MyModel.j3o");
        loadedNode.setName("Root node");

        File file = new File(userHome + "/Models/" + "ObstacleCount.txt");
        File file2 = new File(userHome + "/Models/" + "Rotations.txt");
        File file3 = new File(userHome + "/Models/" + "BallLocation.txt");
        File file4 = new File(userHome + "/Models/" + "WallLocation.txt");
        File file5 = new File(userHome + "/Models/" + "TreeLocation.txt");
        File file7 = new File(userHome + "/Models/" + "HoleLocation.txt");

        try {
            Scanner scanner = new Scanner(file);
            counter1 = scanner.nextInt();
            counter2 = scanner.nextInt();
            counter3 = scanner.nextInt();
            counter4 = scanner.nextInt();
            System.out.println("1:" + counter1 + "   2: " + counter2 + "   3: " + counter3 + "   hole: " + (counter4 > 0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Scanner scanner = new Scanner(file2);
            for (int i = 0; i < counter1; i++) {
                String s = scanner.next();
                Wall w = new Wall("wall" + counter1, s.equals("true") ? true : false, assetManager);
                obstaclesList.add(w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("after wall");

        for (int i = counter1; i < counter2 + counter1; i++) {
            Tree t = new Tree("tree" + (i - counter1), assetManager);
            obstaclesList.add(t);
        }

        System.out.println("after tree");

        try {
            Scanner scanner = new Scanner(file4).useDelimiter("/");
            for (int i = 0; i < counter1; i++) {
                String s = scanner.next();
                s = s.substring(1, s.length() - 1);
                Scanner scan = new Scanner(s).useDelimiter(",");
                float x = Float.parseFloat(scan.next());
                float y = Float.parseFloat(scan.next());
                float z = Float.parseFloat(scan.next());
                Vector3f temp = new Vector3f(x, y, z);
                obstaclesList.get(i).setLocation(temp);
                obstaclesList.get(i).setXYZLocations();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("after wall colides");

        try {
            Scanner scanner = new Scanner(file5).useDelimiter("/");
            for (int i = counter1; i < counter1 + counter2; i++) {
                String s = scanner.next();
                s = s.substring(1, s.length() - 1);
                Scanner scan = new Scanner(s).useDelimiter(",");
                float x = Float.parseFloat(scan.next());
                float y = Float.parseFloat(scan.next());
                float z = Float.parseFloat(scan.next());
                Vector3f temp = new Vector3f(x, y, z);
                obstaclesList.get(i).setLocation(temp);
                obstaclesList.get(i).setXYZLocations();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("after tree colides");

        for (int i = 0; i < counter3; i++) {
            ball = new Ball("ball" + i, assetManager);
            balls.add(ball);
        }

        System.out.println("after ball");

        try {
            Scanner scanner = new Scanner(file3).useDelimiter("/");
            for (int i = 0; i < counter3; i++) {
                String s = scanner.next();
                s = s.substring(1, s.length() - 1);
                Scanner scan = new Scanner(s).useDelimiter(",");
                float x = Float.parseFloat(scan.next());
                float y = Float.parseFloat(scan.next());
                float z = Float.parseFloat(scan.next());
                Vector3f temp = new Vector3f(x, y, z);
                balls.get(i).setLocation(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("after ball loc");

        hole = new Hole("hole", assetManager);

        try {
            Scanner scanner = new Scanner(file7);
            String s = scanner.nextLine();
            s = s.substring(1, s.length() - 1);
            Scanner scan = new Scanner(s).useDelimiter(",");
            float x = Float.parseFloat(scan.next());
            float y = Float.parseFloat(scan.next());
            float z = Float.parseFloat(scan.next());
            Vector3f temp = new Vector3f(x, y, z);
            hole.setLocation(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("after everything");

        terrain = (TerrainQuad) loadedNode.getChild("terrain");
        
        agentManager = new AgentManager();
        
        hole.setSpatial(loadedNode.getChild("hole"));
        hole.setXYZLocations();

        rootNode.attachChild(loadedNode);
        
        for (int i = 0; i < counter3; i++) {
            balls.get(i).setSpatial(loadedNode.getChild("ball" + i));
            balls.get(i).setXYZLocations();
            agentManager.add(new BotAgent(balls.get(i),hole,physics));
        }
        
        ball = agentManager.getCurrentAgent().getBall();

        chaseCam = new ChaseCamera(cam, ball.getSpatial());
        chaseCam.setDefaultDistance(50f);

        done = true;

        addGrass();
        makeVector();
        makePowerIndicator();
        initKeys();

        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky.dds", false);
        rootNode.attachChild(sky);
        System.out.println(done);
    }

    private void makeVector() {

        Vector3f ballLocationn = ball.getSpatial().getLocalTranslation();
        line = new Line(ballLocationn, new Vector3f(ballLocationn.getX() + xDirection, ballLocationn.getY(), ballLocationn.getZ() + yDirection));
        line.setLineWidth(4f);
        gLine = new Geometry("Bullet", line);
        Material orange = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        orange.setColor("Color", ColorRGBA.Orange);
        gLine.setMaterial(orange);
        rootNode.attachChild(gLine);

    }

    private void makePowerIndicator() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);

        gPower = new Geometry("gPower", new Quad(12, power));
        gPower.setMaterial(mat);
        guiNode.attachChild(gPower);
        guiNode.getChild("gPower").setLocalTranslation(34, 61, 0);

    }

    private void updatePowerIndicator(float power) {

        guiNode.detachChildNamed("gPower");
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);

        gPower = new Geometry("gPower", new Quad(12, power));
        gPower.setMaterial(mat);
        guiNode.attachChild(gPower);
        guiNode.getChild("gPower").setLocalTranslation(34, 61, 0);

    }

    //==================KEY INPUT============================STUFF==================================================
    private void initKeys() {
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Shoot");
        inputManager.addMapping("Power", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(analogListener, "Power");
        inputManager.addMapping("RotateCameraLeft", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addListener(analogListener, "RotateCameraLeft");
        inputManager.addMapping("RotateCameraRight", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addListener(analogListener, "RotateCameraRight");
    }
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("RotateCameraLeft")) {
                //rotate one degree left
                chaseCam.setDefaultHorizontalRotation(chaseCam.getHorizontalRotation() + 0.01736f);
                //change x and y direction
                degrees++;
                double rad = (double) Math.toRadians(degrees);
                xDirection = -(float) Math.cos(rad) * 15;
                yDirection = -(float) Math.sin(rad) * 15;
                //System.out.println("x;" + xDirection + "  y ;" + yDirection);
            }
            if (name.equals("RotateCameraRight")) {
                //rotate one degree right
                chaseCam.setDefaultHorizontalRotation(chaseCam.getHorizontalRotation() - 0.01736f);
                //change x and y direction
                degrees--;
                double rad = (double) Math.toRadians(degrees);
                xDirection = -(float) Math.cos(rad) * 15;
                yDirection = -(float) Math.sin(rad) * 15;
                //System.out.println("rad"+ rad + "mofodegrees:" + degrees);
            }
            if (name.equals("Power")) {
                power = power + 0.7f;
                if (power < 102) {
                    updatePowerIndicator(power);
                } else {
                    updatePowerIndicator(102f);
                }
            }
        }
    };
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Shoot") && ball != null && agentManager.getCurrentAgent().getClass().equals(PlayerAgent.class) && keyPressed) {
                timeWhenClicked = System.currentTimeMillis();
            }

            if (name.equals("Shoot") && ball != null && agentManager.getCurrentAgent().getClass().equals(PlayerAgent.class) &&!keyPressed) {
                timeWhenReleased = System.currentTimeMillis();
                shootIntensity = (float) (timeWhenReleased - timeWhenClicked) / 800;

                Agent curAgent = agentManager.getCurrentAgent();
                ((PlayerAgent)curAgent).performShot(shootIntensity, xDirection, yDirection);

                guiNode.detachChildNamed("gPower");
                power = 0;
                shooted = true;
            }
        }
    };

    public void checkBallAndVector() {
        if (ball != null && done == true) {
            //FOR DRAWING THE VECTOR
            if (!ball.getBallControl().isMoving()) {//if ball is not moving: draw the line
                //System.out.println("DX;" + xDirection + "     DY; " +yDirection );
                //vector to show where ball is going!
                Vector3f ballLocationn = ball.getSpatial().getLocalTranslation();
                line.updatePoints(ballLocationn, new Vector3f(ballLocationn.getX() + xDirection, ballLocationn.getY() + 0, ballLocationn.getZ() + yDirection));
            } else {//'delete' line
                line.updatePoints(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
            }
        }
    }

    public void checkIfScored() {

        float x = ball.getSpatial().getLocalTranslation().getX();
        float y = ball.getSpatial().getLocalTranslation().getY();
        float z = ball.getSpatial().getLocalTranslation().getZ();

        float hx = hole.getLocation().getX();
        float hy = hole.getLocation().getY();
        float hz = hole.getLocation().getZ();

        if (x - ball.getXExtent() > hole.getX() - hole.getRadius() && x + ball.getXExtent() < hx + hole.getRadius()
                && z - ball.getZExtent() > hz - hole.getRadius() && z + ball.getZExtent() < hz + hole.getRadius()
                && y < hy - 20
                && ball.getBallControl().getSpeed() < 1.5f) {
            
            ball.getBallControl().setxVelocity(ball.getBallControl().getxVelocity() * 0f);
            ball.getBallControl().setyVelocity(ball.getBallControl().getyVelocity() * 0f);
            ball.getBallControl().setzVelocity(ball.getBallControl().getzVelocity() * 0f);
            System.out.println("stop the ball!" );
        }
    }
    
    public void changeCamera(Ball ball) {
        chaseCam.setSpatial(ball.getSpatial());
    }
    
    public void botTurn() {
        if (!shooted && agentManager.getCurrentAgent().getClass().equals(BotAgent.class)) {
            ((BotAgent)agentManager.getCurrentAgent()).computeShot();
            shooted = true;
        }
    }
    
    public void checkIfStill() {
        if (shooted && !ball.getBallControl().isMoving()) {
                shooted = false;
                ball = agentManager.nextAgent().getBall();
                changeCamera(ball);
        }
    }
}