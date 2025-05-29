package com.craftmine.engine;

import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Material {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    private Vector4f diffuseColor;
    private List<Mesh> meshList;
    private String texturePath;
    private Vector4f ambientColor;
    private float reflectance;
    private Vector4f specularColor;

    public Material() {
        diffuseColor = DEFAULT_COLOR;
        ambientColor = DEFAULT_COLOR;
        specularColor = DEFAULT_COLOR;
        meshList = new ArrayList<>();
    }

    public void cleanup(){
        meshList.forEach(Mesh::cleanup);
    }

    public Vector4f getAmbientColor(){return ambientColor;}
    public float getReflectance(){return reflectance;}
    public Vector4f getSpecularColor(){return specularColor;}
    public void setAmbientColor(Vector4f ambientColor) {this.ambientColor = ambientColor;}
    public void setReflectance(float reflectance) {this.reflectance = reflectance;}
    public void setSpecularColor(Vector4f specularColor) {this.specularColor = specularColor;}
    public List<Mesh> getMeshList(){
        return meshList;
    }
    public Vector4f getDiffuseColor() {return diffuseColor;}
    public String getTexturePath(){
        return texturePath;
    }
    public void setTexturePath(String texturePath){
        this.texturePath = texturePath;
    }
    public void setDiffuseColor(Vector4f diffuseColor) {this.diffuseColor = diffuseColor;}
}