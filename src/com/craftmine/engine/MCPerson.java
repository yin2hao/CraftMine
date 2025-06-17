package com.craftmine.engine;

import com.craftmine.gameBlock.MCBlock;
import com.craftmine.engine.MapGen.MapGrid;
import org.joml.Vector3f;

public class MCPerson {
    private MapGrid mapGrid;
    private Vector3f position;

    public static final double HEIGHT = MCBlock.SIDE * 1.6;
    public static final double WIDTH = MCBlock.SIDE * 0.5;

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
        double minX = x - WIDTH/2;
        double maxX = x + WIDTH/2;
        double minY = y;
        double maxY = y + HEIGHT;
        double minZ = z - WIDTH/2;
        double maxZ = z + WIDTH/2;

        // 计算需要检查的方块范围（转换为方块坐标）
        int startX = (int)Math.floor(minX / MCBlock.SIDE);
        int endX = (int)Math.ceil(maxX / MCBlock.SIDE);
        int startY = (int)Math.floor(minY / MCBlock.SIDE);
        int endY = (int)Math.ceil(maxY / MCBlock.SIDE);
        int startZ = (int)Math.floor(minZ / MCBlock.SIDE);
        int endZ = (int)Math.ceil(maxZ / MCBlock.SIDE);
        System.out.println("碰撞检测范围: X[" + startX + "," + endX + "] Y[" + startY + "," + endY + "] Z[" + startZ + "," + endZ + "]");

        // 遍历可能碰撞的方块
        for (int bx = startX; bx <= endX; bx++) {
            for (int by = startY; by <= endY; by++) {
                for (int bz = startZ; bz <= endZ; bz++) {
                    // 检查方块边界
                    if (!mapGrid.ifBlockInBounds(bx, by, bz)) continue;

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
