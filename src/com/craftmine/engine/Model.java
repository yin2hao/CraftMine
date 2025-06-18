package com.craftmine.engine;

import com.craftmine.game.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String ID;
    private List<Material> materialsList;
    private List<Entity> entitieList; // 添加实体列表

    public Model(String ID, List<Material> materialsList) {
        this.ID = ID;
        this.materialsList = materialsList;
        this.entitieList = new ArrayList<>(); // 初始化实体列表
    }

    public void cleanup(){
        materialsList.forEach(Material::cleanup);
    }

    public String getID(){
        return ID;
    }

    public List<Material> getMaterialList(){
        return materialsList;
    }

    public List<Entity> getEntitieList() {
        return entitieList;
    }
}