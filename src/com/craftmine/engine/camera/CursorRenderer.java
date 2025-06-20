//package com.craftmine.engine.camera;
//
//import com.craftmine.engine.ShaderProgram;
//import com.craftmine.game.GameResources;
//import org.joml.Matrix4f;
//import org.lwjgl.opengl.GL30;
//import org.lwjgl.system.MemoryUtil;
//
//import java.nio.FloatBuffer;
//
//public class CursorRenderer {
//    private int vaoId;
//    private int vboId;
//    private static final String CURSOR_VERT = GameResources.CURSOR_VERT;
//    private static final String CURSOR_FRAG = GameResources.CURSOR_FRAG;
//    private ShaderProgram shader;
//
//    public CursorRenderer() {
//        init();
//    }
//
//    private void init() {
//        // 顶点数据（十字光标）
//        float[] vertices = {
//                // 垂直部分 (上下延伸)
//                -1.0f, -15.0f, 0.0f,
//                -1.0f,  15.0f, 0.0f,
//                1.0f,  15.0f, 0.0f,
//                1.0f, -15.0f, 0.0f,
//                // 水平部分 (左右延伸)
//                -15.0f, -1.0f, 0.0f,
//                -15.0f,  1.0f, 0.0f,
//                15.0f,  1.0f, 0.0f,
//                15.0f, -1.0f, 0.0f
//        };
//
//        // 创建 VAO 和 VBO
//        vaoId = GL30.glGenVertexArrays();
//        vboId = GL30.glGenBuffers();
//
//        GL30.glBindVertexArray(vaoId);
//        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
//
//        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
//        vertexBuffer.put(vertices).flip();
//        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_STATIC_DRAW);
//        MemoryUtil.memFree(vertexBuffer);
//
//        // 设置顶点属性指针
//        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
//        GL30.glEnableVertexAttribArray(0);
//
//        // 解绑
//        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
//        GL30.glBindVertexArray(0);
//
//        // 创建着色器
//        shader = new ShaderProgram(CURSOR_VERT);
//        shader = new ShaderProgram(CURSOR_FRAG);
//    }
//
//    public void render(int width, int height) {
//        // 设置正交投影
//        shader.bind();
//        Matrix4f projection = new Matrix4f().ortho(0, width, 0, height, -1, 1);
//        shader.setUniform("projection", projection);
//
//        // 渲染
//        GL30.glBindVertexArray(vaoId);
//        GL30.glDrawArrays(GL30.GL_QUADS, 0, 8);
//        GL30.glBindVertexArray(0);
//
//        shader.unbind();
//    }
//
//    public void cleanup() {
//        GL30.glDeleteVertexArrays(vaoId);
//        GL30.glDeleteBuffers(vboId);
//        shader.cleanup();
//    }
//}