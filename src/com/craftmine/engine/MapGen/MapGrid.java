package com.craftmine.engine.MapGen;

import com.craftmine.gameBlock.MCBlock;

// 定义一个三维网格类，用于管理Minecraft风格的方块世界
public class MapGrid {

    protected MCBlock[][][] blockMap;// 三维数组，存储所有方块对象
    public final int lx, ly, lz;// 网格的尺寸（长、宽、高）

    // 构造函数：初始化指定尺寸的网格
    public MapGrid(int lx, int ly, int lz){
        blockMap = new MCBlock[this.lx=lx][this.ly=ly][this.lz=lz];
    }

    // 在指定坐标设置方块，返回被替换的旧方块
    public MCBlock setBlock(int x, int y, int z, MCBlock MCBlock){

        if(!ifBlockInBounds(x,y,z)) return null;// 检查坐标是否越界方块
        MCBlock currentBlock = blockMap[x][y][z];// 尝试获取当前位置的方块
        blockMap[x][y][z] = MCBlock;// 设置新方块
        System.out.println("设置方块: " + x + "," + y + "," + z + " 为 " + (MCBlock == null ? "null" : MCBlock.getClass().getSimpleName()));
        return currentBlock;
    }

    // 获取指定坐标的方块
    public MCBlock getBlock(int x, int y, int z){

        if(!ifBlockInBounds(x,y,z)) return null;// 检查坐标是否越界
        return blockMap[x][y][z];
    }

    // 通过世界坐标获取方块（将世界坐标转换为网格坐标）
    // 此方法暂时无用
    public MCBlock getBlock(double x, double y, double z){
        // 将世界坐标除以方块边长得到网格坐标
        int xx = (int)(x / MCBlock.SIDE);
        int yy = (int)(y / MCBlock.SIDE);
        int zz = (int)(z / MCBlock.SIDE);

        return getBlock(xx,yy,zz);
    }

