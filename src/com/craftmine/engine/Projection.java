package com.craftmine.engine;

import org.joml.Matrix4f;

//投影矩阵，将3D空间坐标转换为2D屏幕坐标
public class Projection {

    private static final float FOV = (float) Math.toRadians(60.0f);//以弧度表示的视野角度
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private Matrix4f invProjMatrix;
    private Matrix4f projMatrix;

    public Projection(int width, int height) {
        projMatrix = new Matrix4f();
        invProjMatrix = new Matrix4f();
        updateProjMatrix(width, height);
    }

    //窗口大小改变时调用以更新投影矩阵
    public void updateProjMatrix(int width, int height) {
        projMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
        invProjMatrix.set(projMatrix).invert();
    }

    public Matrix4f getInvProjMatrix() {return invProjMatrix;}
    public Matrix4f getProjMatrix(){
        return projMatrix;
    }
}