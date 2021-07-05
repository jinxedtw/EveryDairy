package com.tw.longerrelationship.logic.network

import com.tw.longerrelationship.logic.model.ArticleElement
import com.tw.longerrelationship.logic.model.ArticleHistory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 包含请求的一些接口
 *
 * @Path 作用是把参数填到Url里面去
 */
interface AppRequestService {
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getPublicArticle(@Path("id") id: Int, @Path("page") page: Int): ArticleHistory?
}