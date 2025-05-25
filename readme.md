## 预期实现效果
<img src="https://github.com/yin2hao/MineCraftMini/blob/master/img/preview.png?raw=true">

## TodoList
- [x] 时间刻
  - [x] 帧率计算
- [x] 游戏窗口
  - [ ] 光照系统
  - [ ] 地形生成
      - [ ] 柏林噪音计算
      - [ ] 天空盒
      - [x] 方块png蒙版
      - [x] 方块面渲染
  - [ ] 十字光标
  - [x] 鼠标指针动态
  - [x] 键盘映射
- [ ] 方块
  - [ ] 草方块
  - [ ] 石头
  - [ ] 泥土
  - [ ] 树(待定)
    - [ ] ~~工作台及合成表功能(待定)~~
  - [ ] 方块被破坏
  - [x] 方块渲染
- [ ] 实体
	- [ ] 玩家
      - [ ] 活动
          - [x] 移动
          - [ ] 跳跃
          - [ ] 水下移动
          - [ ] 水下跳跃
          - [ ] 飞行模式(待定)
          - [ ] 破坏方块
            - [ ] 计算指针指向方块
            - [ ] 播放破坏动画
          - [ ] 放置方块
            - [ ] 计算指针指向方块
      - [ ] 背包
	- [ ] ~~怪物(待定)~~
	- [ ] ~~掉落物(待定)~~


## 预计使用第三方库
~~[FastNoise Lite](https://github.com/Auburn/FastNoiseLite):便携的开源噪声生成库，具有多种噪声算法~~</br>
[LWJGL](https://www.lwjgl.org/):图形化界面以及各种相关库

如果事不可为，则考虑使用 [jmonkeyengine](https://github.com/jMonkeyEngine/jmonkeyengine)

## 参考资料
[我的世界开发者中文指南](https://github.com/mouse0w0/MinecraftDeveloperGuide?tab=readme-ov-file)</br>
[只用7个命令方块做出柏林噪声生成地形？！](https://www.bilibili.com/video/BV1vfKJedEdA/)</br>
~~[How to Use Perlin Noise in Your Games](http://devmag.org.za/2009/04/25/perlin-noise/)~~ 全英文没看完</br>
~~[《3D Game Development with LWJGL 3》中文翻译](https://mouse0w0.github.io/lwjglbook-CN-Translation/02-the-game-loop/)~~ 过时的翻译</br>
[3D Game Development with LWJGL 3](https://ahbejarano.gitbook.io/lwjglgamedev)的[翻译](https://yin2hao.github.io/lwjglbook-CN-Translation/)
