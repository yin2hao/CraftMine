package com.craftmine.engine.MapGen;

import java.util.*;

import com.craftmine.game.Minecraft;
import com.craftmine.gameBlock.MCBlock;

//已验证完成，没有问题（应该？）
public class MCMapGen {

    private int LX,LY,LZ;//设置三维
    private MapGrid grid;//存方块数据
    public MCMapGen(int lx, int ly, int lz) {
        LX = lx;
        LY = ly;
        LZ = lz;
    }//转换

    public MapGrid getGrid(){
        return grid;
    }//返回方块设计

    public void genMap() {
        grid = new MapGrid(LX, LY, LZ); // 初始化网格
        int[][] heights = new int[LX][LZ];
        double[][] n1 = perlinNoise(randGrid(LX, LZ), (int) (6 + 2 * Math.random()), 0.4 + 0.15 * Math.random());
        double[][] n2 = perlinNoise(randGrid(LX, LZ), (int) (14 + 3 * Math.random()), 0.3 + 0.15 * Math.random());

        int minh = Integer.MAX_VALUE, maxh = Integer.MIN_VALUE, Gaoduzhi = 0;

        for (int i = 0; i < LX; i++) {
            for (int j = 0; j < LZ; j++) {
                // 合并两个噪声，归一化到 [0, 1]
                double hval = (n1[i][j] + n2[i][j]) / 2.0;
                hval = (hval + 1.0) / 2.0; // 如果噪声值在 [-1, 1]，先归一化

                // 映射到高度值
                int height = (int)(hval * (LZ - 1));
                height = Math.max(0, Math.min(LZ - 1, height)); // 限制在合法范围内

                heights[i][j] = height;
                minh = Math.min(minh, height);
                maxh = Math.max(maxh, height);
                Gaoduzhi += height;
            }
        }

        int G = ((Gaoduzhi / (LX * LZ) - minh) * 2 / 3 + minh);
        for (int x = 0; x < LX; x++) {
            for (int y = 0; y < LY; y++) {
                // 确保高度值有效
                int height = Math.min(LZ - 1, Math.max(0, heights[x][Math.min(LZ - 1, y)]));

                if (height > G + 2) {
                    grid.setBlock(x, y, height, loadBlock('b', x, y, height));
                    for (int z = height - 1; z >= 0; z--) {
                        char blockType = (height - z) / 10.0 < Math.random() ? 'd' : 't';
                        grid.setBlock(x, y, z, loadBlock(blockType, x, y, z));
                    }
                } else {
                    for (int z = height; z >= 0 && z > height - 10; z--) {
                        grid.setBlock(x, y, z, loadBlock('s', x, y, z));
                    }
                    for (int z = height - 10; z >= 0; z--) {
                        char blockType = (height - z) / 10.0 < Math.random() ? 'd' : 't';
                        grid.setBlock(x, y, z, loadBlock(blockType, x, y, z));
                    }
                }
            }
        }
    }


