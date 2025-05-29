package com.craftmine.engine.light;

import org.joml.Vector3f;
import java.util.*;

//用来初始化各种光的，没实际作用
public class SceneLights {
    private AmbientLight ambientLight;//环境光
    private DirLight dirLight;//方向光
    private List<PointLight> pointLights;//点光源列表
    private List<SpotLight> spotLights;//聚光灯列表

    public SceneLights() {
        ambientLight = new AmbientLight();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        dirLight = new DirLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
    }

    public AmbientLight getAmbientLight() {return ambientLight;}
    public DirLight getDirLight() {return dirLight;}
    public List<PointLight> getPointLights() {return pointLights;}
    public List<SpotLight> getSpotLights() {return spotLights;}
    public void setSpotLights(List<SpotLight> spotLights) {this.spotLights = spotLights;}
}