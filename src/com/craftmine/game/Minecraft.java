package com.craftmine.game;

import com.craftmine.engine.*;
import com.craftmine.engine.GUI.IGUIInstance;
import com.craftmine.engine.camera.Camera;
import com.craftmine.engine.light.SceneLights;
import com.craftmine.engine.mouseinput.MouseInput;
import com.craftmine.engine.scene.Scene;
import com.craftmine.engine.skybox.SkyBox;
import com.craftmine.engine.sound.*;
import imgui.*;
import imgui.flag.ImGuiCond;
import org.joml.*;
import org.lwjgl.openal.AL11;

import java.lang.Math;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements IAppLogic, IGUIInstance {

    private static final String SKYBOX_MODULE = GameResources.SKYBOX_MODULE;
    private static final String SKYBOX_QUAD = GameResources.SKYBOX_QUAD;
    private static final String MINECRAFT_SOUND1 = GameResources.MINECRAFT_SOUND1;
    private static final String CUBE_MODEL_PATH1 = GameResources.CUBE_MODEL_PATH1;

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;
    private Entity cubeEntity1, cubeEntity2;
    private SoundSource playerSoundSource;
    private SoundManager soundMgr;


    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new GameResources.MCWindowsOptions(), mc);//初始化窗口
        game.start();//循环开始
    }

    @Override
    public void init(MCWindows window, Scene scene, Render render) {
        String cubeModelID = "cube-model";
        Model cubeModel = ModelLoader.loadModel(cubeModelID, CUBE_MODEL_PATH1,
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cubeEntity1 = new Entity("cube-entity1", cubeModel.getID());
        cubeEntity2 = new Entity("cube-entity2", cubeModel.getID());
        cubeEntity1.setPosition(0, 0, -2);
        cubeEntity2.setPosition(0, 0, 0);
        scene.addEntity(cubeEntity1);
        scene.addEntity(cubeEntity2);

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

        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));
        initSounds(camera);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {
        double rotation = 1.5;
        cubeEntity1.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity1.updateModelMatrix();

        cubeEntity2.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity2.updateModelMatrix();
    }

    @Override
    public void input(MCWindows windows, Scene scene, long diffTimeMillis, boolean inputConsumed) {

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

    private void selectEntity(MCWindows windows, Scene scene, Vector2f mousePos) {
        int wdwWidth = windows.getWidth();
        int wdwHeight = windows.getHeight();

        float x = (2 * mousePos.x) / wdwWidth - 1.0f;
        float y = 1.0f - (2 * mousePos.y) / wdwHeight;
        float z = -1.0f;

        Matrix4f invProjMatrix = scene.getProjection().getInvProjMatrix();
        Vector4f mouseDir = new Vector4f(x, y, z, 1.0f);
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

        Collection<Model> models = scene.getModelMap().values();
        Matrix4f modelMatrix = new Matrix4f();
        for (Model model : models) {
            List<Entity> entities = model.getEntitieList();
            for (Entity entity : entities) {
                modelMatrix.translate(entity.getPosition()).scale(entity.getScale());
                for (Material material : model.getMaterialList()) {
                    for (Mesh mesh : material.getMeshList()) {
                        Vector3f aabbMin = mesh.getAabbMin();
                        min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
                        min.mul(modelMatrix);
                        Vector3f aabMax = mesh.getAabbMax();
                        max.set(aabMax.x, aabMax.y, aabMax.z, 1.0f);
                        max.mul(modelMatrix);
                        if (Intersectionf.intersectRayAab(center.x, center.y, center.z, mouseDir.x, mouseDir.y, mouseDir.z,
                                min.x, min.y, min.z, max.x, max.y, max.z, nearFar) && nearFar.x < closestDistance) {
                            closestDistance = nearFar.x;
                            selectedEntity = entity;
                            System.out.println("[DEBUG]实体选择" +  selectedEntity);
                        }
                    }
                }
                modelMatrix.identity();
            }
        }
        scene.setSelectedEntity(selectedEntity);
    }
}