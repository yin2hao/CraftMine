package com.craftmine.engine;

import com.craftmine.engine.GUI.GUIRender;
import com.craftmine.engine.skybox.SkyBoxRender;
import com.craftmine.game.MCWindows;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Render {

    private GUIRender guiRender;
    private SceneRender sceneRender;
    private SkyBoxRender skyBoxRender;

    public Render(MCWindows windows) {
        GL.createCapabilities();//加载当前OpenGL上下文中可用的函数指针
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        sceneRender = new SceneRender();
        guiRender = new GUIRender(windows);
        skyBoxRender = new SkyBoxRender();
    }

    public void cleanup(){
        sceneRender.cleanup();
        guiRender.cleanup();
    }

    public void render(MCWindows windows, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, windows.getWidth(), windows.getHeight());

        skyBoxRender.render(scene);
        sceneRender.render(scene);
        guiRender.render(scene);
    }

    public void resize(int width, int height) {
        guiRender.resize(width, height);
    }
}