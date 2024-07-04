package org.worker996.kotlinsprintbootpd.article

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.worker996.kotlinsprintbootpd.web.pageParams
import org.worker996.kotlinsprintbootpd.web.userId
import org.worker996.kotlinsprintbootpd.web.userIdOrNull

@RestController
class ArticleController(
    private val service: ArticleService
) {

    /**
     * Retrieves all articles based on the provided parameters.
     *
     * @param request The HTTP request object.
     * @param tag The tag of the articles to filter by (optional).
     * @param author The author of the articles to filter by (optional).
     * @param favorited The username of the user who favorited the articles to filter by (optional).
     * @return A ResponseEntity containing the articles and the count as an ArticlesResponse object.
     */
    @GetMapping("/articles")
    fun getAllArticles(
        request: HttpServletRequest,
        @RequestParam tag: String?,
        @RequestParam author: String?,
        @RequestParam favorited: String?,
    ): ResponseEntity<ArticlesResponse> {
        val userId = request.userIdOrNull()
        val articles = service.listArticle(
            userId = userId,
            tag = tag,
            author = author,
            favorited = favorited,
            pageParams = request.pageParams()
        )
        val count = service.countArticle(tag = tag, favorited = favorited, author = author)
        return ok().body(ArticlesResponse(articles, count))
    }

    /**
     * Retrieves the articles from the user's following users based on the provided request.
     *
     * @param request The HTTP request object.
     * @return A ResponseEntity containing the articles and the count as an ArticlesResponse object.
     */
    @GetMapping("/articles/feed")
    fun getFeedArticles(
        request: HttpServletRequest,
    ): ResponseEntity<ArticlesResponse> {
        val userId = request.userId()
        val articles = service.listFeedArticle(userId, request.pageParams())
        val count = service.countFeedArticle(userId)
        return ok().body(ArticlesResponse(articles, count))
    }

    /**
     * Retrieves an article by slug.
     *
     * @param request The HttpServletRequest object.
     * @param slug The slug of the article.
     * @return A ResponseEntity containing the ArticleResponse object.
     */
    @GetMapping("/articles/{slug}")
    fun getArticlesBySlug(
        request: HttpServletRequest,
        @PathVariable slug: String,
    ): ResponseEntity<ArticleResponse> {
        val userId = request.userIdOrNull()
        val articleModel = service.getArticle(slug, userId)
        return ok().body(ArticleResponse(articleModel))
    }

    /**
     * Creates an article.
     *
     * @param request The HTTP servlet request object.
     * @param articleCreateRequest The article creation request object.
     * @return A ResponseEntity containing the created article as an ArticleResponse object.
     */
    @PostMapping("/articles")
    fun createArticle(
        request: HttpServletRequest,
        @RequestBody articleCreateRequest: ArticleCreateRequest,
    ): ResponseEntity<ArticleResponse> {
        val userId = request.userId()
        val articleModel = service.createArticle(userId, articleCreateRequest)
        return ok().body(ArticleResponse(articleModel))
    }

    /**
     * Updates an article with the provided details.
     *
     * @param request The HTTP request object.
     * @param slug The slug of the article to be updated.
     * @param articleUpdateRequest The article update request object containing the updated article data.
     * @return A ResponseEntity containing the updated article as an ArticleResponse object.
     */
    @PutMapping("/articles/{slug}")
    fun updateArticle(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @RequestBody articleUpdateRequest: ArticleUpdateRequest,
    ): ResponseEntity<ArticleResponse> {
        val userId = request.userId()
        val articleModel = service.updateArticle(userId, slug, articleUpdateRequest)
        return ok().body(ArticleResponse(articleModel))
    }

    /**
     * Deletes an article based on the provided user ID and slug.
     *
     * @param request The HTTP request object.
     * @param slug The slug of the article to be deleted.
     * @return A ResponseEntity with no content.
     */
    @DeleteMapping("/articles/{slug}")
    fun deleteArticle(
        request: HttpServletRequest,
        @PathVariable slug: String,
    ): ResponseEntity<Unit> {
        val userId = request.userId()
        service.deleteArticle(userId, slug)
        return ok().build()
    }

    /**
     * Adds an article to the favorites list of a specific user.
     *
     * @param request The HTTP request object.
     * @param slug The slug of the article.
     * @return A ResponseEntity containing the favorite article as an ArticleResponse object.
     */
    @PostMapping("/articles/{slug}/favorite")
    fun favoriteArticle(
        request: HttpServletRequest,
        @PathVariable slug: String,
    ): ResponseEntity<ArticleResponse> {
        val userId = request.userId()
        val articleModel = service.favoriteArticle(userId, slug)
        return ok().body(ArticleResponse(articleModel))
    }

    /**
     * Unfavorites an article for a specific user.
     *
     * @param request The HTTP request object.
     * @param slug The slug of the article to be unfavorited.
     * @return A ResponseEntity containing the unfavorited article as an ArticleResponse object.
     */
    @DeleteMapping("/articles/{slug}/favorite")
    fun unfavoriteArticle(
        request: HttpServletRequest,
        @PathVariable slug: String,
    ): ResponseEntity<ArticleResponse> {
        val userId = request.userId()
        val articleModel = service.unfavoriteArticle(userId, slug)
        return ok().body(ArticleResponse(articleModel))
    }
}