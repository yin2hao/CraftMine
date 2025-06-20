package com.craftmine.gameBlock;

public class MCPlanksBlock3 extends MCBlock{

    private String modelID = "gplanks3";

    public MCPlanksBlock3(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public String getModelID() {
        return modelID;
    }
}
