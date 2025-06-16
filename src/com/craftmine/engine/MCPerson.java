package com.craftmine.engine;

import com.craftmine.gameBlock.MCBlock;
import com.craftmine.engine.MapGen.MapGrid;
import org.joml.Vector3f;

import static com.craftmine.game.GameResources.*;

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
        int endX = (int)Math.ceil(maxX / MCBlock.SIDE) - 1;
        int startY = (int)Math.floor(minY / MCBlock.SIDE);
        int endY = (int)Math.ceil(maxY / MCBlock.SIDE) - 1;
        int startZ = (int)Math.floor(minZ / MCBlock.SIDE);
        int endZ = (int)Math.ceil(maxZ / MCBlock.SIDE) - 1;

        // 遍历可能碰撞的方块
        for (int bx = startX; bx <= endX; bx++) {
            for (int by = startY; by <= endY; by++) {
                for (int bz = startZ; bz <= endZ; bz++) {
                    // 检查方块边界
                    if (!mapGrid.ib(bx, by, bz)) continue;

                    // 如果方块存在，发生碰撞
                    if (mapGrid.get(bx, by, bz) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 尝试移动，如果没有碰撞则更新位置
    public boolean tryMove(Vector3f newPosition) {
        if (!collide(newPosition.x, newPosition.y, newPosition.z)) {
            position.set(newPosition);
            return true;
        }
        return false;
    }

    // 滑动碰撞：先尝试X轴移动，再尝试Y轴，最后尝试Z轴
    public Vector3f slideCollision(Vector3f currentPos, Vector3f targetPos) {
        Vector3f result = new Vector3f(currentPos);

        // 尝试X轴移动
        Vector3f temp = new Vector3f(targetPos.x, currentPos.y, currentPos.z);
        if (!collide(temp.x, temp.y, temp.z)) {
            result.x = temp.x;
        }

        // 尝试Y轴移动
        temp.set(result.x, targetPos.y, currentPos.z);
        if (!collide(temp.x, temp.y, temp.z)) {
            result.y = temp.y;
        }

        // 尝试Z轴移动
        temp.set(result.x, result.y, targetPos.z);
        if (!collide(temp.x, temp.y, temp.z)) {
            result.z = temp.z;
        }

        return result;
    }
}