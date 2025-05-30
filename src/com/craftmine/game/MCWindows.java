package com.craftmine.game;

import com.craftmine.engine.MouseInput;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import java.util.concurrent.Callable;
import com.craftmine.engine.Engine;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MCWindows {

    private final long windowHandle;
    private int height;
    private int width;
    private Callable<Void> resizeFunc;
    private MouseInput mouseInput;

    public MCWindows(String title,MCWindowsOptions opts , Callable<Void> resizeFunc) {
        this.resizeFunc = resizeFunc;//回调
        if (!glfwInit()){
            throw new IllegalStateException("无法初始化GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        //这个不重要
        if (opts.compatibleProfile){
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);//兼容模式
        } else{
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);//核心模式
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }

        if (opts.width > 0 || opts.height > 0){
            this.width = opts.width;
            this.height = opts.height;
        }else {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            this.width = vidMode.width();
            this.height = vidMode.height();
        }

        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL){
            throw new RuntimeException("无法创建GLFW窗口");
        }

        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {resize(width, height);});//帧缓冲区大小回调
        glfwSetErrorCallback((int errorCode, long msgPtr) -> {
            Logger.error("Error code[{}]", errorCode, MemoryUtil.memUTF8(msgPtr));
        });
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            keyCallBack(key,action);
        });

        //绑定上下文
        glfwMakeContextCurrent(windowHandle);

        if (opts.fps > 0){
            glfwSwapInterval(0);
        }else {
            glfwSwapInterval(1);
        }

        glfwShowWindow(windowHandle);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetWindowSize(windowHandle, arrWidth, arrHeight);
        this.width = arrWidth[0];
        this.height = arrHeight[0];

        mouseInput = new MouseInput(windowHandle);
    }

    public void keyCallBack(int key, int action) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE){
            glfwSetWindowShouldClose(windowHandle, true);
        }
    }

    public void cleanup(){
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null){
            callback.free();
        }
    }

    public MouseInput getMouseInput(){
        return mouseInput;
    }

    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public void pollEvents(){
        glfwPollEvents();
    }

    protected void resize(int width, int height) {
        this.width = width;
        this.height = height;
        try{
            resizeFunc.call();
        } catch (Exception e) {
            Logger.error("Error calling resize callback", e);
        }
    }

    public void update(){
        glfwSwapBuffers(windowHandle);
    }

    public boolean windowsShouldClose(){
        return glfwWindowShouldClose(windowHandle);
    }

    public int getHeight() {return height;}
    public int getWidth() {return width;}
    public long getWindowHandle() {return windowHandle;}

    public static class MCWindowsOptions{
        public boolean compatibleProfile;//是否使用旧版本函数，此处无用
        public int fps;
        public int ups = Engine.TARGET_UPS;
        public int height;
        public int width;
    }
}