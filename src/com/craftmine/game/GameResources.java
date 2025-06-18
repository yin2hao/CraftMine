package com.craftmine.game;

public class GameResources {
    //背景图纹理
    public static final String DEFAULT_TEXTURE = "src/main/resources/models/default/default_texture.png";

    //草方块纹理
    public static final String CUBE_MODEL_PATH1 = "src/main/resources/models/cube/cube.obj";

    //着色器目录
    public static final String SCENE_SHADER_VERT = "src/main/resources/shaders/scene.vert";
    public static final String SCENE_SHADER_FRAG = "src/main/resources/shaders/scene.frag";

    //GUI着色器
    public static final String GUI_SHADER_VERT = "src/main/resources/shaders/gui.vert";
    public static final String GUI_SHADER_FRAG = "src/main/resources/shaders/gui.frag";

    //GUI字体
    public static final String GUI_FONT_PATH = "src/main/resources/font/MineCraft.otf";

    //天空盒平面
    public static final String SKYBOX_QUAD = "src/main/resources/models/quad/quad.obj";

    //天空盒本体
    public static final String SKYBOX_MODULE = "src/main/resources/models/skybox/skybox.obj";

    //天空盒着色器
    public static final String SKYBOX_SHADER_VERT = "src/main/resources/shaders/skybox.vert";
    public static final String SKYBOX_SHADER_FRAG = "src/main/resources/shaders/skybox.frag";

    //音频
    public static final String MINECRAFT_SOUND1 = "src/main/resources/sound/Minecraft.ogg";

    //光标
    public static final String CURSOR_VERT = "src/main/resources/shaders/cursor.vert";
    public static final String CURSOR_FRAG = "src/main/resources/shaders/cursor.frag";

    public static class MCWindowsOptions{
        public boolean compatibleProfile;//是否使用旧版本函数，此处无用
        public int fps;//这个调了之后移动会出问题，没时间改了
        public int ups = 30;
        public int height;
        public int width;
    }

    public static final int MAP_SIZE_X = 60;
    public static final int MAP_SIZE_Y = 60;
    public static final int MAP_SIZE_Z = 50;

    public static void bugCheck() {}
    public static void lightGUIControl() {}
}
