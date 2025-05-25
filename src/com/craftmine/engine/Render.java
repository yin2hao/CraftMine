package com.craftmine.engine;

import com.craftmine.game.MCWindows;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Render {

    private SceneRender sceneRender;
    public Render() {
        GL.createCapabilities();//加载当前OpenGL上下文中可用的函数指针
        glEnable(GL_DEPTH_TEST);
        sceneRender = new SceneRender();
    }

    public void cleanup(){
        sceneRender.cleanup();
    }

    public void render(MCWindows windows, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glViewport(0, 0, windows.getWidth(), windows.getHeight());
        sceneRender.render(scene);
    }
}