package com.craftmine.engine;

import com.craftmine.game.IAppLogic;
import com.craftmine.game.MCWindows;

import javax.swing.*;

public class Engine {
    public static final int TARGET_UPS = 30;
    private final IAppLogic appLogic;
    private MCWindows windows;
    private Render render;
    private Scene scene;
    private boolean running;
    private int targetFps;
    private int targetUps;

    public Engine(String windowsTitle, MCWindows.MCWindowsOptions opts, IAppLogic appLogic) {
        windows = new MCWindows(windowsTitle, opts, () -> {
            resize();
            return null;
        });
        targetFps = opts.fps;
        targetUps = opts.ups;
        this.appLogic = appLogic;
        render = new Render();
        scene = new Scene();
        appLogic.init(windows, scene, render);
        running = true;
    }

    private void cleanup() {
        appLogic.cleanup();
        render.cleanup();
        scene.cleanup();
        windows.cleanup();
    }

    private void resize() {
    }

    private void run() {
        long initialTime = System.currentTimeMillis();
        float timeU = 1000.0f / targetUps;
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
        float deltaUpdate = 0;
        float deltaFps = 0;

        long updateTime = initialTime;
        while (running && !windows.windowsShouldClose()) {
            windows.pollEvents();

            long now = System.currentTimeMillis();
            deltaUpdate += (now - initialTime) / timeU;
            deltaFps += (now - initialTime) / timeR;
            if (targetFps <= 0 || deltaFps >= 1) {
                appLogic.input(windows, scene, now - initialTime);
            }

            if (deltaUpdate >= 1) {
                long diffTimeMillis = now - updateTime;
                appLogic.update(windows, scene, diffTimeMillis);
                updateTime = now;
                deltaUpdate--;
            }

            if (targetFps <= 0 || deltaFps >= 1) {
                render.render(windows, scene);
                deltaFps--;
                windows.update();
            }
            initialTime = now;
        }

        cleanup();
    }

    public void start() {
        running = true;
        run();
    }

    public void stop() {
        running = false;
    }
}