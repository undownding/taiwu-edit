## 太吾绘卷 - 存档修改器

version 1.0.1 for 2.6.x

### 开发指南

下载 [intellij idea](https://www.jetbrains.com/idea/download/) (建议通过 [Toolbox App](https://www.jetbrains.com/toolbox-app/) 安装)

将本项目作为 gradle 项目导入

导入成功后点开右侧的 gradle 界面，选择 linkDebugExecutableNative 任务后到 build 目录找到 exe 并执行。

（其实对脚本进行小量修改可以支持 Mac 和 Linux，但是觉得没必要）

不要直接点 run，intellij 的 run terminal 似乎不能支持键盘的 read 事件。
