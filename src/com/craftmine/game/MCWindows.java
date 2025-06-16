package com.craftmine.game;

//import com.craftmine.engine.camera.MCCursor;
import com.craftmine.engine.mouseinput.MouseInput;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import java.util.concurrent.Callable;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MCWindows {

    private final long windowHandle;
    private int height;
    private int width;
//    private MCCursor cursor;
    private Callable<Void> resizeFunc;
    private MouseInput mouseInput;

    public MCWindows(String title, GameResources.MCWindowsOptions opts , Callable<Void> resizeFunc) {
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

        if (opts.width > 0 && opts.height > 0){
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

        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);//不显示光标

        //帧缓冲区大小回调
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {resize(width, height);});
        //全局报错监听
        glfwSetErrorCallback((int errorCode, long msgPtr) -> {
            Logger.error("全局监听:错误代码:[{}]", errorCode, MemoryUtil.memUTF8(msgPtr));
        });
        //esc退出
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            keyCallBack(key,action);
        });

        //绑定上下文
        glfwMakeContextCurrent(windowHandle);

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        int refreshRate = vidMode.refreshRate();
        if (opts.fps > 0 && opts.fps > refreshRate) {
            glfwSwapInterval(0);
        } else {
            glfwSwapInterval(1);
        }

        glfwShowWindow(windowHandle);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetWindowSize(windowHandle, arrWidth, arrHeight);
        //模拟C语言指针
        this.width = arrWidth[0];
        this.height = arrHeight[0];

        mouseInput = new MouseInput(windowHandle);
        //cursor = new MCCursor(this);
        //cursor.render();
    }


    public void keyCallBack(int key, int action) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            mouseInput.setESCPressed(true);
        }else if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE){
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            mouseInput.setESCPressed(false);
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

    public void pollEvents(){
        glfwPollEvents();
    }

    protected void resize(int width, int height) {
        this.width = width;
        this.height = height;
        try{
            resizeFunc.call();
        } catch (Exception e) {
            Logger.error("调用 resize 回调时出错", e);
        }
    }

    public void update(){
        glfwSwapBuffers(windowHandle);
    }

    public boolean windowsShouldClose(){
        return glfwWindowShouldClose(windowHandle);
    }
    public MouseInput getMouseInput(){
        return mouseInput;
    }
    public int getHeight() {return height;}
    public int getWidth() {return width;}
    public long getWindowHandle() {return windowHandle;}
    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }
}