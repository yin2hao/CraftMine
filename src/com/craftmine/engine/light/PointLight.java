package com.craftmine.engine.light;

import org.joml.Vector3f;

//点光源
public class PointLight {

    private Attenuation attenuation;//衰减
    private Vector3f color;//光源颜色（RGB）
    private float intensity;//光源强度
    private Vector3f position;//光源位置

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        attenuation = new Attenuation(0, 0, 1);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public Attenuation getAttenuation() {return attenuation;}
    public Vector3f getColor() {return color;}
    public float getIntensity() {return intensity;}
    public Vector3f getPosition() {return position;}
    public void setAttenuation(Attenuation attenuation) {this.attenuation = attenuation;}
    public void setColor(Vector3f color) {this.color = color;}
    public void setColor(float r, float g, float b) {color.set(r, g, b);}
    public void setIntensity(float intensity) {this.intensity = intensity;}
    public void setPosition(float x, float y, float z) {position.set(x, y, z);}

    //这个静态类只是用于存储衰减公式
    public static class Attenuation {
        private float constant;//衰减公式中的常数项
        private float exponent;//衰减公式中的指数项
        private float linear;//衰减公式中的线性项

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant() {return constant;}
        public float getExponent() {return exponent;}
        public float getLinear() {return linear;}
        public void setConstant(float constant) {this.constant = constant;}
        public void setExponent(float exponent) {this.exponent = exponent;}
        public void setLinear(float linear) {this.linear = linear;}
    }
}