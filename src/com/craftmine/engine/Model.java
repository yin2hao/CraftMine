package com.craftmine.engine;

import com.craftmine.game.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String ID;
    private List<Entity> entitiesList;
    private List<Mesh> meshList;

    public Model(String ID, List<Mesh> meshList) {
        this.ID = ID;
        this.meshList = meshList;
        entitiesList = new ArrayList<>();
    }

    public void cleanup(){
        meshList.forEach(Mesh::cleanup);
    }

    public List<Entity> getEntitiesList(){
        return entitiesList;
    }

    public String getID(){
        return ID;
    }

    public List<Mesh> getMeshList(){
        return meshList;
    }
}
