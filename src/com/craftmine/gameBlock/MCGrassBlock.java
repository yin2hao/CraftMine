package com.craftmine.gameBlock;

import com.craftmine.game.Entity;

public class MCGrassBlock extends MCBlock {

    public MCGrassBlock(int x, int y, int z) {
        super(x, y, z);
        cubeEntity1 = new Entity("cube-entity1", cubeModel.getID());
        cubeEntity1.setPosition(this.x, this.y, this.z);
        scene.addEntity(cubeEntity1);
    }
}
