package com.craftmine.game;

import com.craftmine.engine.*;
import com.craftmine.game.IAppLogic;
import com.craftmine.game.MCWindows;

public class Minecraft implements IAppLogic{
    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new MCWindows.MCWindowsOptions(),mc);
        game.start();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(MCWindows windows, Scene scene, Render render) {

    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {

    }

    @Override
    public void input(MCWindows windows, Scene scene, long diffTimeMillis) {

    }
}