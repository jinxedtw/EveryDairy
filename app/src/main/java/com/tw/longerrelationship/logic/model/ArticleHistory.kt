package com.tw.longerrelationship.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey


data class ArticleHistory(
    val data: Data,
    val errorCode: Long,
    val errorMsg: String
)

data class Data(
    val curPage: Long,
    val datas: List<ArticleElement>,
    val offset: Long,
    val over: Boolean,
    val pageCount: Long,
    val size: Long,
    val total: Long
)

@Entity
data class ArticleElement(
    @PrimaryKey
    val id: Long,

    val apkLink: String,
    val audit: Long,
    val author: String,
    val canEdit: Boolean,
    val chapterID: Long,
    val chapterName: String,
    val collect: Boolean,
    val courseID: Long,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    val host: String,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterID: Long,
    val selfVisible: Long,
    val shareDate: Long,
    val shareUser: String,
    val superChapterID: Long,
    val superChapterName: String,
    val tags: List<Tag>,
    val title: String,
    val type: Long,
    val userID: Long,
    val visible: Long,
    val zan: Long
)

@Entity
data class Tag(
    @PrimaryKey
    val name: String,
    val url: String
)