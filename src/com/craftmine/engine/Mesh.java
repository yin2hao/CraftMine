package com.craftmine.engine;

import com.craftmine.game.gameResources;
import org.joml.Vector3f;
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
    private Vector3f aabbMax;
    private Vector3f aabbMin;

    public Mesh(float[] positions, float[] normals,float[] textCoords, int[] indices) {
        this(positions, normals, textCoords, indices, new Vector3f(), new Vector3f());
    }

    public Mesh(float[] positions, float[] normals, float[] textCoords, int[] indices, Vector3f aabbMin, Vector3f aabbMax) {
        this.aabbMin = aabbMin;
        this.aabbMax = aabbMax;
        numVertices = indices.length;
        vboIDList = new ArrayList<>();

        //生成并绑定VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //位置VBO
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

        //法线VBO
        vboID = glGenBuffers();
        vboIDList.add(vboID);
        FloatBuffer normalsBuffer = MemoryUtil.memCallocFloat(normals.length);
        normalsBuffer.put(0, normals);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        //纹理坐标VBO
        gameResources.bugCheck();//插眼
        vboID = glGenBuffers();
        vboIDList.add(vboID);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(textCoords.length);
        textCoordsBuffer.put(0, textCoords);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);

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
        MemoryUtil.memFree(textCoordsBuffer);
        MemoryUtil.memFree(indicesBuffer);
        MemoryUtil.memFree(normalsBuffer);
    }

    public void cleanup() {
        //删除所有VAO和VBO
        vboIDList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoID);
    }

    public int getNumVertices(){return numVertices;}
    public final int getVaoID(){return vaoID;}
    public Vector3f getAabbMax() {return aabbMax;}
    public Vector3f getAabbMin() {return aabbMin;}
}