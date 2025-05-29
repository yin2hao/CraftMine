package com.craftmine.engine.light;

import org.joml.Vector3f;

//聚光灯
public class SpotLight {

    private Vector3f coneDirection;//聚光灯光锥方向
    private float cutOffAngle;//聚光灯光锥半开角
    private float cutOff;//聚光灯光锥半开角的余弦值
    private PointLight pointLight;//继承点光源的属性（位置，颜色，强度，衰减）

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        this.cutOffAngle = cutOffAngle;
        setCutOffAngle(cutOffAngle);
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }
    public float getCutOff() {
        return cutOff;
    }
    public float getCutOffAngle() {
        return cutOffAngle;
    }
    public PointLight getPointLight() {
        return pointLight;
    }
    public void setConeDirection(float x, float y, float z) {
        coneDirection.set(x, y, z);
    }
    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }
    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public final void setCutOffAngle(float cutOffAngle) {
        this.cutOffAngle = cutOffAngle;
        cutOff = (float) Math.cos(Math.toRadians(cutOffAngle));
    }

}