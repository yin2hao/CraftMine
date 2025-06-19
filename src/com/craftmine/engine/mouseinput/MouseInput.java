package com.craftmine.engine.mouseinput;

import com.craftmine.game.GameResources;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private Vector2f currentPos;//鼠标当前位置
    private Vector2f previousPos;//鼠标上一次位置
    private Vector2f displVec;//鼠标偏移量
    private long leftButtonPressedTime;//鼠标左键按下时间
    private long leftButtonReleasedTime;//鼠标左键释放时间
    private long durationTime = 0;//鼠标左键按下持续时间
    private int action;//鼠标操作类型（按下或释放）
    private boolean inWindows;//鼠标是否在窗口内
    private boolean leftButtonPressed;//鼠标左键是否按下
    private boolean rightButtonPressed;//鼠标右键是否按下
    private boolean isESCPressed;//是否按下ESC键

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
            if (button == GLFW_MOUSE_BUTTON_1) {
                if (action == GLFW_PRESS) {
                    leftButtonPressed = true;
                    leftButtonPressedTime = System.currentTimeMillis();
                } else if (action == GLFW_RELEASE) {
                    leftButtonPressed = false;
                    leftButtonReleasedTime = System.currentTimeMillis();
                }
            }
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
            this.action = action;
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

    public void setCursorPosition(long windowsHandle, float x, float y){
        //改变鼠标位置vv
        currentPos.x = x;
        currentPos.y = y;
        glfwSetCursorPos(windowsHandle, x, y);
    }

    public void resetTime(){
        //重置鼠标左键按下时间
        leftButtonPressedTime = 0;
        durationTime = 0;
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
    public long getLeftButtonPressedTime() {
        return leftButtonPressedTime;
    }
    public void setLeftButtonPressedTime() {
        this.leftButtonPressedTime = System.currentTimeMillis();
    }
    public long getDurationTime() {
        return durationTime;
    }
    public void updateDurationTime() {
            durationTime += (System.currentTimeMillis() - leftButtonPressedTime);
    }
}