package com.craftmine.game;

import com.craftmine.gameBlock.MCBlock;

public class GameResources {
    //背景图纹理
    public static final String DEFAULT_TEXTURE = "src/main/resources/models/default/default_texture.png";

    public static final String GRASS_MODEL_PATH = "src/main/resources/models/cube/grass_block/grass_block.obj";//草方块纹理
    public static final String STONE_MODEL_PATH = "src/main/resources/models/cube/stone_block/stone_block.obj";
    public static final String SAND_MODEL_PATH = "src/main/resources/models/cube/sand_block/sand_block.obj";
    public static final String LOG1_MODEL_PATH = "src/main/resources/models/cube/log1_block/log1_block.obj";
    public static final String LOG2_MODEL_PATH = "src/main/resources/models/cube/log2_block/log2_block.obj";
    public static final String PLANKS1_MODEL_PATH = "src/main/resources/models/cube/planks1_block/planks1_block.obj";
    public static final String PLANKS2_MODEL_PATH = "src/main/resources/models/cube/planks2_block/planks2_block.obj";
    public static final String PLANKS3_MODEL_PATH = "src/main/resources/models/cube/planks3_block/planks3_block.obj";

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

        //默认分辨率(如果为空，则默认全屏)
//        public int height = 1080;
//        public int width = 1920;
        public int height;
        public int width;
    }

    public static final long DESTROY_DELAY_MS = 500;

    //地图大小
    public static final int MAP_SIZE_X = 75;
    public static final int MAP_SIZE_Y = 75;
    public static final int MAP_SIZE_Z = 75;

    //为防止卡顿，删除y=DELETE_SIZE层以下的方块
    public static final int DELETE_SIZE = 20;

    public static final int SPEED = 2; //玩家移动速度倍率

    //玩家高度和宽度（有bug，这里实际上没用）
    public static final double PLAYER_HEIGHT = MCBlock.SIDE * 1.4;
    public static final double PLAYER_WIDTH = MCBlock.SIDE * 0.6;

    public static void bugCheck() {}
    public static void lightGUIControl() {}
}
