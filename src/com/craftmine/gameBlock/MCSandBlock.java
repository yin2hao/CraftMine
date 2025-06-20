package com.craftmine.gameBlock;

public class MCSandBlock extends  MCBlock {

    private String modelID = "sand";

    public MCSandBlock(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public String getModelID() {
        return modelID;
    }
}
