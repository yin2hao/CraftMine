package com.craftmine.game;

import org.joml.*;

import java.lang.Math;

public class Entity {

    private final String ID;
    private final String modelID;//关联的模型资源 ID
    private Matrix4f modelMatrix;
    private Vector3f position;//3D位置
    private Quaternionf rotation;//四元数旋转
    private float scale;//统一缩放值

    public Entity(String ID, String modelID){
        this.ID = ID;
        this.modelID = modelID;
        modelMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1;
    }

    public final void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setScale(float scale){
        this.scale = scale;
//        this.scale = 1.0f; // 强制固定为 1.0f

    }

    //更新实体的模型矩阵，将实体的 位置、旋转和缩放组合成一个最终的4x4变换矩阵
    public void updateModelMatrix(){
        modelMatrix.identity(); // 先重置为单位矩阵，防止累加误差
        modelMatrix.translationRotateScale(position, rotation, scale);
        float snappedX = Math.round(position.x);
        float snappedY = Math.round(position.y);
        float snappedZ = Math.round(position.z);
        modelMatrix.translationRotateScale(
                new Vector3f(snappedX, snappedY, snappedZ),
                rotation,
                scale
        );
    }

    public String getID(){return ID;}
    public Matrix4f getModelMatrix(){return modelMatrix;}
    public Vector3f getPosition(){return position;}
    public float getScale() {return scale;}
    public Quaternionf getRotation() {return rotation;}
    public String getModelID(){return modelID;}
}