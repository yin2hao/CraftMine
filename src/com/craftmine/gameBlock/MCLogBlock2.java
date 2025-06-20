package com.craftmine.gameBlock;

public class MCLogBlock2 extends  MCBlock {

    private String modelID = "log2";

    public MCLogBlock2(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public String getModelID() {
        return modelID;
    }
}
