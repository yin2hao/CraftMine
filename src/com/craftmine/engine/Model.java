package com.craftmine.engine;

import com.craftmine.game.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String ID;
    private List<Entity> entitiesList;
    private List<Material> materialsList;

    public Model(String ID, List<Material> materialsList) {
        this.ID = ID;
        entitiesList = new ArrayList<>();
        this.materialsList = materialsList;
    }

    public void cleanup(){
        materialsList.forEach(Material::cleanup);
    }

    public List<Entity> getEntitiesList(){
        return entitiesList;
    }

    public String getID(){
        return ID;
    }

    public List<Material> getMaterialsList(){
        return materialsList;
    }
}