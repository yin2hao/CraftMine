package com.craftmine.gameBlock;

public class MCBlock {
    // 方块的边长
    public static final double SIDE = 1.0;

    // 方块类型
    private int blockType;

    // 默认构造函数
    public MCBlock() {
        this.blockType = 1; // 默认为普通方块
    }

    // 带类型的构造函数
    public MCBlock(int blockType) {
        this.blockType = blockType;
    }

    // 获取方块类型
    public int getBlockType() {
        return blockType;
    }

    // 判断方块是否为实心（可碰撞）
    public boolean isSolid() {
        return true; // 默认所有方块都是实心的
    }
}