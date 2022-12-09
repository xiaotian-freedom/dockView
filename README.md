# dockView
一款可以四处游动、边缘碰撞检测、跟随手指拖动且能够停靠在侧边栏的有趣小组件

# 使用方式:
XML 中添加如下代码
```
<com.storn.viewlib.BubbleTickView
        android:id="@+id/ivTick"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_gravity="bottom|end" />
```
组件的宽高可随意设置。

# 使用链式调用启动图片游动模式

```
binding.ivTick.post {
            binding.ivTick.Builder()
                .setX(binding.ivTick.x)
                .setY(binding.ivTick.y)
                .setWidth(SizeUtils.dp2px(100f))
                .setHeight(SizeUtils.dp2px(100f))
                .setMaxWidth(binding.rootLayout.width)
                .setMaxHeight(binding.rootLayout.height)
                .setDozeDuration(3)
                .setShowDockDrawable(true)
                .setDockDrawable(dock)
                .setOriginDrawable(origin)
                .move()
        }
```


# 在java代码中有如下方法用于控制组件的行为

* hideToSide: 用于隐藏到侧边栏
* dockToSide: 用于停靠在侧边栏
* resetSwimming: 用于设置图片重新游动

# 游动的图片可添加点击事件：
```
binding.ivTick.setOnClickListener {
            Toast.makeText(this, "我被电击了，you can do something in here", Toast.LENGTH_SHORT).show()
        }
```


# 静态效果图
![1](https://github.com/xiaotian-freedom/dockView/blob/main/preview/Screenshot_20221209_093559.png)
![2](https://github.com/xiaotian-freedom/dockView/blob/main/preview/Screenshot_20221209_093633.png)
![3](https://github.com/xiaotian-freedom/dockView/blob/main/preview/Screenshot_20221209_093650.png)
![4](https://github.com/xiaotian-freedom/dockView/blob/main/preview/Screenshot_20221209_093700.png)

# 动态效果图
![](https://github.com/xiaotian-freedom/dockView/blob/main/preview/c31b4ed4-484d-4c87-8f8a-ab8ad7174c47.gif)
