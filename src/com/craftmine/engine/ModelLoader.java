package com.craftmine.engine;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.*;

public class ModelLoader {
    private ModelLoader() {
    }

    //这一坨是抄的，不知道这些常量有啥用
    public static Model loadModel(String modelID, String modelPath, TextureCache textureCache) {
        return loadModel(modelID, modelPath, textureCache,
                aiProcess_GenSmoothNormals |
                        aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate |
                        aiProcess_FixInfacingNormals |
                        aiProcess_CalcTangentSpace |
                        aiProcess_LimitBoneWeights |
                        aiProcess_PreTransformVertices |
                        aiProcess_GenBoundingBoxes  // 新增：生成碰撞包围盒
        );
    }

//    aiProcess_JoinIdenticalVertices : 这个标志会减少使用的顶点数量，识别那些可以在不同面之间重复使用的顶点。
//    aiProcess_Triangulate: 模型可能使用四边形或其他几何体来定义它们的元素。由于我们只处理三角形，因此必须使用这个标志将所有面分割成三角形（如果需要）。
//    aiProcess_FixInfacingNormals: 这个标志尝试反转可能指向内部的法线。
//    aiProcess_CalcTangentSpace: 在实现灯光时我们将使用此参数，但它基本上是利用法线信息来计算切线和副切线。
//    aiProcess_LimitBoneWeights: 在实现动画时我们将使用此参数，但它基本上是限制影响单个顶点的权重数量。
//    aiProcess_PreTransformVertices : 此标志对加载的数据进行一些变换，使模型位于原点，并将坐标修正为数学 OpenGL 坐标系。如果你在处理旋转的模型时遇到问题，请确保使用此标志。重要提示：如果你的模型使用动画，不要使用此标志，此标志将移除该信息
//    aiProcess_PreTransformVertices : 使用这个标志可以简化后续渲染处理

    public static Model loadModel(String modelID, String modelPath, TextureCache textureCache, int flags){
        //检查模型文件是否存在
        File file = new File(modelPath);
        if(!file.exists()){
            throw new RuntimeException("模型文件[" + modelPath + "]不存在");
        }
        String modelDir = file.getParent();//获得文件父路径

        //加载模型
        AIScene aiScene = aiImportFile(modelPath, flags);
        if(aiScene == null){
            throw new RuntimeException("加载模型[" + modelPath + "]时出错");
        }

        //处理材质
        int numMaterials = aiScene.mNumMaterials();//获取场景中的材质数量
        List<Material> materialList = new ArrayList<>();
        for(int i = 0; i < numMaterials; i++){
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materialList.add(processMaterial(aiMaterial, modelDir, textureCache));
        }//处理所有原始材质并添加到列表

        //处理网格
        int numMeshes = aiScene.mNumMeshes();//获取场景中的网格数量
        PointerBuffer aiMeshes = aiScene.mMeshes();//网格数据缓冲区
        Material defaultMaterial = new Material();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));//获取原始网格对象
            Mesh mesh = processMesh(aiMesh);
            int materialIdx = aiMesh.mMaterialIndex();//获取网格关联的材质索引
            Material material;
            if (materialIdx >= 0 && materialIdx < materialList.size()) {
                material = materialList.get(materialIdx);//获取顶点关联的材质
            } else {
                material = defaultMaterial;//没有有效材质时使用默认材质
            }
            material.getMeshList().add(mesh);
        }

        //如果有使用默认材质的网格，添加到材质列表
        if (!defaultMaterial.getMeshList().isEmpty()) {
            materialList.add(defaultMaterial);
        }
        return new Model(modelID, materialList);
    }

    //处理索引数据
    private static int[] processIndices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    //处理材质
    private static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color = AIColor4D.create();
            // 获取漫反射颜色
            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
                    color);
            if (result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }
            // 获取高光颜色
            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }
            // 获取反射率/光泽度
            float reflectance = 0.0f;
            float[] shininessFactor = new float[]{0.0f};
            int[] pMax = new int[]{1};
            result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininessFactor, pMax);
            if (result != aiReturn_SUCCESS) {
                reflectance = shininessFactor[0];
            }
            material.setReflectance(reflectance);

            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null,
                    null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            if (texturePath != null && texturePath.length() > 0) {
                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
                textureCache.createTexture(material.getTexturePath());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }

            return material;
        }
    }

    //处理网格
    private static Mesh processMesh(AIMesh aiMesh) {
        float[] vertices = processVertices(aiMesh);//顶点
        float[] normals = processNormals(aiMesh);//法线
        float[] textCoords = processTextCoords(aiMesh);
        int[] indices = processIndices(aiMesh);//索引
        AIAABB aabb = aiMesh.mAABB();//轴对齐包围盒

        // 如果没有纹理坐标，创建空的纹理坐标数组
        if (textCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }

        Vector3f aabbMin = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
        Vector3f aabbMax = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());

        return new Mesh(vertices, normals, textCoords, indices, aabbMin, aabbMax);
    }

    //处理法线数据
    private static float[] processNormals(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        float[] data = new float[buffer.remaining()*3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }

    //处理纹理坐标
    private static float[] processTextCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }

    //处理顶点数据
    private static float[] processVertices(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = textCoord.y();
            data[pos++] = textCoord.z();
        }
        return data;
    }
}