package org.worker996.kotlinsprintbootpd.article

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.worker996.kotlinsprintbootpd.tag.TagEntity
import org.worker996.kotlinsprintbootpd.tag.TagService
import org.worker996.kotlinsprintbootpd.user.UserRepository
import org.worker996.kotlinsprintbootpd.util.forbidden
import org.worker996.kotlinsprintbootpd.util.isUniqueConstraintException
import org.worker996.kotlinsprintbootpd.util.slugify
import org.worker996.kotlinsprintbootpd.web.PageParams
import java.sql.SQLException

@Service
class ArticleService(
    private val repository: ArticleRepository,
    private val userRepository: UserRepository,
    private val tagService: TagService,
) {

    /**
     * Retrieves a list of articles based on the provided parameters.
     *
     * @param userId The ID of the user making the request (optional).
     * @param tag The tag of the articles to filter by (optional).
     * @param author The author of the articles to filter by (optional).
     * @param favorited The username of the user who favorited the articles to filter by (optional).
     * @param pageParams The pagination parameters for the result set.
     * @return The list of articles matching the provided criteria.
     */
    fun listArticle(
        userId: String?,
        tag: String?,
        author: String?,
        favorited: String?,
        pageParams: PageParams,
    ): List<ArticleModel> {
        val pageable = PageRequest.of((pageParams.offset / pageParams.limit).toInt(), pageParams.limit.toInt())
        return repository.list(tag, author, favorited, pageable).map { toModel(userId, it) }
    }

    /**
     * Counts the number of articles based on the provided parameters.
     *
     * @param tag The tag of the articles to filter by (optional).
     * @param author The author of the articles to filter by (optional).
     * @param favorited The username of the user who favorited the articles to filter by (optional).
     * @return The total count of articles matching the provided criteria.
     */
    fun countArticle(tag: String?, author: String?, favorited: String?): Long {
        return repository.count(tag, author, favorited)
    }

    /**
     * Retrieves a list of articles from the user's feed based on the provided parameters.
     *
     * @param userId The ID of the user making the request.
     * @param pageParams The pagination parameters for the result set.
     * @return The list of articles matching the provided criteria.
     */
    fun listFeedArticle(userId: String, pageParams: PageParams): List<ArticleModel> {
        val pageable = PageRequest.of((pageParams.offset / pageParams.limit).toInt(), pageParams.limit.toInt())
        return repository.listFeed(userId, pageable).map { toModel(userId, it) }
    }

    /**
     * Counts the number of feed articles for a given user ID.
     *
     * @param userId The ID of the user.
     * @return The total count of feed articles.
     */
    fun countFeedArticle(userId: String): Long {
        return repository.countFeed(userId)
    }

    /**
     * Retrieves an article based on the provided slug and user ID.
     *
     * @param slug The slug of the article.
     * @param userId The ID of the user making the request (optional).
     * @return The article matching the provided slug converted to an ArticleModel object.
     */
    fun getArticle(slug: String, userId: String?): ArticleModel {
        val article = repository.findBySlug(slug)
        return toModel(userId, article)
    }

    /**
     * Creates an article for a specific user.
     *
     * @param userId The ID of the user creating the article.
     * @param request The article creation request object containing the article data.
     * @return The created article as an ArticleModel object.
     */
    fun createArticle(userId: String, request: ArticleCreateRequest): ArticleModel {
        return doCreateArticle(userId, request.article, false)
    }

    /**
     * Updates an article with the provided details.
     *
     * @param userId The ID of the user updating the article.
     * @param slug The slug of the article to be updated.
     * @param request The article update request object containing the updated article data.
     * @return The updated article as an ArticleModel object.
     */
    fun updateArticle(userId: String, slug: String, request: ArticleUpdateRequest): ArticleModel {
        return doUpdateArticle(userId, slug, request.article, false)
    }

    /**
     * Deletes an article based on the provided user ID and slug.
     *
     * @param userId The ID of the user making the request.
     * @param slug The slug of the article to be deleted.
     */
    fun deleteArticle(userId: String, slug: String) {
        val article = repository.findBySlug(slug)
        if (article.author.id != userId) forbidden("Not author")
        repository.removeTagsFromArticle(article.id)
        repository.removeFavoritesFromArticle(article.id)
        repository.deleteBySlug(slug)
    }

    /**
     * Add a article to the favorites list of a specific user.
     *
     * @param userId The ID of the user.
     * @param slug The slug of the article.
     * @return The favorite article as an ArticleModel object.
     */
    fun favoriteArticle(userId: String, slug: String): ArticleModel {
        val article = repository.findBySlug(slug)
        repository.favoriteArticle(userId, article.id)
        return toModel(userId, article, true)
    }

    /**
     * Unfavorites an article for a specific user.
     *
     * @param userId The ID of the user unfavoriting the article.
     * @param slug The slug of the article to be unfavorited.
     * @return The unfavorited article as an ArticleModel object.
     */
    fun unfavoriteArticle(userId: String, slug: String): ArticleModel {
        val article = repository.findBySlug(slug)
        repository.unfavoriteArticle(userId, article.id)
        return toModel(userId, article, false)
    }

    /**
     * Creates an article for a specific user.
     *
     * @param userId The ID of the user creating the article.
     * @param payload The article creation request object containing the article data.
     * @param slugSuffix Determines whether to add a random suffix to the slug. Defaults to false.
     * @return The created article as an ArticleModel object.
     */
    private fun doCreateArticle(
        userId: String,
        payload: ArticleCreateRequest.Article,
        slugSuffix: Boolean,
    ): ArticleModel {
        return try {
            val slug = payload.title.slugify(slugSuffix)
            val author = userRepository.getReferenceById(userId)
            val tags = tagService.upsertTags(payload.tagList)
            val article = repository.save(
                ArticleEntity(
                    title = payload.title,
                    description = payload.description,
                    body = payload.body,
                    slug = slug,
                    author = author,
                    tags = tags.toSet()
                )
            )

            // add tags articles relationship
            tags.map { it.id }.forEach {
                repository.addTagToArticle(article.id, it)
            }
            toModel(userId, article)
        } catch (e: SQLException) {
            when {
                // if the slug is not unique, try again with a different suffix
                e.isUniqueConstraintException("article_slug_key") -> doCreateArticle(userId, payload, true)
                else -> throw e
            }
        }
    }

    /**
     * Updates an article with the provided details.
     *
     * @param userId The ID of the user updating the article.
     * @param slug The slug of the article to be updated.
     * @param payload The article update request object containing the updated article data.
     * @param slugSuffix Determines whether to add a random suffix to the slug. Defaults to false.
     * @return The updated article as an ArticleModel object.
     * @throws SQLException If there is an error performing the database operation.
     */
    private fun doUpdateArticle(
        userId: String,
        slug: String,
        payload: ArticleUpdateRequest.Article,
        slugSuffix: Boolean,
    ): ArticleModel = try {
        val article = repository.findBySlug(slug)
        if (article.author.id != userId) forbidden("Not author")

        val newSlug = payload.title?.slugify(slugSuffix)
        repository.save(
            article.copy(
                title = payload.title ?: article.title,
                description = payload.description ?: article.description,
                body = payload.body ?: article.body,
                slug = newSlug ?: article.slug,
            )
        )
        toModel(userId, article)
    } catch (e: SQLException) {
        when {
            // if the slug is not unique, try again with a different suffix
            e.isUniqueConstraintException("article_slug_key") -> doUpdateArticle(userId, slug, payload, true)
            else -> throw e
        }
    }

    /**
     * Converts an ArticleEntity object to an ArticleModel object.
     *
     * @param userId The ID of the user making the request (optional).
     * @param article The ArticleEntity object to convert.
     * @param favorited Determines if the article is favorited by the user (default: false).
     * @return The converted ArticleModel object.
     */
    private fun toModel(
        userId: String?,
        article: ArticleEntity,
        favorited: Boolean = repository.isFavorited(userId, article.id),
    ): ArticleModel {
        // check if the user is following the author of the article
        val isFollowed = userId != null && userRepository.isFollowed(userId, article.author.id)
        return ArticleModel.fromEntity(
            article = article,
            favorited = favorited,
            favoritesCount = repository.countFavorite(article.id),
            authorFollowed = isFollowed,
        )
    }
}