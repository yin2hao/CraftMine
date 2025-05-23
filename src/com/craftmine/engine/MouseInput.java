package com.craftmine.engine;

import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private Vector2f currentPos;
    private Vector2f displVec;
    private boolean inWindows;
    private boolean leftButtonPressed;
    private Vector2f previousPos;
    private boolean rightButtonPressed;


    public MouseInput(long windowsHandle) {
        previousPos = new Vector2f(-1, -1);
        currentPos = new Vector2f();
        displVec = new Vector2f();
        leftButtonPressed = false;
        rightButtonPressed = false;
        inWindows = false;

        glfwSetCursorPosCallback(windowsHandle, (handle, x, y) -> {
            currentPos.x = (float) x;
            currentPos.y = (float) y;
        });
        glfwSetCursorEnterCallback(windowsHandle, (handle, entered) -> {inWindows = entered;});
        glfwSetMouseButtonCallback(windowsHandle, (handle, button, action, mods) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = action == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public Vector2f getCurrentPos(){
        return currentPos;
    }

    public Vector2f getDisplVec(){
        return displVec;
    }

    public void input(){
        displVec.x = 0;
        displVec.y = 0;
        if(previousPos.x > 0 && previousPos.y > 0 && inWindows){
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if(rotateX){
                displVec.y = (float) deltax;
            }
            if(rotateY){
                displVec.x = (float) deltay;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed(){
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed(){
        return rightButtonPressed;
    }
}