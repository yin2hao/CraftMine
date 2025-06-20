package com.craftmine.engine;

import com.craftmine.game.GameResources;
import com.craftmine.gameBlock.MCBlock;
import com.craftmine.engine.MapGen.MapGrid;
import org.joml.Vector3f;

public class MCPerson {
    private MapGrid mapGrid;
    private Vector3f position;

    public static final double PLAYER_HEIGHT = GameResources.PLAYER_HEIGHT;
    public static final double PLAYER_WIDTH = GameResources.PLAYER_WIDTH;
    public MCPerson(MapGrid mapGrid) {
        this.mapGrid = mapGrid;
        this.position = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    // 检查指定位置是否发生碰撞
    public boolean collide(double x, double y, double z) {
        // 计算玩家边界框
        double minX = x - PLAYER_WIDTH/10;
        double maxX = x + PLAYER_WIDTH/10;
        double minZ = z - PLAYER_WIDTH/10;
        double maxZ = z + PLAYER_WIDTH/10;
        double minY = y - PLAYER_HEIGHT/2 - PLAYER_HEIGHT/4;
        double maxY = y + PLAYER_HEIGHT/2 - PLAYER_HEIGHT/4;

        // 计算需要检查的方块范围（转换为方块坐标）
        double startX = Math.floor(minX / MCBlock.SIDE);
        double endX = Math.ceil(maxX / MCBlock.SIDE);
        double startY = Math.floor(minY / MCBlock.SIDE);
        double endY = Math.ceil(maxY / MCBlock.SIDE);
        double startZ = Math.floor(minZ / MCBlock.SIDE);
        double endZ = Math.ceil(maxZ / MCBlock.SIDE);
//        System.out.println("碰撞检测范围: X[" + startX + "," + endX + "] Y[" + startY + "," + endY + "] Z[" + startZ + "," + endZ + "]");

        // 遍历可能碰撞的方块
        for (double bx = startX; bx <= endX; bx++) {
            for (double by = startY; by <= endY; by++) {
                for (double bz = startZ; bz <= endZ; bz++) {
                    // 检查方块边界 - 这行必须取消注释，否则会导致边界外的空间被视为碰撞体
//                    if (!mapGrid.ifBlockInBounds(bx, by, bz)) {
//                        System.out.println("坐标超出地图边界: [" + bx + "," + by + "," + bz + "]");
//                        continue;
//                    }

                    // 如果方块存在，发生碰撞
                    if (mapGrid.getBlock(bx, by, bz) != null) {
                        System.out.println("检测到碰撞 at [" + bx + "," + by + "," + bz + "]");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
