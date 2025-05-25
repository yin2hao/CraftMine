package com.craftmine.engine;

import com.craftmine.game.Entity;
import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;
    private TextureCache textureCache;
    private Camera camera;
    private IGUIInstance guiInstance;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);//投影矩阵
        textureCache = new TextureCache();
        camera = new Camera();
    }

    public void addEntity(Entity entity) {
        String modelID = entity.getModelID();
        Model model = modelMap.get(modelID);
        if (model == null) {
            throw new RuntimeException("无法找到模型  [" + modelID + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getID(), model);
    }

    public void cleanup(){
        modelMap.values().forEach(Model::cleanup);
    }

    //用以更新投影矩阵
    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }

    public void setGuiInstance(IGUIInstance guiInstance) {this.guiInstance = guiInstance;}
    public Map<String, Model> getModelMap(){
        return modelMap;
    }
    public IGUIInstance getGUIInstance(){return guiInstance;}
    public Projection getProjection(){
        return projection;
    }
    public TextureCache getTextureCache(){
        return textureCache;
    }
    public Camera getCamera(){return camera;}
}