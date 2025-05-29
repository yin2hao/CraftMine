package com.craftmine.engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

//统一变量
public class UniformsMap {

    private int programID;
    private Map<String, Integer> uniforms;

    public UniformsMap(int programID) {
        this.programID = programID;
        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(programID, uniformName);
        if (uniformLocation < 0) {
            throw new RuntimeException("无法在着色器程序[" + programID + "]找到统一变量[" + uniformName + "]");
        }
        uniforms.put(uniformName, uniformLocation);
    }

    private int getUniformLocation(String uniformName){
        Integer location = uniforms.get(uniformName);
        if (location == null) {
            throw new RuntimeException("无法找到统一变量 [" + uniformName + "]");
        }
        return location.intValue();
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(getUniformLocation(uniformName),false,value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(getUniformLocation(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, Vector2f value) {
        glUniform2f(getUniformLocation(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(getUniformLocation(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(getUniformLocation(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(getUniformLocation(uniformName),value);
    }
}