package com.craftmine.gameBlock;

public class MCBlock {
    // 方块的边长
    public static final double SIDE = 1.0;
    public String modelID;
    int x,y,z;

    public MCBlock(int x, int y, int z){
        this.x = x; this.y = y; this.z = z;
    }
    public String getModelID() {
        return modelID;
    }
}