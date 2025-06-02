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
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements IAppLogic, IGUIInstance {

    private static final String SKYBOX_MODULE = gameResources.SKYBOX_MODULE;
    private static final String SKYBOX_QUAD = gameResources.SKYBOX_QUAD;
    private static final String MINECRAFT_SOUND1 = gameResources.MINECRAFT_SOUND1;
    private static final String CUBE_MODEL_PATH1 = gameResources.CUBE_MODEL_PATH1;

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;
    private Entity cubeEntity;
    private SoundSource playerSoundSource;
    private SoundManager soundMgr;


    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new MCWindows.MCWindowsOptions(),mc);//初始化窗口
        game.start();//循环开始
    }

    @Override
    public void init(MCWindows window, Scene scene, Render render) {
        String cubeModelID = "cube-model";
        Model cubeModel = ModelLoader.loadModel(cubeModelID, CUBE_MODEL_PATH1,
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getID());
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);

        String quadModelId = "quad-model";
        Model quadModel = ModelLoader.loadModel(quadModelId, SKYBOX_QUAD,
                scene.getTextureCache());//返回一个model
        scene.addModel(quadModel);

        int numRows = NUM_CHUNKS * 2 + 1;//计算行数
        int numCols = numRows;//计算列数
        terrainEntities = new Entity[numRows][numCols];
        for (int j = 0; j < numRows; j++) {
            for (int i = 0; i < numCols; i++) {
                Entity entity = new Entity("TERRAIN_" + j + "_" + i, quadModelId);
                terrainEntities[j][i] = entity;
                scene.addEntity(entity);
            }
        }

        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.2f);
        scene.setSceneLights(sceneLights);

        SkyBox skyBox = new SkyBox(SKYBOX_MODULE, scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(50);
        scene.setSkyBox(skyBox);

        scene.getCamera().moveUp(0.1f);

        updateTerrain(scene);

        Camera camera = scene.getCamera();
//        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.setPosition(0f, 0f, 0f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));
        initSounds(camera);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {
        updateTerrain(scene);
    }

    @Override
    public void input(MCWindows windows, Scene scene, long diffTimeMillis, boolean inputConsumd) {

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
        if (mouseInput.isRightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
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
        int cellSize = 10;
        Camera camera = scene.getCamera();
        Vector3f cameraPos = camera.getPosition();
        int cellCol = (int) (cameraPos.x / cellSize);
        int cellRow = (int) (cameraPos.z / cellSize);

        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        int zOffset = -NUM_CHUNKS;
        float scale = cellSize / 2.0f;
        for (int j = 0; j < numRows; j++) {
            int xOffset = -NUM_CHUNKS;
            for (int i = 0; i < numCols; i++) {
                Entity entity = terrainEntities[j][i];
                entity.setScale(scale);
                entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
                entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
                xOffset++;
            }
            zOffset++;
        }
    }

    private void initSounds(Camera camera) {
        soundMgr = new SoundManager();
        soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        soundMgr.setListener(new SoundListener(camera.getPosition()));

        SoundBuffer buffer = new SoundBuffer(MINECRAFT_SOUND1);
        soundMgr.addSoundBuffer(buffer);
        playerSoundSource = new SoundSource(true, false);
        playerSoundSource.setPosition(camera.getPosition());
        playerSoundSource.setBuffer(buffer.getBufferId());
        soundMgr.addSoundSource("CREAK", playerSoundSource);
        playerSoundSource.play();
    }
}