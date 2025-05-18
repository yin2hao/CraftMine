package com.craftmine.engine;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private Map<String, Mesh> meshMap;

    public Scene(){
        meshMap = new HashMap<>();
    }

    public void cleanup(){
        meshMap.values().forEach(Mesh::cleanup);
    }

    public Map<String, Mesh> getMeshMap(){
        return meshMap;
    }

    public void addMesh(String meshID, Mesh mesh){
        meshMap.put(meshID, mesh);
    }
}