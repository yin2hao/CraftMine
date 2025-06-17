package com.craftmine.gameBlock;

import com.craftmine.engine.Model;
import com.craftmine.engine.ModelLoader;
import com.craftmine.game.Entity;

import static com.craftmine.game.GameResources.CUBE_MODEL_PATH1;

public class MCBlock {
    // 方块的边长
    public static final double SIDE = 1.0;
    int x,y,z;

    public MCBlock(int x, int y, int z){
        this.x = x; this.y = y; this.z = z;
    }
}