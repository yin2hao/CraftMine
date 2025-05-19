package com.craftmine.engine;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private Map<String, Mesh> meshMap;
    private Projection projection;

    public Scene(int width, int height) {
        meshMap = new HashMap<>();
        projection = new Projection(width, height);//投影矩阵
    }

    public void cleanup(){
        meshMap.values().forEach(Mesh::cleanup);
    }

    public Projection getProjection(){
        return projection;
    }

    //用以更新投影矩阵
    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }

    public Map<String, Mesh> getMeshMap(){
        return meshMap;
    }

    public void addMesh(String meshID, Mesh mesh){
        meshMap.put(meshID, mesh);
    }
}