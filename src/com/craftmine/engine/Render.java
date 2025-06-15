package com.craftmine.engine;

import com.craftmine.engine.GUI.GUIRender;
import com.craftmine.engine.scene.Scene;
import com.craftmine.engine.scene.SceneRender;
import com.craftmine.engine.skybox.SkyBoxRender;
import com.craftmine.game.MCWindows;
import com.craftmine.game.GameResources;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Render {

    private GUIRender guiRender;
    private SceneRender sceneRender;
    private SkyBoxRender skyBoxRender;

    public Render(MCWindows windows) {
        GameResources.bugCheck();
        GL.createCapabilities();//加载当前OpenGL上下文中可用的函数指针
        //此处天空盒与深度测试疑似冲突
//        glEnable(GL_DEPTH_TEST);//深度测试，用于渲染前后关系
        glEnable(GL_CULL_FACE);//启用面剔除
        glCullFace(GL_BACK);//指定剔除背对相机的面
        sceneRender = new SceneRender();//着色器创建
        guiRender = new GUIRender(windows);//GUI初始化，GUI键盘回调
        skyBoxRender = new SkyBoxRender();//天空盒初始化
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