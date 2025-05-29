package com.craftmine.engine.light;

import org.joml.Vector3f;

//方向光
public class DirLight {

    private Vector3f color;//光颜色（RGB）
    private Vector3f direction;//光方向
    private float intensity;//光强度

    public DirLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public Vector3f getColor() {return color;}
    public Vector3f getDirection() {return direction;}
    public float getIntensity() {return intensity;}
    public void setColor(Vector3f color) {this.color = color;}
    public void setColor(float r, float g, float b) {color.set(r, g, b);}
    public void setDirection(Vector3f direction) {this.direction = direction;}
    public void setIntensity(float intensity) {this.intensity = intensity;}
    public void setPosition(float x, float y, float z) {direction.set(x, y, z);}
}