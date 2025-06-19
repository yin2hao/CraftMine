package com.craftmine.engine.MapGen;

import com.craftmine.gameBlock.MCBlock;

/**
 * MapGrid：三维方块网格，用于存储地形数据
 */
public class MapGrid {

    protected MCBlock[][][] blockMap; // 方块三维数组
    public final int lx, ly, lz;      // 网格尺寸

    public MapGrid(int lx, int ly, int lz) {
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
        blockMap = new MCBlock[lx][ly][lz];
    }

    // 设置一个方块
    public MCBlock setBlock(int x, int y, int z, MCBlock block) {
        if (!ifBlockInBounds(x, y, z)) return null;
        MCBlock old = blockMap[x][y][z];
        blockMap[x][y][z] = block;
        return old;
    }

    // 获取一个方块
    public MCBlock getBlock(int x, int y, int z) {
        if (!ifBlockInBounds(x, y, z)) return null;
        return blockMap[x][y][z];
    }

    // 判断坐标是否在范围内
    public boolean ifBlockInBounds(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < lx && y < ly && z < lz;
    }

    // 获取完整地图数据
    public MCBlock[][][] getBlockMap() {
        return blockMap;
    }

    // 提供世界坐标获取（暂未启用）
    public MCBlock getBlock(double x, double y, double z) {
        int xx = (int)(x / MCBlock.SIDE);
        int yy = (int)(y / MCBlock.SIDE);
        int zz = (int)(z / MCBlock.SIDE);
        return getBlock(xx, yy, zz);
    }
}
