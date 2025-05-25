package com.craftmine.engine;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.time.format.TextStyle;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {
    private ModelLoader() {
    }

    public static Model loadModel(String modelID, String modelPath, TextureCache textureCache){
        return loadModel(modelID, modelPath, textureCache, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices);
    }

//    aiProcess_JoinIdenticalVertices : 这个标志会减少使用的顶点数量，识别那些可以在不同面之间重复使用的顶点。
//    aiProcess_Triangulate: 模型可能使用四边形或其他几何体来定义它们的元素。由于我们只处理三角形，因此必须使用这个标志将所有面分割成三角形（如果需要）。
//    aiProcess_FixInfacingNormals: 这个标志尝试反转可能指向内部的法线。
//    aiProcess_CalcTangentSpace: 在实现灯光时我们将使用此参数，但它基本上是利用法线信息来计算切线和副切线。
//    aiProcess_LimitBoneWeights: 在实现动画时我们将使用此参数，但它基本上是限制影响单个顶点的权重数量。
//    aiProcess_PreTransformVertices : 此标志对加载的数据进行一些变换，使模型位于原点，并将坐标修正为数学 OpenGL 坐标系。如果你在处理旋转的模型时遇到问题，请确保使用此标志。重要提示：如果你的模型使用动画，不要使用此标志，此标志将移除该信息

    public static Model loadModel(String modelID, String modelPath, TextureCache textureCache, int flags){
        File file = new File(modelPath);
        if(!file.exists()){
            throw new RuntimeException("Model file does not exist[" + modelPath + "]");
        }
        String modelDir = file.getParent();

        AIScene aiScene = aiImportFile(modelPath, flags);
        if(aiScene == null){
            throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
        }
        int numMaterials = aiScene.mNumMaterials();
        List<Material> materialList = new ArrayList<>();
        for(int i = 0; i < numMaterials; i++){
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materialList.add(processMaterial(aiMaterial, modelDir, textureCache));
        }

        //
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Material defaultMaterial = new Material();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);
            int materialIdx = aiMesh.mMaterialIndex();
            Material material;
            if (materialIdx >= 0 && materialIdx < materialList.size()) {
                material = materialList.get(materialIdx);
            } else {
                material = defaultMaterial;
            }
            material.getMeshList().add(mesh);
        }

        if (!defaultMaterial.getMeshList().isEmpty()) {
            materialList.add(defaultMaterial);
        }

        return new Model(modelID, materialList);
    }

    private static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color = AIColor4D.create();

            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
                    color);
            if (result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }

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

    private static Mesh processMesh(AIMesh aiMesh) {
        float[] vertices = processVertices(aiMesh);
        float[] textCoords = processTextCoords(aiMesh);
        int[] indices = processIndices(aiMesh);

        // 纹理坐标可能未填充。我们至少需要空槽
        if (textCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }

        return new Mesh(vertices, textCoords, indices);
    }

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