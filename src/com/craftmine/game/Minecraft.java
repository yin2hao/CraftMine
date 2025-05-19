package com.craftmine.game;

import com.craftmine.engine.*;

public class Minecraft implements IAppLogic{
    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new MCWindows.MCWindowsOptions(),mc);//初始化窗户
        game.start();//循环开始
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(MCWindows windows, Scene scene, Render render) {
        float[] positions = new float[]{
                -0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
                0,1,3,3,1,2,
        };
        Mesh mesh = new Mesh(positions, colors, indices);
        scene.addMesh("quad", mesh);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {

    }

    @Override
    public void input(MCWindows windows, Scene scene, long diffTimeMillis) {

    }
}