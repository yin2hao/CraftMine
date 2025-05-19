package com.craftmine.engine;

import com.craftmine.game.Entity;
import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);//投影矩阵
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

    public Map<String, Model> getModelMap(){
        return modelMap;
    }

    public Projection getProjection(){
        return projection;
    }

    //用以更新投影矩阵
    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }

//    public void addMesh(String meshID, Mesh mesh){
//        modelMap.put(meshID, mesh);
//    }
}