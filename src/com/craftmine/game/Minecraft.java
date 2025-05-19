package com.craftmine.game;

import com.craftmine.engine.*;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements IAppLogic{

    private Entity cubeEntity;
    private Vector4f displInc = new Vector4f();
    private float rotation;

    public static void main(String[] args) {
        Minecraft mc = new Minecraft();
        Engine game = new Engine("CraftMine", new MCWindows.MCWindowsOptions(),mc);//初始化窗户
        game.start();//循环开始
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(MCWindows windows, Scene scene, Render render) {
        float[] positions = new float[]{
                // VO
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
                //正面
                0, 1, 3, 3, 1, 2,
                //顶面
                4, 0, 3, 5, 4, 3,
                //右面
                3, 2, 7, 5, 3, 7,
                //左面
                6, 1, 0, 6, 0, 4,
                //底面
                2, 1, 6, 2, 6, 7,
                //背面
                7, 6, 4, 7, 4, 5,
        };
        List<Mesh> meshList = new ArrayList<Mesh>();
        Mesh mesh = new Mesh(positions, colors, indices);
        meshList.add(new Mesh(positions, colors, indices));

        Model model = new Model("cube-model", meshList);
        scene.addModel(model);

        cubeEntity = new Entity("cube-entity", "cube-model");
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);
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
    public void input(MCWindows windows, Scene scene, long diffTimeMillis) {
        displInc.zero();
        if (windows.isKeyPressed(GLFW_KEY_UP)) {
            displInc.y = 1;
        } else if (windows.isKeyPressed(GLFW_KEY_DOWN)) {
            displInc.y = -1;
        }
        if (windows.isKeyPressed(GLFW_KEY_LEFT)) {
            displInc.x = -1;
        } else if (windows.isKeyPressed(GLFW_KEY_RIGHT)) {
            displInc.x = 1;
        }
        if (windows.isKeyPressed(GLFW_KEY_A)) {
            displInc.z = -1;
        } else if (windows.isKeyPressed(GLFW_KEY_Q)) {
            displInc.z = 1;
        }
        if (windows.isKeyPressed(GLFW_KEY_Z)) {
            displInc.w = -1;
        } else if (windows.isKeyPressed(GLFW_KEY_X)) {
            displInc.w = 1;
        }

        displInc.mul(diffTimeMillis / 1000.0f);

        Vector3f entityPos = cubeEntity.getPosition();
        cubeEntity.setPosition(displInc.x+entityPos.x, displInc.y+entityPos.y, displInc.z+entityPos.z);
        cubeEntity.setScale(cubeEntity.getScale() + displInc.w);
        cubeEntity.updateModelMatrix();
    }
}