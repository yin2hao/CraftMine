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
import java.util.Collection;
import java.util.List;

import static com.craftmine.game.GameResources.*;
import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements IAppLogic, IGUIInstance {

    private static final String SKYBOX_MODULE = GameResources.SKYBOX_MODULE;
    private static final String MINECRAFT_SOUND1 = GameResources.MINECRAFT_SOUND1;
    private static final String CUBE_MODEL_PATH1 = GameResources.CUBE_MODEL_PATH1;

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;
    private SoundSource playerSoundSource;
    private SoundManager soundMgr;
    private MouseInput mouseInput;
    private Scene scene;
    private MCMapGen mcMapGen;
    private MapGrid mapGrid;
    private MCPerson mcPerson;
    private Entity[][][] entityMap;

    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new GameResources.MCWindowsOptions(), mc);//初始化窗口
        game.start();//循环开始
    }

    @Override
    public void init(MCWindows window, Scene scene, Render render) {
        mouseInput = window.getMouseInput();

        //这是草方块的模型和纹理
        Model cubeModel = ModelLoader.loadModel("Grass_model", CUBE_MODEL_PATH1,
                scene.getTextureCache());
        scene.addModel(cubeModel);


        mcMapGen = new MCMapGen(MAP_SIZE_X, MAP_SIZE_Y, MAP_SIZE_Z);//初始化地图数据（长宽高）
        mcMapGen.genMap();//生成方块
        mapGrid = mcMapGen.getGrid();

        entityMap = scene.addBlockMap(mapGrid.getBlockMap());
//        cubeEntity1 = new Entity("cube-entity1", cubeModel.getID());
//        cubeEntity1.setPosition(0, 0, 1);
//        scene.addEntity(cubeEntity1);

//        String quadModelId = "quad-model";
//        Model quadModel = ModelLoader.loadModel(quadModelId, SKYBOX_QUAD,
//                scene.getTextureCache());//返回一个model
//        scene.addModel(quadModel);
//
//        int numRows = NUM_CHUNKS * 2 + 1;//计算行数
//        int numCols = numRows;//计算列数
//        terrainEntities = new Entity[numRows][numCols];
//        for (int j = 0; j < numRows; j++) {
//            for (int i = 0; i < numCols; i++) {
//                Entity entity = new Entity("TERRAIN_" + j + "_" + i, quadModelId);
//                terrainEntities[j][i] = entity;
//                scene.addEntity(entity);
//            }
//        }

        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.2f);
        scene.setSceneLights(sceneLights);

        SkyBox skyBox = new SkyBox(SKYBOX_MODULE, scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(50);
        scene.setSkyBox(skyBox);

        updateTerrain(scene);//动态加载地形，暂时无用

        // 初始化地图和玩家
        mcPerson = new MCPerson(mapGrid);

        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 30.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));
        camera.setCollision(mcPerson, mapGrid);

        initSounds(camera);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {
//        double rotation = 1.5;
//        cubeEntity1.setRotation(1, 1, 1, (int) Math.toRadians(rotation));
//        cubeEntity1.updateModelMatrix();
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

        long currentWindowHandle = glfwGetCurrentContext();
        float move = diffTimeMillis * MOVEMENT_SPEED;
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

        MouseInput mouseInput = windows.getMouseInput();
        if (mouseInput.isLeftButtonPressed()) {
            selectEntity(windows, scene, mouseInput.getCurrentPos());
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

    public void updateTerrain(Scene scene) {
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
    }

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


    //因为地形存储原因，实体列表由原本通过List<Entity>进行存储变为使用BlockMap[][][]进行存储
    //因此，改方法需要大改
    private void selectEntity(MCWindows windows, Scene scene, Vector2f Pos) {
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
                                System.out.println("[DEBUG]选中实体: " + selectedEntity.getID() + " 在位置 [" + x + "," + y + "," + z + "]");
                            }
                        }
                    }
                }
            }
        }

        scene.setSelectedEntity(selectedEntity);
    }

    public static MCBlock loadBlock(char c, int x, int y, int z){
        switch(c){
            case 'g' :
            default:    return new MCGrassBlock(x, y, z);
//            case 'd' : return new MCDirtBlock(x, y, z);
//            case 'w' : return new MCWaterBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
//            case 'o' : return new MCWoodBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
//            case 'l' : return new MCLeavesBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
//            case 's' : return new MCSandBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
//            case 't' : return new MCStoneBlock(x, y, z);
        }
//        return null;
    }
}