package com.craftmine.engine.MapGen;

import java.util.ArrayList;
import java.util.Random; // 使用Random实例以获得更好的随机性
import com.craftmine.game.Minecraft;
import com.craftmine.gameBlock.MCBlock;

// 已修正：恢复Z轴为高度，并修复了所有相关逻辑
public class MCMapGen {

    // 坐标系定义: LX: 宽度 (X轴), LY: 深度 (Y轴), LZ: 高度 (Z轴)
    private int LX, LY, LZ;
    private MapGrid grid;
    private Random rand = new Random();

    public MCMapGen(int lx, int ly, int lz) {
        LX = lx;
        LY = ly;
        LZ = lz;
    }

    public MapGrid getGrid() {
        return grid;
    }

    public void genMap() {
        grid = new MapGrid(LX, LY, LZ);

        // --- 1. 生成基础地形高度图 (基于X/Y平面) ---
        int[][] heights = new int[LX][LY];
        // 修正: 柏林噪声应该基于X/Y平面生成
        double[][] n1 = perlinNoise(randGrid(LX, LY), (int) (6 + 2 * rand.nextDouble()), 0.4 + 0.15 * rand.nextDouble());
        double[][] n2 = perlinNoise(randGrid(LX, LY), (int) (14 + 3 * rand.nextDouble()), 0.3 + 0.15 * rand.nextDouble());

        int minh = Integer.MAX_VALUE, maxh = Integer.MIN_VALUE;
        long Gaoduzhi = 0;

        // 修正: 遍历X和Y轴来计算每个水平坐标点的高度(Z值)
        for (int x = 0; x < LX; x++) {
            for (int y = 0; y < LY; y++) {
                double hval = (n1[x][y] + n2[x][y]) / 2.0;
                hval = (hval + 1.0) / 2.0; // 归一化到 [0, 1]

                // 修正: 高度应该映射到Z轴 (LZ)，因为Z是垂直轴
                int height = (int) (hval * (LZ - 1));
                height = Math.max(0, Math.min(LZ - 1, height));

                heights[x][y] = height;
                minh = Math.min(minh, height);
                maxh = Math.max(maxh, height);
                Gaoduzhi += height;
            }
        }

        // --- 2. 根据高度图填充方块 ---
        int G = (int) (((Gaoduzhi / (long)(LX * LY)) - minh) * 2 / 3 + minh);

        // 修正: 遍历X和Y平面，并沿Z轴垂直填充
        for (int x = 0; x < LX; x++) {
            for (int y = 0; y < LY; y++) {
                int height = heights[x][y];

                if (height > G + 2) {
                    grid.setBlock(x, y, height, loadBlock('b', x, y, height));
                    for (int z = height - 1; z >= 0; z--) { // 沿Z轴向下填充
                        char blockType = (height - z) / 10.0 < rand.nextDouble() ? 'd' : 't';
                        grid.setBlock(x, y, z, loadBlock(blockType, x, y, z));
                    }
                } else {
                    for (int z = height; z >= 0 && z > height - 10; z--) {
                        grid.setBlock(x, y, z, loadBlock('s', x, y, z));
                    }
                    for (int z = height - 10; z >= 0; z--) {
                        char blockType = (height - z) / 10.0 < rand.nextDouble() ? 'd' : 't';
                        grid.setBlock(x, y, z, loadBlock(blockType, x, y, z));
                    }
                }
            }
        }

        // --- 3. 生成洞穴并挖空方块 ---
        System.out.println("Generating cave mask...");
        // 修正: cave方法参数适配 X-width, Y-depth, Z-height
        boolean[][][] caveMask = cave(LX, LY, LZ, minh, maxh, LX * LY * LZ / 30);
        System.out.println("Carving caves...");

        // 修正: 遍历所有维度挖空方块
        for (int x = 0; x < LX; x++) {
            for (int y = 0; y < LY; y++) {
                for (int z = 0; z < LZ; z++) {
                    if (caveMask[x][y][z] && grid.getBlock(x, y, z) != null) {
                        grid.setBlock(x, y, z, null);
                    }
                }
            }
        }
        System.out.println("Map generation complete.");
    }

