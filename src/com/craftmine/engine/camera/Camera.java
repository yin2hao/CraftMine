package com.craftmine.engine.camera;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import com.craftmine.engine.MCPerson;
import com.craftmine.engine.MapGen.MapGrid;

public class Camera {

    private Vector3f direction;//相机方向，是一个三维向量
    private Vector3f position;
    private Vector2f rotation;//旋转角（俯仰角和偏航角）
    private Vector3f right;//三维向量，表示相机的右方向
    private Vector3f up;//三维向量，表示相机的上方向
    private Matrix4f viewMatrix;
    private Matrix4f invViewMatrix;

    private MCPerson mcPerson;
    private MapGrid mapGrid;

    public Camera() {
        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();
        position = new Vector3f();
        viewMatrix = new Matrix4f();
        invViewMatrix = new Matrix4f();
        rotation = new Vector2f();
    }

    public void setCollision(MCPerson mcPerson, MapGrid mapGrid) {
        this.mcPerson = mcPerson;
        this.mapGrid = mapGrid;
    }

    //摄像头移动（根据摄像头方向）
    public void moveBackwards(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        Vector3f oldPos = new Vector3f(position);
        position.sub(direction);
        if (mcPerson != null && mapGrid != null && mcPerson.collide(position.x, position.y, position.z)) {
            position.set(oldPos);
        } else {
            recalculate();
        }
    }
    public void moveForward(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        Vector3f oldPos = new Vector3f(position);
        position.add(direction);
        if (mcPerson != null && mapGrid != null && mcPerson.collide(position.x, position.y, position.z)) {
            position.set(oldPos);
        } else {
            recalculate();
        }
    }
    public void moveLeft(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        Vector3f oldPos = new Vector3f(position);
        position.sub(right);
        if (mcPerson != null && mapGrid != null && mcPerson.collide(position.x, position.y, position.z)) {
            position.set(oldPos);
        } else {
            recalculate();
        }
    }
    public void moveRight(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        Vector3f oldPos = new Vector3f(position);
        position.add(right);
        if (mcPerson != null && mapGrid != null && mcPerson.collide(position.x, position.y, position.z)) {
            position.set(oldPos);
        } else {
            recalculate();
        }
    }
    public void moveUp(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        Vector3f oldPos = new Vector3f(position);
        position.add(up);
        if (mcPerson != null && mapGrid != null && mcPerson.collide(position.x, position.y, position.z)) {
            position.set(oldPos);
        } else {
            recalculate();
        }
    }
    public void moveDown(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        Vector3f oldPos = new Vector3f(position);
        position.sub(up);
        if (mcPerson != null && mapGrid != null && mcPerson.collide(position.x, position.y, position.z)) {
            position.set(oldPos);
        } else {
            recalculate();
        }
    }

    // 重新计算视图矩阵和逆视图矩阵
    private void recalculate() {
        viewMatrix.identity()
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .translate(-position.x, -position.y, -position.z);
        invViewMatrix.set(viewMatrix).invert();
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
    public Matrix4f getInvViewMatrix() {return invViewMatrix;}
    public Vector3f getPosition(){return position;}
    public Matrix4f getViewMatrix() {return viewMatrix;}
}