package com.tw.longerrelationship.logic.model

/** 相册实体类 */
data class ImageFolder(
    var count: Int = 0,              // 文件夹下的图片个数
    var firstImagePath: String = "", // 第一张图片的路径 传这个给小相册图片显示
    var dir: String = "",            // 文件夹路径
    var name: String = "",           // 文件夹的名字
    var imageUrl: List<String> = mutableListOf()       // 相册下所有图片的路径
)
