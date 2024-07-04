package org.worker996.kotlinsprintbootpd.article

interface CustomArticleRepository {
    fun countArticles(tag: String?, author: String?, favorited: String?): Long
}