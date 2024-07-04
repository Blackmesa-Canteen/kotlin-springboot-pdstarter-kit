package org.worker996.kotlinsprintbootpd.comment

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.worker996.kotlinsprintbootpd.web.userId
import org.worker996.kotlinsprintbootpd.web.userIdOrNull

@RestController
class CommentController (
    private val service: CommentService,
) {

    @PostMapping("/articles/{slug}/comments")
    fun createCommentForArticle(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @RequestBody payload: CommentCreateRequest,
    ): ResponseEntity<CommentResponse> {
        val userId = request.userId()
        val commentModel = service.createComment(
            userId = userId,
            articleSlug = slug,
            payload = payload.comment,
        )

        return ok().body(CommentResponse(commentModel))
    }

    @GetMapping("/articles/{slug}/comments")
    fun listCommentsForArticle(
        request: HttpServletRequest,
        @PathVariable slug: String,
    ): ResponseEntity<CommentsResponse> {
        val userId = request.userIdOrNull()
        val comments = service.listComment(userId, slug)

        return ok().body(CommentsResponse(comments))
    }

    @DeleteMapping("/articles/{slug}/comments/{id}")
    fun deleteCommentForArticle(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        val userId = request.userId()
        service.deleteComment(userId, id)

        return ok().build()
    }
}