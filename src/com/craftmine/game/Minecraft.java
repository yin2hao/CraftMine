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
        Engine game = new Engine("CraftMine", new MCWindows.MCWindowsOptions(),mc);//初始化窗口
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

                //对于顶面纹理
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // 为右侧面纹理坐标重复的顶点
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // 为左侧面纹理坐标重复的顶点
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // 为底面纹理坐标重复的顶点
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                // 正面纹理坐标
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                // 背面纹理坐标
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // 顶面纹理坐标
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // 右侧面纹理坐标
                0.0f, 0.0f,
                0.0f, 0.5f,

                // 左侧面纹理坐标
                0.5f, 0.0f,
                0.5f, 0.5f,

                // 底面纹理坐标
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // 正面
                0, 1, 3, 3, 1, 2,
                // 顶面
                8, 10, 11, 9, 8, 11,
                // 右面
                12, 13, 7, 5, 12, 7,
                // 左面
                14, 15, 6, 4, 14, 6,
                // 底面
                16, 18, 19, 17, 16, 19,
                // 背面
                4, 6, 7, 5, 4, 7,
        };
        Texture texture = scene.getTextureCache().createTexture("src/main/resources/models/cube/cube.png");
        Material material = new Material();
        material.setTexturePath(texture.getTexturePath());
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = new Mesh(positions, textCoords, indices);
        material.getMeshList().add(mesh);
        Model cubeModel = new Model("cube-model", materialList);
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getID());
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
    }
}