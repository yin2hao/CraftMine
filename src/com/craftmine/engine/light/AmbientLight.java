package com.craftmine.engine.light;

import org.joml.Vector3f;

//环境光
public class AmbientLight {

    private Vector3f color;//环境光颜色（RGB）
    private float intensity;//光强度

    public AmbientLight(float intensity, Vector3f color) {
        this.intensity = intensity;
        this.color = color;
    }

    //默认定义
    public AmbientLight() {
        this(1.0f, new Vector3f(1.0f, 1.0f, 1.0f));
    }

    public Vector3f getColor() {return color;}
    public float getIntensity() {return intensity;}
    public void setColor(Vector3f color) {this.color = color;}
    public void setColor(float r, float g, float b) {color.set(r, g, b);}
    public void setIntensity(float intensity) {this.intensity = intensity;}
}