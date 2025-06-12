package com.craftmine.engine.GUI;

import imgui.ImDrawData;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GUIMesh {
    private int indicesVBO;//索引缓冲对象ID
    private int VAOID;//顶点数组对象ID
    private int verticesVBO;//顶点缓冲对象ID

    public GUIMesh() {
        //生成并绑定顶点数组对象
        VAOID = glGenVertexArrays();
        glBindVertexArray(VAOID);

        verticesVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.sizeOfImDrawVert(), 16);

        indicesVBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(indicesVBO);
        glDeleteBuffers(verticesVBO);
        glDeleteVertexArrays(VAOID);
    }

    public int getIndicesVBO(){return indicesVBO;}
    public int getVAOID(){return VAOID;}
    public int getVerticesVBO(){return verticesVBO;}
}