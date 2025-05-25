package com.craftmine.engine;

import com.craftmine.game.MCWindows;

public interface IGUIInstance {
    void drawGUI();

    boolean handleGUIInput(Scene scene, MCWindows window);
}
