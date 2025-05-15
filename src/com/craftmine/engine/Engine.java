package com.craftmine.engine;

import com.craftmine.game.MCWindows;
import com.craftmine.game.iAppLogic;

public class Engine {
    private final iAppLogic applogic;
    private final MCWindows MCwindows;
    private Render render;
    private Scene scene;

    private static final int TARGET_UPS = 30;
    private boolean running;
    private int targetFps;
    private int targetUps;

    public Engine(String title ,MCWindows.MCWindowsOptions opts, iAppLogic applogic) {
        MCwindows = new MCWindows(title,opts,()-> {
                resize();
                return null;});
        targetFps = opts.fps;
        targetUps = opts.ups;
        this.applogic = applogic;
        render = new Render();
        scene = new Scene();
        applogic.init(MCwindows,scene,,render);
        running = true;
    }

    private void cleanup(){
        applogic.cleanup();
        render.cleanup();
        scene.cleanup();
        MCWindows.cleanup();
    }

    private void resize(){}
}