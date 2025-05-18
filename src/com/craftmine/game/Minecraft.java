package com.craftmine.game;

import com.craftmine.engine.*;
import com.craftmine.game.IAppLogic;
import com.craftmine.game.MCWindows;

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
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
        };
        Mesh mesh = new Mesh(positions, 3);
        scene.addMesh("triangle", mesh);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {

    }

    @Override
    public void input(MCWindows windows, Scene scene, long diffTimeMillis) {

    }
}