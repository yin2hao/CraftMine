package com.craftmine.engine.mouseinput;

import com.craftmine.game.GameResources;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private Vector2f currentPos;
    private Vector2f previousPos;
    private Vector2f displVec;//鼠标偏移量
    private boolean inWindows;
    private boolean leftButtonPressed;
    private boolean rightButtonPressed;
    private boolean isESCPressed;

    public MouseInput(long windowsHandle) {
        //初始化
        previousPos = new Vector2f(-1, -1);//(-1,-1)通常用作一个标记，表示还没有有效的先前位置，以避免在第一次计算位移时产生不正确的巨大值
        currentPos = new Vector2f();
        displVec = new Vector2f();//位移向量
        leftButtonPressed = false;
        rightButtonPressed = false;
        inWindows = false;
        isESCPressed = false;

        //鼠标移动回调
        glfwSetCursorPosCallback(windowsHandle, (handle, x, y) -> {
            currentPos.x = (float) x;
            currentPos.y = (float) y;
        });
        //鼠标进入窗口回调
        glfwSetCursorEnterCallback(windowsHandle, (handle, entered) -> {inWindows = entered;});
        //鼠标点击回调
        //mods是Shift、Ctrl、Alt等快捷键，此处没有用到
        glfwSetMouseButtonCallback(windowsHandle, (handle, button, action, mods) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public void input(){
        GameResources.bugCheck();//下方的if有问题
        if (true) {
            displVec.x = 0;
            displVec.y = 0;
            if(previousPos.x > 0 && previousPos.y > 0 && inWindows){
                double deltax = currentPos.x - previousPos.x;
                double deltay = currentPos.y - previousPos.y;
                boolean rotateX = deltax != 0;
                boolean rotateY = deltay != 0;
                if(rotateX){
                    //3D视角控制中，鼠标水平移动（X轴）通常用来控制视角绕Y轴旋转（偏航Yaw）
                    displVec.y = (float) deltax;
                }
                if(rotateY){
                    displVec.x = (float) deltay;
                }
            }
            previousPos.x = currentPos.x;
            previousPos.y = currentPos.y;
        }
    }

    public boolean isLeftButtonPressed(){
        return leftButtonPressed;
    }
    public boolean isRightButtonPressed(){
        return rightButtonPressed;
    }
    public Vector2f getCurrentPos(){
        return currentPos;
    }
    public Vector2f getDisplVec(){
        return displVec;
    }
    public boolean isInWindows() {
        return inWindows;
    }
    public void setInWindows(boolean inWindows) {
        this.inWindows = inWindows;
    }
    public boolean isESCPressed() {
        return isESCPressed;
    }
    public void setESCPressed(boolean ESCPressed) {
        isESCPressed = ESCPressed;
    }
}