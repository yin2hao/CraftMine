package com.craftmine.engine.scene;

import com.craftmine.engine.*;
import com.craftmine.engine.light.*;
import com.craftmine.game.Entity;
import com.craftmine.game.GameResources;
import org.joml.*;

import java.util.*;
import static org.lwjgl.opengl.GL30.*;

public class SceneRender {
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    
    private static final String SCENE_SHADER_VERT = GameResources.SCENE_SHADER_VERT;
    private static final String SCENE_SHADER_FRAG = GameResources.SCENE_SHADER_FRAG;

    public SceneRender() {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        //创建着色器
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(SCENE_SHADER_VERT,GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(SCENE_SHADER_FRAG,GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();
    }

    public void render(Scene scene){
        shaderProgram.bind();

        updateLights(scene);
        // 设置公共uniform
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
        uniformsMap.setUniform("txtSampler", 0);

        // 渲染所有模型
        Collection<Model> models = scene.getModelMap().values();//获取场景中的所有待渲染模型
        TextureCache textureCache = scene.getTextureCache();
        Entity selectedEntity = scene.getSelectedEntity();
        for (Model model : models) {
            List<Entity> entities = model.getEntitieList();//使用当前model的所有实体实例
            for (Material material : model.getMaterialList()){
                // 设置材质uniform
                uniformsMap.setUniform("material.ambient", material.getAmbientColor());// 环境光颜色
                uniformsMap.setUniform("material.diffuse", material.getDiffuseColor());// 漫反射颜色
                uniformsMap.setUniform("material.specular", material.getSpecularColor());// 镜面反射颜色
                uniformsMap.setUniform("material.reflectance", material.getReflectance());// 反射率（高光强度）
                // 绑定纹理
                Texture texture = textureCache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE0);
                texture.bind();

                // 渲染材质的所有网格
                for (Mesh mesh : material.getMeshList()){
                    glBindVertexArray(mesh.getVaoID());
                    // 渲染网格的所有实体实例
                    // 方块破坏的bug在这里
                    GameResources.bugCheck();
                    for (Entity entity : entities){
                        uniformsMap.setUniform("selected",
                                selectedEntity != null && selectedEntity.getID().equals(entity.getID()) ? 1 : 0);
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                        glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }
        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    private void createUniforms() {
        //基本变换矩阵
        uniformsMap = new UniformsMap(shaderProgram.getProgramID());
        uniformsMap.createUniform("projectionMatrix");//投影矩阵
        uniformsMap.createUniform("modelMatrix");//视图矩阵
        uniformsMap.createUniform("viewMatrix");//模型矩阵:定义单个实体的位置、旋转和缩放
        uniformsMap.createUniform("txtSampler");//纹理采样器
        //材质属性
        uniformsMap.createUniform("material.diffuse");//漫反射颜色
        uniformsMap.createUniform("material.ambient");//环境光反射颜色
        uniformsMap.createUniform("material.specular");//镜面反射颜色
        uniformsMap.createUniform("material.reflectance");//反射率
        //环境光
        uniformsMap.createUniform("ambientLight.factor");//环境光强度
        uniformsMap.createUniform("ambientLight.color");//环境光颜色
        //点光源数组
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            String name = "pointLights[" + i + "]";
            uniformsMap.createUniform(name + ".position");
            uniformsMap.createUniform(name + ".color");
            uniformsMap.createUniform(name + ".intensity");
            uniformsMap.createUniform(name + ".att.constant");//衰减常数项
            uniformsMap.createUniform(name + ".att.linear");//衰减线性项
            uniformsMap.createUniform(name + ".att.exponent");//衰减指数项
        }
        //聚光灯数组
        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            String name = "spotLights[" + i + "]";
            uniformsMap.createUniform(name + ".pl.position");
            uniformsMap.createUniform(name + ".pl.color");
            uniformsMap.createUniform(name + ".pl.intensity");
            uniformsMap.createUniform(name + ".pl.att.constant");//衰减常数项
            uniformsMap.createUniform(name + ".pl.att.linear");//衰减线性项
            uniformsMap.createUniform(name + ".pl.att.exponent");//衰减指数项
            uniformsMap.createUniform(name + ".conedir");//聚光方向
            uniformsMap.createUniform(name + ".cutoff");//聚光切角
        }
        //定向光
        uniformsMap.createUniform("dirLight.color");
        uniformsMap.createUniform("dirLight.direction");
        uniformsMap.createUniform("dirLight.intensity");

        //选择方块
        uniformsMap.createUniform("selected");
    }

    private void updateLights(Scene scene) {
        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
        // 更新环境光
        SceneLights sceneLights = scene.getSceneLights();
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        uniformsMap.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniformsMap.setUniform("ambientLight.color", ambientLight.getColor());
        // 更新定向光(考虑视图变换)
        DirLight dirLight = sceneLights.getDirLight();
        Vector4f auxDir = new Vector4f(dirLight.getDirection(), 0);
        auxDir.mul(viewMatrix);
        Vector3f dir = new Vector3f(auxDir.x, auxDir.y, auxDir.z);
        uniformsMap.setUniform("dirLight.color", dirLight.getColor());
        uniformsMap.setUniform("dirLight.direction", dir);
        uniformsMap.setUniform("dirLight.intensity", dirLight.getIntensity());
        // 更新点光源
        List<PointLight> pointLights = sceneLights.getPointLights();
        int numPointLights = pointLights.size();
        PointLight pointLight;
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            if (i < numPointLights) {
                pointLight = pointLights.get(i);
            } else {
                pointLight = null;
            }
            String name = "pointLights[" + i + "]";
            updatePointLight(pointLight, name, viewMatrix);
        }

        // 更新聚光灯
        List<SpotLight> spotLights = sceneLights.getSpotLights();
        int numSpotLights = spotLights.size();
        SpotLight spotLight;
        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            if (i < numSpotLights) {
                spotLight = spotLights.get(i);
            } else {
                spotLight = null;
            }
            String name = "spotLights[" + i + "]";
            updateSpotLight(spotLight, name, viewMatrix);
        }
    }

    private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix) {
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;
        if (pointLight != null) {
            aux.set(pointLight.getPosition(), 1);
            aux.mul(viewMatrix);
            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(pointLight.getColor());
            intensity = pointLight.getIntensity();
            PointLight.Attenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }
        uniformsMap.setUniform(prefix + ".position", lightPosition);
        uniformsMap.setUniform(prefix + ".color", color);
        uniformsMap.setUniform(prefix + ".intensity", intensity);
        uniformsMap.setUniform(prefix + ".att.constant", constant);
        uniformsMap.setUniform(prefix + ".att.linear", linear);
        uniformsMap.setUniform(prefix + ".att.exponent", exponent);
    }

    private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix) {
        PointLight pointLight = null;
        Vector3f coneDirection = new Vector3f();
        float cutoff = 0.0f;
        if (spotLight != null) {
            coneDirection = spotLight.getConeDirection();
            cutoff = spotLight.getCutOff();
            pointLight = spotLight.getPointLight();
        }

        uniformsMap.setUniform(prefix + ".conedir", coneDirection);
        uniformsMap.setUniform(prefix + ".cutoff", cutoff);
        updatePointLight(pointLight, prefix + ".pl", viewMatrix);
    }

    public void cleanup(){
        shaderProgram.cleanup();
    }

}