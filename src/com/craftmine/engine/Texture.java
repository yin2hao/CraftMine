package com.craftmine.engine;

import org.lwjgl.system.MemoryStack;
import java.nio.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private int textureID;
    private String texturePath;

    public Texture(int width, int height, ByteBuffer buf){
        this.texturePath = "";
        generateTexture(width, height, buf);
    }

    public Texture(String texturePath){
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.texturePath = texturePath;
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            //将图像实际加载到ByteBuffer中
            ByteBuffer buf = stbi_load(texturePath, w, h, channels, 4);
            if (buf == null) {
                System.out.println("图像[" + texturePath + "]加载失败: " + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();

            generateTexture(width, height, buf);
            stbi_image_free(buf);
        }
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void cleanup(){
        glDeleteTextures(textureID);
    }

    //将纹理上传到GPU
    private void generateTexture(int width, int height, ByteBuffer buf){
        textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public String getTexturePath(){
        return texturePath;
    }
}