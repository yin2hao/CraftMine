package com.craftmine.gameBlock;

public class MCStoneBlock extends  MCBlock {

    private static String modelID = "stone";

    public MCStoneBlock(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public  String getModelID() {
        return modelID;
    }
}
