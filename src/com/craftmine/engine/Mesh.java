package com.craftmine.engine;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.opengl.GL30;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private int numVertices;//存储网格中顶点的数量
    private int vaoID;//顶点数组对象
    private List<Integer> vboIDList;//顶点缓冲对象

    public Mesh(float[] positions, float[] colors, int[] indices) {
        numVertices = indices.length;
        vboIDList = new ArrayList<>();

        //生成并绑定VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //生成VBO并添加到列表
        int vboID = glGenBuffers();
        vboIDList.add(vboID);
        //创建并填充顶点位置缓冲
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
        positionsBuffer.put(0, positions);
        //绑定VBO并上传数据
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        //启用顶点属性并设置属性指针(位置属性)
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //颜色VBO
        vboID = glGenBuffers();
        vboIDList.add(vboID);
        FloatBuffer colorsBuffer = MemoryUtil.memCallocFloat(colors.length);
        colorsBuffer.put(0, colors);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        //索引VBO
        vboID = glGenBuffers();
        vboIDList.add(vboID);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
        indicesBuffer.put(0, indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        //启用顶点属性并设置属性指针(位置属性)
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //释放临时缓冲
        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(colorsBuffer);
        MemoryUtil.memFree(indicesBuffer);
    }

    public void cleanup() {
        //删除所有VAO和VBO
        vboIDList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoID);
    }

    public int getNumVertices(){
        return numVertices;
    }

    public final int getVaoID(){
        return vaoID;
    }
}