    /**
     * 洞穴生成算法
     * @param LX X轴大小
     * @param LY Y轴大小
     * @param LZ Z轴大小
     * @param minh 最小高度
     * @param maxh 最大高度
     * @param totc 洞穴方块总数
     * @return 洞穴位置标记数组
     */
    boolean[][][] cave(int LX, int LY, int LZ, int minh, int maxh, int totc) {
        boolean[][][] m = new boolean[LX][LY][LZ];
        boolean[][][] vis = new boolean[LX][LY][LZ];
        int[][][] ct = new int[LX][LY][LZ];

        int diff = maxh - minh;
        int tot = 0;
        int seeds = 0;
        final int DR = 2;
        double DDR = Math.pow(DR * 2 + 1, 3);

        // 洞穴生成主循环
        outer:
        while (tot < totc) {
            // 随机选择起始点
            int x = (int) (LX * Math.random());
            int y = (int) (LY * Math.random());
            int z = (int) (maxh * Math.random());
            double r = 1 + 1 * Math.random();
            seeds++;

            // 洞穴生长
            while (tot < totc && r > 0) {
                // 生成球形洞穴
                r += 0.75 * (Math.random() - 0.5);
                /*使用三重循环遍历球形范围内的所有点 (a, b, c)，判断是否满足球形条件
                 ((a - x)² + (b - y)² + (c - z)² < trr)，并且该点未被访问过。如果满足条件，则设置 m[a][b][c] = true，表示这个点是洞穴的一部分。*/
                double trr = r * r;
                int rr = (int) Math.ceil(r);
                for (int a = x - rr; a <= x + rr; a++) {
                    for (int b = y - rr; b <= y + rr; b++) {
                        for (int c = z - rr; c <= z + rr; c++) {
                            if ((a - x) * (a - x) + (b - y) * (b - y) + (c - z) * (c - z) < trr
                                    && a >= 0 && b >= 0 && a < LX && b < LY && c >= 0 && c < LZ
                                    && !m[a][b][c]) {
                                m[a][b][c] = true;
                                // 更新周围影响计数
                                for (int aa = a - DR; aa <= a + DR; aa++) {
                                    for (int bb = b - DR; bb <= b + DR; bb++) {
                                        for (int cc = c - DR; cc <= c + DR; cc++) {
                                            if (aa >= 0 && bb >= 0 && aa < LX && bb < LY
                                                    && cc >= 0 && cc < LZ) {
                                                ct[aa][bb][cc]++;
                                                /*更新周围点的计数，ct 数组用来存储周围的影响值。DR 是一个常量（2），表示影响范围，ct[aa][bb][cc]++ 用来
                                                增加周围邻域点的影响值。
                                                tot++ 表示生成了一个新的洞穴单元。*/
                                            }
                                        }
                                    }
                                }
                                tot++;
                            }
                        }
                    }
                }

                // 选择下一个生长方向
                ArrayList<double[]> prob = new ArrayList<>();
                double totprob = 0;
                for (int a = x - 1; a <= x + 1; a++) {
                    for (int b = y - 1; b <= y + 1; b++) {
                        for (int c = z - 1; c <= z + 1; c++) {
                            if (a >= 0 && b >= 0 && a < LX && b < LY && c >= 0 && c < LZ) {
                                double pp = (DDR - ct[a][b][c]) / DDR;
                                pp *= pp;
                                totprob += pp;
                                prob.add(new double[]{a, b, c, pp});
                            }
                        }
                    }
                }

                // 随机选择生长方向
                double rand = Math.random(), cur = 0;
                for (double[] dr : prob) {
                    if (rand < cur + dr[3] / totprob) {
                        x = (int) dr[0];
                        y = (int) dr[1];
                        z = (int) dr[2];
                        break;
                    }
                    cur += dr[3] / totprob;
                }

                if (vis[x][y][z]) break;
                vis[x][y][z] = true;
            }
        }
//        boolean[][][] caveMask = cave(LX, LY, LZ, minh, maxh, LX * LY * LZ / 25); // 洞穴体积比例，可调
//
//// 根据掩码“挖空”地形，生成洞穴
//        for (int x = 0; x < LX; x++) {
//            for (int y = 0; y < LY; y++) {
//                for (int z = 0; z < LZ; z++) {
//                    if (caveMask[x][y][z]) {
//                        grid.setBlock(x, y, z, null); // 设置为空气（或使用 loadBlock('0', ...) 代表空气）
//                    }
//                }
//            }
//        }

        System.out.println("Generated " + seeds + " cave seeds");
        return m;
    }
    /**
     * 平滑噪声
     * @param b 基础噪声
     * @param o 八度数
     * @return 平滑后的噪声
     */
    double[][]  smoothNoise (double[][] b, int o) {
        int row = b.length;
        int col = b[0].length;
        double[][] m = new double[row][col];

        int p = 1 << o;
        double f = 1.0 / p;

        for(int i = 0; i < row; i++) {
            int i0 = i >> o << o;
            int i1 = Math.min(i0 + p, row - 1);
            double hBlend = (i - i0) * f;

            for(int j = 0; j < col; j++) {
                int j0 = j >> o << o;
                int j1 = Math.min(j0 + p, col - 1);
                double vBlend = (j - j0) * f;

                double top = interpolate(b[i0][j0], b[i1][j0], hBlend);
                double bottom = interpolate(b[i0][j1], b[i1][j1], hBlend);
                m[i][j] = interpolate(top, bottom, vBlend);
            }
        }

        return m;
    }

    /**
     * Perlin噪声生成
     * @param b 基础噪声
     * @param o 八度数
     * @param p 持久度
     * @return Perlin噪声
     */
    public double[][] perlinNoise(double[][] b, int o, double p) {
        int row = b.length;
        int col = b[0].length;
        double[][][] sn = new double[o][][];

        // 生成多层噪声
        for(int i = 0; i < o; i++) {
            sn[i] = smoothNoise(b, i);
        }

        // 叠加噪声层
        double[][] pn = new double[row][col];
        double amp = 1;
        double tot = 0;

        for(int oo = o - 1; oo >= 0; oo--) {
            amp *= p;
            tot += amp;
            for(int i = 0; i < row; i++) {
                for(int j = 0; j < col; j++) {
                    pn[i][j] += sn[oo][i][j] * amp;
                }
            }
        }

        // 归一化
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                pn[i][j] /= tot;
            }
        }

        return pn;
    }
    /**
     * 线性插值
     * @param x0 起点值
     * @param x1 终点值
     * @param alpha 插值因子
     * @return 插值结果
     */
    double interpolate(double x0, double x1, double alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    /**
     * 生成随机噪声网格
     * @param row 行数
     * @param col 列数
     * @return 随机噪声网格
     */
    double[][] randGrid(int row, int col) {
        double[][] m = new double[row][col];
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                m[i][j] = Math.random();
            }
        }
        return m;
    }

    /**
     * 加载方块
     * @param c 方块类型字符
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     * @return 方块对象
     */
    public static MCBlock loadBlock(char c, int x, int y, int z) {
        return Minecraft.loadBlock(c, x, y, z);
    }
}