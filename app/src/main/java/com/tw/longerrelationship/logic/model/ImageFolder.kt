package com.tw.longerrelationship.logic.model

/** 相册实体类 */
data class ImageFolder(
    /** Folder name */
    var folderName: String = "",
    /** Thumbnail url */
    var thumbnailImage: String = "",
    /** All files */
    var mAlbumFiles: MutableList<String> = mutableListOf()
)
