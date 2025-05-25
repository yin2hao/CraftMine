package com.craftmine.engine;

import org.joml.*;

public class Camera {

    private Vector3f direction;//相机方向，是一个三维向量
    private Vector3f position;
    private Vector2f rotation;//旋转角（俯仰角和偏航角）
    private Vector3f right;//三维向量，表示相机的右方向
    private Vector3f up;//三维向量，表示相机的上方向
    private Matrix4f viewMatrix;

    public Camera() {
        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();
        position = new Vector3f();
        viewMatrix = new Matrix4f();
        rotation = new Vector2f();
    }

    public void addRotation(float x, float y) {
        rotation.add(x, y);
        recalculate();
    }
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
    }
    public void setRotation(float x, float y) {
        rotation.set(x, y);
        recalculate();
    }

    public Vector3f getPosition(){
        return position;
    }
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    //摄像头方向移动
    //关于移动处理部分暂时无法理解
    public void moveBackwards(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.sub(direction);
        recalculate();
    }
    public void moveForward(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.add(direction);
        recalculate();
    }
    public void moveLeft(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.sub(right);
        recalculate();
    }
    public void moveRight(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.add(right);
        recalculate();
    }
    public void moveUp(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.add(up);
        recalculate();
    }
    public void moveDown(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.sub(up);
        recalculate();
    }

    private void recalculate() {
        viewMatrix.identity()
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .translate(-position.x, -position.y, -position.z);
    }
}