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
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author nicolagheza
 */
public class GUIAppState extends AbstractAppState implements ScreenController{
    
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private AppStateManager   stateManager;
    private InputManager      inputManager;
    private ViewPort          guiViewPort;
    private AudioRenderer     audioRenderer;
    private FlyByCamera       flyCam;
    private Nifty             nifty;
    private NiftyJmeDisplay   disp;
    private Screen            screen;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.rootNode      = this.app.getRootNode();
        this.assetManager  = this.app.getAssetManager();
        this.stateManager  = this.app.getStateManager();
        this.inputManager  = this.app.getInputManager();
        this.guiViewPort   = this.app.getGuiViewPort();
        this.flyCam        = this.app.getFlyByCamera();
        disp = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        /** Create a new NiftyGUI object */
        nifty = disp.getNifty();
        /** Read your XML and initialize your custom ScreenController */
        nifty.fromXml("Interface/screen.xml", "start", this);
        
        
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        // Clear all mappings and listeners, detach all nodes etc.
        setEnabled(false);
        rootNode.detachAllChildren();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        //Pause and unpause
        super.setEnabled(enabled);
        if(enabled) {         
            guiViewPort.addProcessor(disp);
            flyCam.setEnabled(false);
        } else {
            guiViewPort.removeProcessor(disp);
            rootNode.detachAllChildren();
        }
    }
    
    public void startEditor(String nextScreen) {
        nifty.gotoScreen(nextScreen);
        stateManager.getState(EditorAppState.class).setEnabled(true);
    }
    
    public void startGameOnClickButton(String nextScreen) {
        nifty.gotoScreen(nextScreen);
        stateManager.getState(PlayAppState.class).setEnabled(true);
    }
    
    public void placeBallOnClickButton() {
        stateManager.getState(EditorAppState.class).addBall();
    }
    
    public void placeWallOnClickButton() {
        stateManager.getState(EditorAppState.class).addWall();
    }
    public void placeTreeOnClickButton() {
        stateManager.getState(EditorAppState.class).addTree();
    }
    
    public void placeHoleOnClickButton() {
        stateManager.getState(EditorAppState.class).addHole();
    }
    
    public void editTerrainOnClickButton() {
        stateManager.getState(EditorAppState.class).changeHeightMap();
    }
    
    public void saveOnClickButton() {
        stateManager.getState(EditorAppState.class).saveCourse();
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("bind( " + screen.getScreenId() + ")");
    }

    public void onStartScreen() {
        System.out.println("onStartScreen");
    }

    public void onEndScreen() {
        System.out.println("onEndScreen");
    }

    public void quit(){
        System.exit(0);
    }
    
}
