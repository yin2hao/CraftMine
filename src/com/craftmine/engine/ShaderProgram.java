package com.craftmine.engine;

import org.lwjgl.opengl.GL30;
import java.util.*;
import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {
    private final int programID;//着色器主程序

    public ShaderProgram(List<ShaderModuleData> shaderModuleDataList) {
        programID = glCreateProgram();//创建着色器主程序
        if (programID == 0) {
            throw new RuntimeException("无法创建着色器");
        }

        List<Integer> shaderModules = new ArrayList<>();
        shaderModuleDataList.forEach(s -> shaderModules.add(createShader(Utils.readFile(s.shaderFile),s.shaderType)));
        link(shaderModules);
    }

    public void bind(){
        glUseProgram(programID);//激活指定的着色器程序
    }
    public void unbind(){
        glUseProgram(0);
    }

    public void cleanup(){
        unbind();
        if (programID != 0) {
            glDeleteProgram(programID);
        }
    }

    protected int createShader(String shaderCode, int shaderType){
        int shaderID = glCreateShader(shaderType);//着色器模块
        if (shaderID == 0) {
            throw new RuntimeException("创建着色器" + shaderType + "错误");
        }
        glShaderSource(shaderID, shaderCode);//将字符串形式的着色器代码（shaderCode）绑定到着色器对象（shaderID）。
        glCompileShader(shaderID);//编译绑定到shaderID的着色器代码

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("编译着色器[" + shaderType + "]失败,ID:" + glGetShaderInfoLog(shaderID, 1024));
        }
        glAttachShader(programID, shaderID);//将编译成功的着色器对象（shaderID）附加到指定的着色器程序
        return shaderID;
    }

    public int getProgramID(){
        return programID;
    }

    public void link(List<Integer> shaderModules){
        glLinkProgram(programID);//链接主程序中的着色器模块
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("链接着色器错误:" + glGetProgramInfoLog(programID, 1024));
        }

        shaderModules.forEach(s -> glDetachShader(programID, s));//分离已附加的着色器模块
        shaderModules.forEach(GL30::glDeleteShader);//删除所有着色器模块
    }

    public void validate(){
        glValidateProgram(programID);//验证着色器程序
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {
            throw new RuntimeException("验证着色器失败:" + glGetProgramInfoLog(programID, 1024));
        }
    }

    //Java 14+引入的一种简洁的不可变数据载体，用于表示着色器模块的元数据
    public record ShaderModuleData( String shaderFile, int shaderType){}
}