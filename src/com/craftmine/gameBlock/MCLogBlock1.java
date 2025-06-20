package com.craftmine.gameBlock;

public class MCLogBlock1 extends MCBlock {

    private String modelID = "log1";

    public MCLogBlock1(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public String getModelID() {
        return modelID;
    }
}
