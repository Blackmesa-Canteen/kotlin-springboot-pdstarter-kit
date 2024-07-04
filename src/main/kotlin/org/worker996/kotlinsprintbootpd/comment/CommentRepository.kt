package org.worker996.kotlinsprintbootpd.comment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.worker996.kotlinsprintbootpd.article.ArticleEntity

@Repository
interface CommentRepository: JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.article = :article ORDER BY c.createdAt ASC")
    fun listByArticle(@Param("article") article: ArticleEntity): List<CommentEntity>

    @Query("SELECT c.author.id FROM CommentEntity c WHERE c.id = :commentId")
    fun getAuthorId(@Param("commentId") commentId: Long): String
}