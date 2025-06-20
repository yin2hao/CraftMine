package com.craftmine.gameBlock;

public class MCPlanksBlock1 extends MCBlock {

    private String modelID = "planks1";

    public MCPlanksBlock1(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public String getModelID() {
        return modelID;
    }
}
