package com.craftmine.engine;

import com.craftmine.game.GameResources;

import java.util.*;

public class TextureCache {
    public static final String DEFAULT_TEXTURE = GameResources.DEFAULT_TEXTURE;
    private Map<String, Texture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<>();
        textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
    }

    public void cleanup(){
        textureMap.values().forEach(Texture::cleanup);
    }

    public  Texture createTexture(String texturePath){
        return textureMap.computeIfAbsent(texturePath, Texture::new);// 如果不存在则创建新的Texture对象
    }

    public Texture getTexture(String texturePath){
        Texture texture = null;
        if(textureMap != null ){
            texture = textureMap.get(texturePath);
        }
        if (texture == null) {
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }
}