    // 检查坐标是否在网格范围内
    public boolean ifBlockInBounds(int x, int y, int z){
        return !(x<0 || y<0 || z<0 || x>=lx || y>=ly || z>=lz);
    }

//    // 网格渲染器实例
//    protected MCGridRender r;
//
//    // 六个方向的x偏移量（左、右、前、后、上、下）
//    int[] mx = {-1,1,0,0,0,0};
//    int[] my = {0,0,-1,1,0,0};
//    int[] mz = {0,0,0,0,-1,1};
//
//    // 六个方向对应的方块面常量
//    int[] sides = { MCBlock.RIGHT, MCBlock.LEFT,
//            MCBlock.BACK, MCBlock.FRONT,
//            MCBlock.TOP, MCBlock.BOT};
//
//    // 六个方向的反方向对应的方块面常量
//    int[] oppSides = { MCBlock.LEFT, MCBlock.RIGHT,
//            MCBlock.FRONT, MCBlock.BACK,
//            MCBlock.BOT, MCBlock.TOP};
//
//    // 可见性标记数组，记录哪些方块已被处理
//    boolean vis[][][];
//
//    // 主渲染方法，传入玩家对象
//    public void render( MCPerson p ){
//        // 获取玩家的视线方向向量
//        double[] vec = p.toVec3D();
//        // 设置视平面位置（玩家前方20单位处）
//        plane.setPlane(p.x+(vec[0]*-20),p.y+(vec[1]*-20),p.z+(vec[2]*-20), vec);
//
//        // 如果渲染器未初始化
//        if(r == null){
//            // 创建新的渲染器实例
//            r = new MCGridRender();
//
//            // 如果可见性数组未初始化
//            if (vis == null)
//                vis = new boolean[blockMap.length][blockMap[0].length][blockMap[0][0].length];
//
//            // 计算玩家所在的网格坐标
//            int x = (int)(p.x/MCBlock.SIDE);
//            int y = (int)(p.y/MCBlock.SIDE);
//            int z = (int)(p.z/MCBlock.SIDE);
//            // 边界检查
//            if(x < 0) x = 0;
//            if(x >= lx) x = lx-1;
//            if(y < 0) y = 0;
//            if(y >= ly) y = ly-1;
//            if(z < 0) z = 0;
//            if(z >= lz) z = lz-1;
//
//            // 从玩家位置开始洪水填充计算可见性
//            flood(x,y,z);
//        }
//
//        // 执行实际渲染
//        realRender(p, plane);
//    }
//
//    // 实际渲染方法（可被子类覆盖）
//    public void realRender( MCPerson p, MCPlane plane){
//        // 调用渲染器的渲染方法，false表示不是特殊渲染
//        r.render(plane, p, false);
//    }
//
//    // 移除方块的渲染
//    public void unrender( MCBlock b ){
//        // 从渲染器中移除该方块的所有四边形面
//        for(MCTexturedQuad q : b.getQuads())
//            r.remove(q);
//
//        // 计算方块所在的网格坐标
//        int x = (int)(b.x/MCBlock.SIDE);
//        int y = (int)(b.y/MCBlock.SIDE);
//        int z = (int)(b.z/MCBlock.SIDE);
//        // 从网格中移除该方块
//        setBlock(x,y,z, null);
//
//        // 重新计算该位置的可见性
//        flood(x,y,z);
//    }
//
//    // 添加方块到网格
//    public void add( MCBlock b ){
//        // 计算方块所在的网格坐标
//        int x = (int)(b.x/MCBlock.SIDE);
//        int y = (int)(b.y/MCBlock.SIDE);
//        int z = (int)(b.z/MCBlock.SIDE);
//
//        // 检查六个相邻方向
//        for(int i = 0; i<mx.length; i++){
//            // 计算相邻坐标
//            int tx = x +mx[i];
//            int ty = y +my[i];
//            int tz = z +mz[i];
//
//            // 获取相邻方块
//            MCBlock bb = getBlock(tx,ty,tz);
//            if( bb == null)
//                // 如果相邻位置为空，添加当前方块的对应面
//                r.add( b.getQuads()[oppSides[i]] );
//            else
//                // 如果相邻位置有方块，移除相邻方块的对应面
//                r.remove( bb.getQuads()[sides[i]] );
//        }
//
//        // 将方块放入网格
//       setBlock(x,y,z, b);
//    }
//
//    // 洪水填充算法，计算从(x,y,z)开始的可见方块
//    public void flood(int x, int y, int z){
//        // 使用队列实现BFS（广度优先搜索）
//        Queue<int[]> que = new LinkedList<int[]>();
//        que.add(new int[]{x,y,z});
//
//        while(!que.isEmpty()){
//            // 从队列中取出一个坐标
//            int[] c = que.remove();
//            int xx = c[0]; int yy = c[1]; int zz = c[2];
//
//            // 如果当前位置是空的
//            if(blockMap[xx][yy][zz] == null)
//                // 检查六个方向
//                for(int i = 0; i<mx.length; i++){
//                    // 计算相邻坐标
//                    int tx = xx +mx[i];
//                    int ty = yy +my[i];
//                    int tz = zz +mz[i];
//
//                    // 检查相邻坐标是否有效
//                    if(ifBlockInBounds(tx,ty,tz)){
//                        // 如果相邻位置有方块
//                        if(blockMap[tx][ty][tz] != null)
//                            // 将该方块的对应面添加到渲染器
//                            r.add(blockMap[tx][ty][tz].getQuads()[sides[i]]);
//
//                        // 如果该位置已经处理过则跳过
//                        if(vis[tx][ty][tz])
//                            continue;
//
//                        // 将相邻坐标加入队列
//                        que.add(new int[]{tx,ty,tz});
//                        // 标记为已处理
//                        vis[tx][ty][tz] = true;
//                    }
//                }
//        }
//    }
}