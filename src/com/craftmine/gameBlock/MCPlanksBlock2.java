package com.craftmine.gameBlock;

public class MCPlanksBlock2 extends MCBlock{

    private String modelID = "planks2";

    public MCPlanksBlock2(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public String getModelID() {
        return modelID;
    }
}
