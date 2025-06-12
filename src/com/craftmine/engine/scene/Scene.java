package com.craftmine.engine.scene;

import com.craftmine.engine.GUI.IGUIInstance;
import com.craftmine.engine.Model;
import com.craftmine.engine.Projection;
import com.craftmine.engine.TextureCache;
import com.craftmine.engine.camera.Camera;
import com.craftmine.engine.light.SceneLights;
import com.craftmine.engine.skybox.SkyBox;
import com.craftmine.game.Entity;
import java.util.*;

//主要用于初始化各个类
public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;//投影
    private TextureCache textureCache;//纹理
    private Camera camera;//摄像机
    private IGUIInstance guiInstance;//GUI
    private SceneLights sceneLights;//灯光
    private SkyBox skyBox;
    private Entity selectedEntity;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);//投影矩阵
        textureCache = new TextureCache();//背景默认颜色
        camera = new Camera();
    }

    public void addEntity(Entity entity) {
        String modelID = entity.getModelID();
        Model model = modelMap.get(modelID);
        if (model == null) {
            throw new RuntimeException("无法找到模型  [" + modelID + "]");
        }
        model.getEntitieList().add(entity);
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

    public SkyBox getSkyBox() {return skyBox;}
    public void setSkyBox(SkyBox skyBox) {this.skyBox = skyBox;}
    public void setGUIInstance(IGUIInstance guiInstance) {this.guiInstance = guiInstance;}
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
    public SceneLights getSceneLights(){
        return sceneLights;
    }
    public void setSceneLights(SceneLights sceneLights){
        this.sceneLights = sceneLights;
    }
    public void setSelectedEntity(Entity selectedEntity) {this.selectedEntity = selectedEntity;}
    public Entity getSelectedEntity() {return selectedEntity;}
}