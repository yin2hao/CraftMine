package com.craftmine.game;

import com.craftmine.engine.*;
import com.craftmine.engine.GUI.IGUIInstance;
import com.craftmine.engine.camera.Camera;
import com.craftmine.engine.light.LightControls;
import com.craftmine.engine.light.PointLight;
import com.craftmine.engine.light.SceneLights;
import com.craftmine.engine.light.SpotLight;
import imgui.*;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements IAppLogic, IGUIInstance {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Entity cubeEntity;
    private float rotation;
    private LightControls lightControls;

    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new MCWindows.MCWindowsOptions(),mc);//初始化窗口
        game.start();//循环开始
    }

    @Override
    public void cleanup() {
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

    @Override
    public void init(MCWindows window, Scene scene, Render render) {
        Model cubeModel = ModelLoader.loadModel("cube-model", gameResources.CUBE_MODEL_PATH1,
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getID());
        cubeEntity.setPosition(0, 0, -2);
        cubeEntity.updateModelMatrix();
        scene.addEntity(cubeEntity);

        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setSceneLights(sceneLights);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));

        lightControls = new LightControls(scene);
        scene.setGuiInstance(lightControls);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {
        //原本这里是旋转，但现在不一样了，删了
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
        if (windows.isKeyPressed(GLFW_KEY_UP)) {
            camera.moveUp(move);
        } else if (windows.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = windows.getMouseInput();
        if (mouseInput.isRightButtonPressed()){
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
                    (float) Math.toRadians(displVec.y * MOUSE_SENSITIVITY));
        }
    }
}