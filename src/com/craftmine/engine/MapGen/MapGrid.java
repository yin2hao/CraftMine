package com.craftmine.engine.MapGen;

import com.craftmine.gameBlock.MCBlock;
import com.craftmine.gameBlock.MCGrassBlock;
import com.craftmine.gameBlock.MCSandBlock;
import com.craftmine.gameBlock.MCStoneBlock;

import java.util.concurrent.BlockingDeque;

// 定义一个三维网格类，用于管理Minecraft风格的方块世界
public class MapGrid {

    protected MCBlock[][][] blockMap;// 三维数组，存储所有方块对象
    public final int lx, ly, lz;// 网格的尺寸（长、高、宽）

    // 构造函数：初始化指定尺寸的网格
    public MapGrid(int lx, int ly, int lz){
        blockMap = new MCBlock[this.lx=lx][this.ly=ly][this.lz=lz];
    }

    // 放置一个方块
    public MCBlock setBlock(int x, int y, int z, MCBlock block) {
        if (!ifBlockInBounds(x, y, z)) return null;
        MCBlock old = blockMap[x][y][z];
        blockMap[x][y][z] = block;
        return old;
    }

    // 获取指定坐标的方块
    public MCBlock getBlock(int x, int y, int z){
        if(!ifBlockInBounds(x,y,z)) return null;// 检查坐标是否越界
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

        return getBlock(xx,yy,zz);
    }

    public void destoryBlock(int x, int y, int z) {
        // 设置指定位置的方块为null
        setBlock(x, y, z, null);
    }

    public void addBlock(int x, int y, int z, MCBlock blockToPlace) {
        setBlock(x, y, z, blockToPlace);
    }
}