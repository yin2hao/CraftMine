package com.craftmine.engine.GUI;

import com.craftmine.engine.scene.Scene;
import com.craftmine.game.MCWindows;

public interface IGUIInstance {
    void drawGUI();

    boolean handleGUIInput(Scene scene, MCWindows window);
}
