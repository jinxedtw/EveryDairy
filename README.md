### 介绍

:smile:时刻是一个使用纯kotlin和Jetpack实现的具有简单增删改查功能的日记App

### **效果图**

<img src="E:\FileRecv\MobileFile\Screenshot_2021-09-11-21-20-30-07_f91078b412684ef.jpg" alt="开始页面" style="zoom:25%;" />

<img src="E:\FileRecv\MobileFile\Screenshot_2021-09-11-21-21-14-47_f91078b412684ef.jpg" alt="Screenshot_2021-09-11-21-21-14-47_f91078b412684ef" style="zoom:25%;" />

<img src="E:\FileRecv\MobileFile\Screenshot_2021-09-11-22-12-23-24_f91078b412684ef.jpg" alt="Screenshot_2021-09-11-22-12-23-24_f91078b412684ef" style="zoom:25%;" />

<img src="E:\FileRecv\MobileFile\Screenshot_2021-09-11-21-24-17-98_f91078b412684ef.jpg" alt="Screenshot_2021-09-11-21-24-17-98_f91078b412684ef" style="zoom:25%;" />

<img src="E:\FileRecv\MobileFile\Screenshot_2021-09-11-21-53-03-08.jpg" alt="Screenshot_2021-09-11-21-53-03-08" style="zoom:25%;" />

<img src="E:\FileRecv\MobileFile\Screenshot_2021-09-11-21-29-45-34_f91078b412684ef.jpg" alt="Screenshot_2021-09-11-21-29-45-34_f91078b412684ef" style="zoom:25%;" />



### 使用的相关技术**

* DataBinding :省去了繁琐的findViewById工作，而且通过和ViewMode配合,可以直接把ViewModel的数据渲染到界面上
* Paging：分页加载库
  [郭神的博客](https://guolin.blog.csdn.net/article/details/114707250)
* Room:封装了sqlite，将表映射成Java或Kotlin对象，我觉得相比于之前使用的greendao，room最大的优势是支持了kotlin的flow，通过和paging配合，实现自动的刷新数据[链接](https://www.jianshu.com/p/3e358eb9ac43)
* dataStore：用来代替SP的数据持久化方案，最大的优势是支持异步获取保存数据，通过一个PreferencesKey保存对应类型泛型的方法保证了类型安全
* ViewModel：用于保存actvity的数据中转
* Glide：图片加载，这个太牛皮了就不介绍了
* liveData：代替EventBus用于进行事件分发



### 最后

github地址：[LongerRelationShip](https://github.com/jinxedtw/LongerRelationShip)

有兴趣的大佬可以下载提提建议
[时刻下载地址](https://www.pgyer.com/HkJI)
![请添加图片描述](https://img-blog.csdnimg.cn/cb987ddbf92146b8820f57dd4be98179.png)
后期开发功能

* 记账功能
* 录音笔记功能
* App的图库功能
* 日记的收藏和设置私密

###### 感谢

* 界面设计很大部分参考了该设计
  [美贴 记事原型](https://modao.cc/community/mtkb3iar4hsj965b?title=%E7%BE%8E%E8%B4%B4-%E8%AE%B0%E4%BA%8B%E5%8E%9F%E5%9E%8B)
* 代码的学习参考
  [Eyepetizer](https://github.com/VIPyinzhiwei/Eyepetizer)
  官方的代码示例
  [Sunflower](https://github.com/android/sunflower)