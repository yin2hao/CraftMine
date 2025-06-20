package com.craftmine.engine.scene;

import com.craftmine.engine.GUI.IGUIInstance;
import com.craftmine.engine.Model;
import com.craftmine.engine.Projection;
import com.craftmine.engine.TextureCache;
import com.craftmine.engine.camera.Camera;
import com.craftmine.engine.light.SceneLights;
import com.craftmine.engine.skybox.SkyBox;
import com.craftmine.game.Entity;
import com.craftmine.gameBlock.MCBlock;

import java.util.*;

//主要用于初始化各个类
public class Scene {

    private Map<String, Model> modelMap;
    private MCBlock[][][] blockMap;
    private Projection projection;//投影
    private TextureCache textureCache;//纹理
    private Camera camera;//摄像机
    private IGUIInstance guiInstance;//GUI
    private SceneLights sceneLights;//灯光
    private SkyBox skyBox;
    private Entity[][][] entityMap;
    private Entity selectedEntity;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);//投影矩阵
        textureCache = new TextureCache();//背景默认颜色
        camera = new Camera();
    }

    public Entity[][][] addBlockMap(MCBlock[][][] blockMap) {
        this.blockMap = blockMap;
        entityMap = new Entity[blockMap.length][blockMap[0].length][blockMap[0][0].length];

        // 遍历方块数组中的所有方块
        for (int x = 0; x < blockMap.length; x++) {
            for (int y = 0; y < blockMap[x].length; y++) {
                for (int z = 0; z < blockMap[x][y].length; z++) {
                    MCBlock block = blockMap[x][y][z];
                    if (block != null) {
                        // 获取方块对应的模型ID
                        String modelID = blockMap[x][y][z].getModelID();

                        // 获取对应的模型
                        Model model = modelMap.get(modelID);
                        if (model == null) {
                            System.err.println("无法找到模型 [" + modelID + "]");
                            continue;
                        }

                        // 创建实体并设置位置
                        entityMap[x][y][z] = new Entity("block-" + x + "-" + y + "-" + z, modelID);
                        entityMap[x][y][z].setPosition(x, y, z);

                        // 将实体添加到模型中
                        try {
                            model.getEntitieList().add(entityMap[x][y][z]);
                        } catch (Exception e) {
                            System.err.println("添加实体到模型失败: " + e.getMessage());
                        }
                    }
                }
            }
        }
        System.out.println("地图方块加载完成，总共处理了 " + blockMap.length + "x" + blockMap[0].length + "x" + blockMap[0][0].length + " 个方块");
        return entityMap;
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

    public SkyBox getSkyBox() {
        return skyBox;
    }
    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }
    public void setGUIInstance(IGUIInstance guiInstance) {
        this.guiInstance = guiInstance;
    }
    public Map<String, Model> getModelMap(){
        return modelMap;
    }
    public IGUIInstance getGUIInstance(){
        return guiInstance;
    }
    public Projection getProjection(){
        return projection;
    }
    public TextureCache getTextureCache(){
        return textureCache;
    }
    public Camera getCamera(){
        return camera;
    }
    public SceneLights getSceneLights(){
        return sceneLights;}
    public void setSceneLights(SceneLights sceneLights){
        this.sceneLights = sceneLights;
    }
    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }
    public Entity getSelectedEntity() {
        return selectedEntity;
    }
}