    /**
     * 洞穴生成算法 (适配坐标系: X-width, Y-depth, Z-height)
     * @param width 地图宽度 (X)
     * @param depth 地图深度 (Y)
     * @param height 地图高度 (Z)
     * @param minh 地形最低高度
     * @param maxh 地形最高高度
     * @param totc 洞穴方块总数
     * @return 洞穴位置标记数组
     */
    boolean[][][] cave(int width, int depth, int height, int minh, int maxh, int totc) {
        boolean[][][] m = new boolean[width][depth][height];
        boolean[][][] vis = new boolean[width][depth][height];
        int[][][] ct = new int[width][depth][height];

        int tot = 0;
        int seeds = 0;
        final int DR = 2;
        double DDR = Math.pow(DR * 2 + 1, 3);

        while (tot < totc) {
            int cx = rand.nextInt(width);
            int cy = rand.nextInt(depth);
            int cz = rand.nextInt(height); // 洞穴可以在任何高度开始

            double r = 1 + rand.nextDouble();
            seeds++;

            while (tot < totc && r > 0) {
                r += 0.75 * (rand.nextDouble() - 0.5);
                r = Math.max(0, r);

                double trr = r * r;
                int rr = (int) Math.ceil(r);

                // 遍历球形范围
                for (int ix = cx - rr; ix <= cx + rr; ix++) {
                    for (int iy = cy - rr; iy <= cy + rr; iy++) {
                        for (int iz = cz - rr; iz <= cz + rr; iz++) {
                            if ((ix - cx) * (ix - cx) + (iy - cy) * (iy - cy) + (iz - cz) * (iz - cz) < trr
                                    && ix >= 0 && iy >= 0 && iz >= 0 && ix < width && iy < depth && iz < height
                                    && !m[ix][iy][iz]) {
                                m[ix][iy][iz] = true;
                                tot++;

                                for (int ax = ix - DR; ax <= ix + DR; ax++) {
                                    for (int ay = iy - DR; ay <= iy + DR; ay++) {
                                        for (int az = iz - DR; az <= iz + DR; az++) {
                                            if (ax >= 0 && ay >= 0 && az >= 0 && ax < width && ay < depth && az < height) {
                                                ct[ax][ay][az]++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (tot >= totc) break;

                // 选择下一个生长方向
                ArrayList<double[]> prob = new ArrayList<>();
                double totprob = 0;
                for (int nx = cx - 1; nx <= cx + 1; nx++) {
                    for (int ny = cy - 1; ny <= cy + 1; ny++) {
                        for (int nz = cz - 1; nz <= cz + 1; nz++) {
                            if (nx >= 0 && ny >= 0 && nz >= 0 && nx < width && ny < depth && nz < height) {
                                double pp = (DDR - ct[nx][ny][nz]) / DDR;
                                pp *= pp;
                                totprob += pp;
                                prob.add(new double[]{nx, ny, nz, pp});
                            }
                        }
                    }
                }

                if (totprob == 0) break;

                double randomValue = rand.nextDouble() * totprob;
                double cur = 0;
                boolean moved = false;
                for (double[] dr : prob) {
                    cur += dr[3];
                    if (randomValue < cur) {
                        cx = (int) dr[0];
                        cy = (int) dr[1];
                        cz = (int) dr[2];
                        moved = true;
                        break;
                    }
                }

                if (!moved || vis[cx][cy][cz]) break;
                vis[cx][cy][cz] = true;
            }
        }
        System.out.println("Generated " + seeds + " cave seeds");
        return m;
    }

    // --- Perlin噪声相关方法 (无需修改) ---
    double[][] smoothNoise(double[][] b, int o) {
        int row = b.length;
        int col = b[0].length;
        double[][] m = new double[row][col];
        int p = 1 << o;
        double f = 1.0 / p;
        for (int i = 0; i < row; i++) {
            int i0 = (i >> o) << o;
            int i1 = Math.min(i0 + p, row - 1);
            double hBlend = (i - i0) * f;
            for (int j = 0; j < col; j++) {
                int j0 = (j >> o) << o;
                int j1 = Math.min(j0 + p, col - 1);
                double vBlend = (j - j0) * f;
                double top = interpolate(b[i0][j0], b[i1][j0], hBlend);
                double bottom = interpolate(b[i0][j1], b[i1][j1], hBlend);
                m[i][j] = interpolate(top, bottom, vBlend);
            }
        }
        return m;
    }

    public double[][] perlinNoise(double[][] b, int o, double p) {
        int row = b.length;
        int col = b[0].length;
        double[][][] sn = new double[o][][];
        for (int i = 0; i < o; i++) {
            sn[i] = smoothNoise(b, i);
        }
        double[][] pn = new double[row][col];
        double amp = 1;
        double tot = 0;
        for (int oo = o - 1; oo >= 0; oo--) {
            amp *= p;
            tot += amp;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    pn[i][j] += sn[oo][i][j] * amp;
                }
            }
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                pn[i][j] /= tot;
            }
        }
        return pn;
    }

    double interpolate(double x0, double x1, double alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    double[][] randGrid(int row, int col) {
        double[][] m = new double[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                m[i][j] = rand.nextDouble();
            }
        }
        return m;
    }

    public static MCBlock loadBlock(char c, int x, int y, int z) {
        return Minecraft.loadBlock(c, x, y, z);
    }
}