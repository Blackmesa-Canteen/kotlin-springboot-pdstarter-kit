package org.worker996.kotlinsprintbootpd.article

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.worker996.kotlinsprintbootpd.tag.TagEntity

@Repository
interface ArticleRepository : JpaRepository<ArticleEntity, Long>, CustomArticleRepository {
    @Query(
        """
        SELECT a FROM ArticleEntity a 
        LEFT JOIN a.tags t 
        LEFT JOIN a.favoritedBy f
        WHERE (:tag IS NULL OR t.name = :tag) 
        AND (:author IS NULL OR a.author.username = :author) 
        AND (:favorited IS NULL OR f.username = :favorited)
    """
    )
    fun list(
        @Param("tag") tag: String?,
        @Param("author") author: String?,
        @Param("favorited") favorited: String?,
        pageable: org.springframework.data.domain.Pageable
    ): List<ArticleEntity>

    @Query(
        """
        SELECT a FROM ArticleEntity a 
        JOIN a.author.followees f 
        WHERE f.id = :userId
    """
    )
    fun listFeed(
        @Param("userId") userId: String,
        pageable: org.springframework.data.domain.Pageable
    ): List<ArticleEntity>

    @Query(
        """
        SELECT COUNT(a) FROM ArticleEntity a 
        JOIN a.author.followees f 
        WHERE f.id = :userId
    """
    )
    fun countFeed(@Param("userId") userId: String): Long

    fun findBySlug(slug: String): ArticleEntity

    @Modifying
    @Transactional
    @Query("DELETE FROM ArticleEntity a WHERE a.slug = :slug")
    fun deleteBySlug(@Param("slug") slug: String): Int

    @Modifying
    @Transactional
    @Query("INSERT INTO article_favorite (user_id, article_id) VALUES (:userId, :articleId)", nativeQuery = true)
    fun favoriteArticle(@Param("userId") userId: String, @Param("articleId") articleId: Long)

    @Modifying
    @Transactional
    @Query("DELETE FROM article_favorite WHERE user_id = :userId AND article_id = :articleId", nativeQuery = true)
    fun unfavoriteArticle(@Param("userId") userId: String, @Param("articleId") articleId: Long)

    @Query("SELECT COUNT(af) FROM article_favorite af WHERE af.article_id = :articleId", nativeQuery = true)
    fun countFavorite(@Param("articleId") articleId: Long): Long

    @Query(
        "SELECT CASE WHEN COUNT(af) > 0 THEN TRUE ELSE FALSE END FROM article_favorite af WHERE af.user_id = :userId AND af.article_id = :articleId",
        nativeQuery = true
    )
    fun isFavorited(@Param("userId") userId: String?, @Param("articleId") articleId: Long): Boolean

    @Modifying
    @Transactional
    @Query("DELETE FROM article_tag WHERE article_id = :articleId", nativeQuery = true)
    fun removeTagsFromArticle(articleId: Long)

    @Modifying
    @Transactional
    @Query("DELETE FROM article_favorite WHERE article_id = :articleId", nativeQuery = true)
    fun removeFavoritesFromArticle(articleId: Long)
}