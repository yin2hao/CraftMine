package com.craftmine.engine;

import org.lwjgl.opengl.GL;
import com.craftmine.game.MCWindows;

import static org.lwjgl.opengl.GL11.*;

public class Render {
    public Render() {
        GL.createCapabilities();
    }

    public void cleanup() {}

    public void render(MCWindows windows, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// 清除帧缓冲区
    }
}
