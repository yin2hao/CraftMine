package com.craftmine.engine;

import com.craftmine.engine.GUI.IGUIInstance;
import com.craftmine.game.IAppLogic;
import com.craftmine.game.MCWindows;


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
        });//窗口和openGL初始化
        targetFps = opts.fps;
        targetUps = opts.ups;
        this.appLogic = appLogic;
        render = new Render(windows);//这里绑定了函数指针，加载着色器并编译进程序中，加载统一变量
        scene = new Scene(windows.getWidth(), windows.getHeight());//包括投影矩阵
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
        int width = windows.getWidth();
        int height = windows.getHeight();
        scene.resize(width, height);
        render.resize(width, height);
    }

    private void run() {
        long initialTime = System.currentTimeMillis();
        float timeU = 1000.0f / targetUps;
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
        float deltaUpdate = 0;
        float deltaFps = 0;

        long updateTime = initialTime;
        IGUIInstance iguiInstance = scene.getGUIInstance();
        while (running && !windows.windowsShouldClose()) {
            windows.pollEvents();

            long now = System.currentTimeMillis();
            deltaUpdate += (now - initialTime) / timeU;
            deltaFps += (now - initialTime) / timeR;

            if (targetFps <= 0 || deltaFps >= 1) {
                windows.getMouseInput().input();
                boolean inputConsumed = iguiInstance != null && iguiInstance.handleGUIInput(scene, windows);
                appLogic.input(windows, scene, now - initialTime, inputConsumed);
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