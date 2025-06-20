package com.craftmine.game;

import com.craftmine.engine.*;
import com.craftmine.engine.GUI.IGUIInstance;
import com.craftmine.engine.MapGen.MapGrid;
import com.craftmine.engine.MapGen.MCMapGen;
import com.craftmine.engine.camera.Camera;
import com.craftmine.engine.light.SceneLights;
import com.craftmine.engine.mouseinput.MouseInput;
import com.craftmine.engine.scene.Scene;
import com.craftmine.engine.skybox.SkyBox;
import com.craftmine.engine.sound.*;
import com.craftmine.gameBlock.*;
import imgui.*;
import imgui.flag.ImGuiCond;
import org.joml.*;
import org.lwjgl.openal.AL11;

import java.lang.Math;

import static com.craftmine.game.GameResources.*;
import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements IAppLogic, IGUIInstance {

    private static final String SKYBOX_MODULE = GameResources.SKYBOX_MODULE;
    private static final String MINECRAFT_SOUND1 = GameResources.MINECRAFT_SOUND1;
    private static final String GRASS_MODEL_PATH = GameResources.GRASS_MODEL_PATH;
    private static final String STONE_MODEL_PATH = GameResources.STONE_MODEL_PATH;
    private static final String SAND_MODEL_PATH = GameResources.SAND_MODEL_PATH;
    private static final String LOG1_MODEL_PATH = GameResources.LOG1_MODEL_PATH;
    private static final String LOG2_MODEL_PATH = GameResources.LOG2_MODEL_PATH;
    private static final String PLANKS1_MODEL_PATH = GameResources.PLANKS1_MODEL_PATH;
    private static final String PLANKS2_MODEL_PATH = GameResources.PLANKS2_MODEL_PATH;

    private static final int SPEED = GameResources.SPEED; // 玩家移动速度倍率

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private SoundSource playerSoundSource;
    private SoundManager soundMgr;
    private MouseInput mouseInput;
    private MCMapGen mcMapGen;
    private MapGrid mapGrid;
    private MCPerson mcPerson;
    private Entity[][][] entityMap;
    private boolean isLeftButtonPressContinue = false;// 按下鼠标左键后是否继续按下
    private boolean isRightButtonPressContinue = false;
    private int placedBlock = 1;

    private Entity lastSelectedEntity = null;// 上一个选中的实体
    private static final long DESTROY_DELAY_MS = GameResources.DESTROY_DELAY_MS;

    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new GameResources.MCWindowsOptions(), mc);//初始化窗口
        game.start();//循环开始
    }

    @Override
    public void init(MCWindows window, Scene scene, Render render) {
        mouseInput = window.getMouseInput();

        loadModels(scene);

        mcMapGen = new MCMapGen(MAP_SIZE_X, MAP_SIZE_Y, MAP_SIZE_Z);//初始化地图数据（长宽高）
        mcMapGen.genMap();//生成方块
        mapGrid = mcMapGen.getGrid();

        entityMap = scene.addBlockMap(mapGrid.getBlockMap());

        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.2f);
        scene.setSceneLights(sceneLights);

        SkyBox skyBox = new SkyBox(SKYBOX_MODULE, scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(50);
        scene.setSkyBox(skyBox);

//        updateTerrain(scene);//动态加载地形，暂时无用

        // 初始化地图和玩家
        mcPerson = new MCPerson(mapGrid);

        Camera camera = scene.getCamera();
        camera.setPosition(10f, 75f, 10f);//设置相机位置
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));
        camera.setCollision(mcPerson, mapGrid);

        initSounds(camera);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {
        for (int x = 0; x < entityMap.length; x++) {
            for (int y = 0; y < entityMap[x].length; y++) {
                for (int z = 0; z < entityMap[x][y].length; z++) {
                    if (entityMap[x][y][z] != null) {
                        // 更新每个实体的模型矩阵
                        entityMap[x][y][z].updateModelMatrix();//更新所有实体的模型矩阵
                    }
                }
            }
        }
    }

    @Override
    public void input(MCWindows windows, Scene scene, long diffTimeMillis, boolean inputConsumed) {

        float move = diffTimeMillis * MOVEMENT_SPEED * SPEED ;
        Camera camera = scene.getCamera();
        if (windows.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (windows.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (windows.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (windows.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if (windows.isKeyPressed(GLFW_KEY_SPACE)) {
            camera.moveUp(move);
        } else if (windows.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            camera.moveDown(move);
        }

        if (windows.isKeyPressed(GLFW_KEY_1)) {
            placedBlock = 1;//草
        } else if (windows.isKeyPressed(GLFW_KEY_2)) {
            placedBlock = 2;//石头
        } else if (windows.isKeyPressed(GLFW_KEY_3)) {
            placedBlock = 3;//沙子
        } else if (windows.isKeyPressed(GLFW_KEY_4)) {
            placedBlock = 4;//原木1
        } else if (windows.isKeyPressed(GLFW_KEY_5)){
            placedBlock = 5;//原木2
        } else if (windows.isKeyPressed(GLFW_KEY_6)) {
            placedBlock = 6;//木板1
        } else if (windows.isKeyPressed(GLFW_KEY_7)) {
            placedBlock = 7;//木板2
        }

        MouseInput mouseInput = windows.getMouseInput();
        Entity selectedEntity = null;

        //左键破坏方块
        if (mouseInput.isLeftButtonPressed()) {
            selectedEntity = selectEntity(windows, scene, mouseInput.getCurrentPos());
            if (selectedEntity != null) {// 如果选中了实体
                if (!isLeftButtonPressContinue) mouseInput.setLeftButtonPressedTime();// 有间隔按下，设置初始时间
                scene.setSelectedEntity(selectedEntity);//更新被选中实体的渲染（改变颜色）
                isLeftButtonPressContinue = true; // 设置为按下状态
                // 如果是同一个实体
                if (selectedEntity == lastSelectedEntity) {
                    mouseInput.updateDurationTime();//更新按下时间
                    long pressDuration = mouseInput.getDurationTime();
                    if (pressDuration > DESTROY_DELAY_MS) {// 检查按下时间是否超过1秒
                        // 执行销毁方块操作
                        System.out.println("销毁方块: " + selectedEntity.getID());
                        //从地图中销毁方块
                        mapGrid.destoryBlock((int)selectedEntity.getPosition().x,
                                (int)selectedEntity.getPosition().y, (int)selectedEntity.getPosition().z);
                        entityMap[(int)selectedEntity.getPosition().x]
                                [(int)selectedEntity.getPosition().y]
                                [(int)selectedEntity.getPosition().z] = null; // 从实体地图中移除
                        scene.removeEntity(selectedEntity.getModelID(), selectedEntity);

                        lastSelectedEntity = null; // 重置选中实体
                        mouseInput.setDurationTime(0);
                    }
                } else {
                    //选中了新实体
                    mouseInput.setDurationTime(0);
                    lastSelectedEntity = selectedEntity;
                    isLeftButtonPressContinue = false;
                }
            } else {
                // 没有选中实体，取消选中状态
                scene.setSelectedEntity(null);
                mouseInput.setDurationTime(0);
                lastSelectedEntity = null;
                isLeftButtonPressContinue = false;
            }
        } else {
            // 鼠标释放，保持当前选中状态，但不执行销毁
            mouseInput.setDurationTime(0);
            scene.setSelectedEntity(null);
            lastSelectedEntity = null;
            isLeftButtonPressContinue = false;
        }

        // 右键放置方块（无CD版）
//        if (mouseInput.isRightButtonPressed()) {Add commentMore actions
//            Vector3i placementPos = getPlacementLocation(windows, scene, mouseInput.getCurrentPos());
//            MCBlock blockToPlace = null;
//            if (placementPos != null) {
//                int x = placementPos.x;
//                int y = placementPos.y;
//                int z = placementPos.z;
//                switch (placedBlock){
//                    case 1: blockToPlace = new MCGrassBlock(x, y, z); break;
//                    case 2: blockToPlace = new MCStoneBlock(x, y, z); break;
//                    case 3: blockToPlace = new MCSandBlock (x, y, z); break;
//                }
//                System.out.println("放置位置确定位置: " + x + ", " + y + ", " + z);
//                mapGrid.addBlock(x, y, z, blockToPlace);
//                entityMap[x][y][z] = scene.addBlock(x, y, z, blockToPlace);//将方块添加到实体地图中
//                Entity entity = scene.addBlock(x, y, z, blockToPlace);
//                scene.addEntity(entity.getModelID(), entity);//将方块添加到场景中
//            }
//        }

        //右键放置方块
        if (mouseInput.isRightButtonPressed()) {
            if (!isRightButtonPressContinue) {
                Vector3i placementPos = getPlacementLocation(windows, scene, mouseInput.getCurrentPos());

                if (placementPos != null) {
                    int x = placementPos.x;
                    int y = placementPos.y;
                    int z = placementPos.z;

                    if (mapGrid.getBlock(x, y, z) == null) {
                        MCBlock blockToPlace = null;
                        switch (placedBlock) {
                            case 1: blockToPlace = new MCGrassBlock(x, y, z);break;
                            case 2: blockToPlace = new MCStoneBlock(x, y, z);break;
                            case 3: blockToPlace = new MCSandBlock(x, y, z);break;
                            case 4: blockToPlace = new MCLogBlock1(x, y, z);break;
                            case 5: blockToPlace = new MCLogBlock2(x, y, z);break;
                            case 6: blockToPlace = new MCPlanksBlock1(x, y, z);break;
                            case 7: blockToPlace = new MCPlanksBlock2(x, y, z);break;
                            case 8: blockToPlace = new MCPlanksBlock3(x, y, z);break;
                            default:break;
                        }
                        if (blockToPlace != null) {
                            System.out.println("放置位置确定位置: " + x + ", " + y + ", " + z);
                            mapGrid.addBlock(x, y, z, blockToPlace);
                            entityMap[x][y][z] = scene.addBlock(x, y, z, blockToPlace);//将方块添加到实体地图中
                            Entity entity = scene.addBlock(x, y, z, blockToPlace);
                            scene.addEntity(entity.getModelID(), entity);//将方块添加到场景中
                        }
                    }
                }
                isRightButtonPressContinue = true; // 设置为按下状态
            }
        } else {
            isRightButtonPressContinue = false;
        }

        if (mouseInput.isInWindows()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(displVec.y * MOUSE_SENSITIVITY));
        }
        soundMgr.updateListenerPosition(camera);
    }

    @Override
    public void cleanup() {
        soundMgr.cleanup();
    }

    @Override
    public void drawGUI(){
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean handleGUIInput(Scene scene, MCWindows window){
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPos();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        imGuiIO.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }

    //在此进行地图加载优化
    //时间不足，当前类尚未完成
//    public void updateTerrain(Scene scene) {
//        int cellSize = 10;
//        Camera camera = scene.getCamera();
//        Vector3f cameraPos = camera.getPosition();
//        int cellCol = (int) (cameraPos.x / cellSize);
//        int cellRow = (int) (cameraPos.z / cellSize);
//
//        int numRows = NUM_CHUNKS * 2 + 1;
//        int numCols = numRows;
//        int zOffset = -NUM_CHUNKS;
//        float scale = cellSize / 2.0f;
//        for (int j = 0; j < numRows; j++) {
//            int xOffset = -NUM_CHUNKS;
//            for (int i = 0; i < numCols; i++) {
//                Entity entity = terrainEntities[j][i];
//                entity.setScale(scale);
//                entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
//                entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
//                xOffset++;
//            }
//            zOffset++;
//        }
//    }

    private void initSounds(Camera camera) {
        soundMgr = new SoundManager();
        soundMgr.setAttenuationModel(AL11.AL_NONE);//设置衰减模型：不衰减
        soundMgr.setListener(new SoundListener(camera.getPosition()));

        SoundBuffer buffer = new SoundBuffer(MINECRAFT_SOUND1);
        soundMgr.addSoundBuffer(buffer);
        playerSoundSource = new SoundSource(true, false);
        playerSoundSource.setPosition(camera.getPosition());
        playerSoundSource.setBuffer(buffer.getBufferId());
        soundMgr.addSoundSource("MineCraft", playerSoundSource);
        playerSoundSource.play();
    }

    private Vector3i getPlacementLocation(MCWindows windows, Scene scene, Vector2f Pos) {
        int wdwWidth = windows.getWidth();
        int wdwHeight = windows.getHeight();

        float centerX = wdwWidth / 2.0f;
        float centerY = wdwHeight / 2.0f;
        float mousex,mousey,mousez;

        if (!mouseInput.isESCPressed()){
            mousex = (2 * centerX) / wdwWidth - 1.0f;
            mousey = 1.0f - (2 * centerY) / wdwHeight;
            mousez = -1.0f;
        }else {
            mousex = (2 * Pos.x) / wdwWidth - 1.0f;
            mousey = 1.0f - (2 * Pos.y) / wdwHeight;
            mousez = -1.0f;
        }

        Matrix4f invProjMatrix = scene.getProjection().getInvProjMatrix();
        Vector4f mouseDir = new Vector4f(mousex, mousey, mousez, 1.0f);
        mouseDir.mul(invProjMatrix);
        mouseDir.z = -1.0f;
        mouseDir.w = 0.0f;

        Matrix4f invViewMatrix = scene.getCamera().getInvViewMatrix();
        mouseDir.mul(invViewMatrix);

        Vector4f min = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector4f max = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector2f nearFar = new Vector2f();

        Entity selectedEntity = null;
        float closestDistance = Float.POSITIVE_INFINITY;
        Vector3f center = scene.getCamera().getPosition();

        Vector4f selectedMin = new Vector4f();
        Vector4f selectedMax = new Vector4f();

        Matrix4f modelMatrix = new Matrix4f();
        for (int x = 0; x < entityMap.length; x++) {
            for (int y = 0; y < entityMap[x].length; y++) {
                for (int z = 0; z < entityMap[x][y].length; z++) {
                    Entity entity = entityMap[x][y][z];
                    if (entity == null) continue;

                    // 获取实体对应的模型
                    Model model = scene.getModelMap().get(entity.getModelID());
                    if (model == null) continue;

                    // 构建模型矩阵
                    modelMatrix.identity()
                            .translate(entity.getPosition())
                            .scale(entity.getScale());

                    // 遍历模型的所有材质和网格
                    for (Material material : model.getMaterialList()) {
                        for (Mesh mesh : material.getMeshList()) {
                            Vector3f aabbMin = mesh.getAabbMin();
                            min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
                            min.mul(modelMatrix);
                            Vector3f aabMax = mesh.getAabbMax();
                            max.set(aabMax.x, aabMax.y, aabMax.z, 1.0f);
                            max.mul(modelMatrix);

                            // 检测射线与AABB的碰撞
                            if (Intersectionf.intersectRayAab(
                                    center.x, center.y, center.z,
                                    mouseDir.x, mouseDir.y, mouseDir.z,
                                    min.x, min.y, min.z,
                                    max.x, max.y, max.z,
                                    nearFar) && nearFar.x < closestDistance) {
                                closestDistance = nearFar.x;
                                selectedEntity = entity;
                                selectedMin.set(min);
                                selectedMax.set(max);
                            }
                        }
                    }
                }
            }
        }

        if (selectedEntity == null) {
            return null;
        }

        // 计算交点
        Vector3f intersectionPoint = new Vector3f();
        center.add(new Vector3f(mouseDir.x, mouseDir.y, mouseDir.z).mul(closestDistance), intersectionPoint);

        // 确定被击中的面并计算放置位置
        Vector3f normal = new Vector3f();
        float epsilon = 0.01f;

        if (Math.abs(intersectionPoint.x - selectedMin.x) < epsilon) {
            normal.set(-1, 0, 0);
        } else if (Math.abs(intersectionPoint.x - selectedMax.x) < epsilon) {
            normal.set(1, 0, 0);
        } else if (Math.abs(intersectionPoint.y - selectedMin.y) < epsilon) {
            normal.set(0, -1, 0);
        } else if (Math.abs(intersectionPoint.y - selectedMax.y) < epsilon) {
            normal.set(0, 1, 0);
        } else if (Math.abs(intersectionPoint.z - selectedMin.z) < epsilon) {
            normal.set(0, 0, -1);
        } else if (Math.abs(intersectionPoint.z - selectedMax.z) < epsilon) {
            normal.set(0, 0, 1);
        }

        Vector3f selectedPos = selectedEntity.getPosition();
        Vector3f placementPosF = new Vector3f(selectedPos).add(normal);

        return new Vector3i((int) placementPosF.x, (int) placementPosF.y, (int) placementPosF.z);
    }

    // 选择方块
    private Entity selectEntity(MCWindows windows, Scene scene, Vector2f Pos) {
        int wdwWidth = windows.getWidth();
        int wdwHeight = windows.getHeight();

        float centerX = wdwWidth / 2.0f;
        float centerY = wdwHeight / 2.0f;
        float mousex,mousey,mousez;

        if (!mouseInput.isESCPressed()){
            mousex = (2 * centerX) / wdwWidth - 1.0f;
            mousey = 1.0f - (2 * centerY) / wdwHeight;
            mousez = -1.0f;
        }else {
            mousex = (2 * Pos.x) / wdwWidth - 1.0f;
            mousey = 1.0f - (2 * Pos.y) / wdwHeight;
            mousez = -1.0f;
        }

        Matrix4f invProjMatrix = scene.getProjection().getInvProjMatrix();
        Vector4f mouseDir = new Vector4f(mousex, mousey, mousez, 1.0f);
        mouseDir.mul(invProjMatrix);
        mouseDir.z = -1.0f;
        mouseDir.w = 0.0f;

        Matrix4f invViewMatrix = scene.getCamera().getInvViewMatrix();
        mouseDir.mul(invViewMatrix);

        Vector4f min = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector4f max = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector2f nearFar = new Vector2f();

        Entity selectedEntity = null;
        float closestDistance = Float.POSITIVE_INFINITY;
        Vector3f center = scene.getCamera().getPosition();

        Matrix4f modelMatrix = new Matrix4f();
        for (int x = 0; x < entityMap.length; x++) {
            for (int y = 0; y < entityMap[x].length; y++) {
                for (int z = 0; z < entityMap[x][y].length; z++) {
                    Entity entity = entityMap[x][y][z];
                    if (entity == null) continue;

                    // 获取实体对应的模型
                    Model model = scene.getModelMap().get(entity.getModelID());
                    if (model == null) continue;

                    // 构建模型矩阵
                    modelMatrix.identity()
                            .translate(entity.getPosition())
                            .scale(entity.getScale());

                    // 遍历模型的所有材质和网格
                    for (Material material : model.getMaterialList()) {
                        for (Mesh mesh : material.getMeshList()) {
                            Vector3f aabbMin = mesh.getAabbMin();
                            min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
                            min.mul(modelMatrix);
                            Vector3f aabMax = mesh.getAabbMax();
                            max.set(aabMax.x, aabMax.y, aabMax.z, 1.0f);
                            max.mul(modelMatrix);

                            // 检测射线与AABB的碰撞
                            if (Intersectionf.intersectRayAab(
                                    center.x, center.y, center.z,
                                    mouseDir.x, mouseDir.y, mouseDir.z,
                                    min.x, min.y, min.z,
                                    max.x, max.y, max.z,
                                    nearFar) && nearFar.x < closestDistance) {
                                closestDistance = nearFar.x;
                                selectedEntity = entity;
                            }
                        }
                    }
                }
            }
        }
        return selectedEntity;
    }

    public void loadModels(Scene scene) {
        //这是草方块的模型和纹理
        Model grassModel = ModelLoader.loadModel("grass", GRASS_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(grassModel);
        Model stoneModel = ModelLoader.loadModel("stone", STONE_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(stoneModel);
        Model sandModel = ModelLoader.loadModel("sand", SAND_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(sandModel);
        Model log1Model = ModelLoader.loadModel("log1", LOG1_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(log1Model);
        Model log2Model = ModelLoader.loadModel("log2", LOG2_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(log2Model);
        Model planks1Model = ModelLoader.loadModel("planks1", PLANKS1_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(planks1Model);
        Model planks2Model = ModelLoader.loadModel("planks2", PLANKS2_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(planks2Model);
        Model planks3Model = ModelLoader.loadModel("planks3", PLANKS3_MODEL_PATH,
                scene.getTextureCache());
        scene.addModel(planks3Model);
    }

    public static MCBlock loadBlock(char c, int x, int y, int z){
        switch(c){
            case 's' : return new MCSandBlock(x, y, z);
            case 't' : return new MCStoneBlock(x, y, z);
            case 'g' :
            default: return new MCGrassBlock(x, y, z);
        }
    }
}