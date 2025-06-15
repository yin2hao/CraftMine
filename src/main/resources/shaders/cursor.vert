#version 330 core  // 使用 GLSL 3.30（兼容 OpenGL 3.3）

layout (location = 0) in vec3 aPos;  // 输入顶点位置（3D 坐标）

uniform mat4 projection;  // 正交投影矩阵（从 Java 代码传入）

void main() {
    // 将顶点位置乘以投影矩阵，转换为裁剪空间坐标
    gl_Position = projection * vec4(aPos, 1.0);
}