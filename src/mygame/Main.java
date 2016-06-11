package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.system.AppSettings;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    private GUIAppState     guiAppState;
    private EditorAppState  editorAppState;
    private PlayAppState    playAppState;
    private boolean         isStart = true;
    private FilterPostProcessor fpp;
    private CartoonEdgeFilter toon;
    
    public static void main(String[] args) {
        Main app = new Main();
        //Set some custom settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Crazy Golf 1.2");
        settings.setFrameRate(60);
        app.setSettings(settings);
        // Start the app
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Clear the debug display info
        setDisplayStatView(false); setDisplayFps(false);    
        // Set up all app states and attach them
        guiAppState = new GUIAppState(); // gui menu and hud
        stateManager.attach(guiAppState);
        
        playAppState = new PlayAppState();
        stateManager.attach(playAppState);
        
        editorAppState = new EditorAppState();
        stateManager.attach(editorAppState); // course editor
//        fpp = new FilterPostProcessor(assetManager);
//        viewPort.addProcessor(fpp);
//        toon = new CartoonEdgeFilter();
//        fpp.addFilter(toon);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Start the first cycle here so everything is initialized before enabling
        if(isStart) {
            // Start with the GUI
            guiAppState.setEnabled(true);
            
            // Indicate program is now started for the first time
            isStart = false;
        }
    }

}
