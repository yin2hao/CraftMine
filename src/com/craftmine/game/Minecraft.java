package com.craftmine.game;

import com.craftmine.engine.*;
import imgui.*;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements IAppLogic, IGUIInstance{

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Entity cubeEntity;
    private float rotation;

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
        Model cubeModel = ModelLoader.loadModel("cube-model", "src/main/resources/models/cube/cube.obj",
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getID());
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);

        scene.setGuiInstance(this);
    }

    @Override
    public void update(MCWindows windows, Scene scene, long diffTimeMillis) {
        rotation += 1.5;
        if (rotation > 360) {
            rotation = 0;
        }
        cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity.updateModelMatrix();
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