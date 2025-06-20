package com.craftmine.engine.camera;

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
        Vector3f moveVector = viewMatrix.positiveZ(new Vector3f()).mul(inc);
        moveAndCollideByAxis(moveVector);
    }
    public void moveForward(float inc) {
        Vector3f moveVector = viewMatrix.positiveZ(new Vector3f()).negate().mul(inc);
        moveAndCollideByAxis(moveVector);
    }
    public void moveLeft(float inc) {
        Vector3f moveVector = viewMatrix.positiveX(new Vector3f()).negate().mul(inc);
        moveAndCollideByAxis(moveVector);
    }
    public void moveRight(float inc) {
        Vector3f moveVector = viewMatrix.positiveX(new Vector3f()).mul(inc);
        moveAndCollideByAxis(moveVector);
    }
    public void moveUp(float inc) {
        moveAndCollideByAxis(new Vector3f(0, inc, 0));
    }
    public void moveDown(float inc) {
        moveAndCollideByAxis(new Vector3f(0, -inc, 0));
    }

    // 检查碰撞并输出调试信息
    private boolean checkCollision(Vector3f position) {
        if (mcPerson == null || mapGrid == null) {
            System.out.println("警告: mcPerson或mapGrid为null，跳过碰撞检测");
            return false;
        }
        mcPerson.setPosition(position);// 更新人物位置
        boolean collision = mcPerson.collide(position.x, position.y, position.z);// 检查碰撞
        return collision;
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

    private void moveAndCollideByAxis(Vector3f moveVector) {
        if (mcPerson == null || mapGrid == null) {
            position.add(moveVector);
            recalculate();
            return;
        }

        Vector3f originalPos = new Vector3f(position);

        // 在X轴上移动
        position.x += moveVector.x;
        if (checkCollision(position)) {
            position.x = originalPos.x; // 发生碰撞，撤销X轴移动
        }

        // 在Y轴上移动
        position.y += moveVector.y;
        if (checkCollision(position)) {
            position.y = originalPos.y; // 发生碰撞，撤销Y轴移动
        }

        // 在Z轴上移动
        position.z += moveVector.z;
        if (checkCollision(position)) {
            position.z = originalPos.z; // 发生碰撞，撤销Z轴移动
        }

        recalculate();
    }

    public Matrix4f getInvViewMatrix() {return invViewMatrix;}
    public Vector3f getPosition(){return position;}
    public Matrix4f getViewMatrix() {return viewMatrix;}
}