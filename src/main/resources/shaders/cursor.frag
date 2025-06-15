#version 330 core

out vec4 FragColor;  // 输出颜色（RGBA）

void main() {
    // 将光标颜色设为白色（可自定义）
    FragColor = vec4(1.0, 1.0, 1.0, 0.8);  // R=1, G=1, B=1, A=0.8（半透明）
}