package com.craftmine.gameBlock;

public class MCGrassBlock extends MCBlock {

    private String modelID = "grass";

    public MCGrassBlock(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public String getModelID() {
        return modelID;
    }
}