package com.craftmine.game;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import java.util.concurrent.Callable;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MCWindows extends Minecraft {
    private final long windowshandle;
    private Callable<Void> resizeFunc;
    private int height;
    private int width;


    public MCWindows(String title, MCWindowsOptions opts, Callable<Void> resizeFunc) {
        this.resizeFunc = resizeFunc;
        if (!glfwInit()) {
            throw new IllegalStateException("无法初始化GLFW");
        }
        glfwDefaultWindowHints();//将所有窗口创建相关的配置选项恢复为GLFW的默认值
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        //兼容模式和核心模式未更改，opts.compatibleProfile

        if (opts.width > 0 || opts.height > 0) {
            this.width = opts.width;
            this.height = opts.height;
        } else {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode VidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            this.width = VidMode.width();
            this.height = VidMode.height();
        }

        windowshandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowshandle == NULL) {
            throw new RuntimeException("无法创建GLFW窗口");
        }

        glfwSetFramebufferSizeCallback(windowshandle, (window, w, h) -> resize(windows, w, h));
        glfwSetErrorCallback((int errorCode, long msgPtr) ->
                Logger.error("GLFW错误码:{},错误信息:{}", errorCode, MemoryUtil.memUTF8(msgPtr))
        );

        glfwSetKeyCallback(windowshandle, (window, key, scancode, action, mods) -> {
            KeyCallBack(key, action);
        });

        glfwMakeContextCurrent(windowshandle);

        if (opts.fps > 0) {
            glfwSwapInterval(0);//禁用垂直同步
        } else {
            glfwSwapInterval(1);//启用垂直同步
        }

        glfwShowWindow(windowshandle);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetWindowSize(windowshandle, arrWidth, arrHeight);
        this.width = arrWidth[0];
        this.height = arrHeight[0];
    }

    public static class MCWindowsOptions() {
        public boolean compatibleProfile;
        public int height;
        public int width;
        public int fps;
        public int ups = Engine.TARGET_UPS;
    }

    public void KeyCallBack(int key, int action) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwWindowShouldClose(windowshandle, true);
        }
    }

    public void cleaup(){
        glfwFreeCallbacks(windowshandle);//移除所有通过glfwSetCallback注册到该窗口的回调（如键盘、鼠标、窗口大小回调）
        glfwDestroyWindow(windowshandle);//销毁 GLFW 窗口，释放其占用的系统资源（如 OpenGL 上下文、显存、窗口句柄）
        //GLFW 初始化时（glfwInit()）会分配全局资源，必须对称调用 glfwTerminate()
        glfwTerminate();// 终止 GLFW 库
        glfwErrorCallBack callback = glfwSetErrorCallback(null);//清除错误回调
        if (callback != null) {
            callback.free();
        }
    }

    public void pollEvent(){
        glfwPollEvents();
    }

    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(windowshandle, keyCode) == GLFW_PRESS;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        try{
            resizeFunc.call();
        } catch (Exception e) {
            Logger.error("调用 resize 回调时出错", e);
        }
    }

    public long getWindowshandle(){return windowshandle;}
    public int getHeight() {return height;}
    public int getWidth() {return width;}
}