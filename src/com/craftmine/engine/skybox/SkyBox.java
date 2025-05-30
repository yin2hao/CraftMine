package com.craftmine.engine.skybox;

import com.craftmine.engine.Model;
import com.craftmine.engine.ModelLoader;
import com.craftmine.engine.TextureCache;
import com.craftmine.game.*;

public class SkyBox {

    private Entity skyBoxEntity;
    private Model skyBoxModel;

    public SkyBox(String skyBoxModelPath, TextureCache textureCache) {
        skyBoxModel = ModelLoader.loadModel("skybox-model", skyBoxModelPath, textureCache);
        skyBoxEntity = new Entity("skyBoxEntity-entity", skyBoxModel.getID());
    }

    public Entity getSkyBoxEntity() {
        return skyBoxEntity;
    }
    public Model getSkyBoxModel() {
        return skyBoxModel;
    }
}