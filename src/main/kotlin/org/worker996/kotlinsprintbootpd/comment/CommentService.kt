package org.worker996.kotlinsprintbootpd.comment

import org.springframework.stereotype.Service
import org.worker996.kotlinsprintbootpd.article.ArticleRepository
import org.worker996.kotlinsprintbootpd.user.UserRepository
import org.worker996.kotlinsprintbootpd.util.forbidden
import org.worker996.kotlinsprintbootpd.util.notFound

@Service
class CommentService (
    private val repository: CommentRepository,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
) {

    /**
     * Creates a new comment and returns the comment model.
     *
     * @param userId The ID of the user creating the comment.
     * @param articleSlug The slug of the article to comment on.
     * @param payload The comment payload containing the comment body.
     * @return The created comment model.
     */
    fun createComment(userId: String, articleSlug: String, payload: CommentCreateRequest.Comment): CommentModel {
        val author = userRepository.getReferenceById(userId)
        val article = articleRepository.findBySlug(articleSlug)
        val comment = repository.save(
            CommentEntity(
                author = author,
                article = article,
                body = payload.body,
            )
        )
        return toModel(userId, comment)
    }

    /**
     * Retrieves a list of comments for a given article slug.
     *
     * @param userId The ID of the user requesting the comment list. Can be null.
     * @param articleSlug The slug of the article to retrieve comments for.
     * @return A list of comment models associated with the article.
     */
    fun listComment(userId: String?, articleSlug: String): List<CommentModel> {
        val article = articleRepository.findBySlug(articleSlug)
        return repository.listByArticle(article).map { toModel(userId, it) }
    }

    /**
     * Deletes a comment with the specified commentId if the userId matches the authorId of the comment.
     *
     * @param userId The ID of the user attempting to delete the comment.
     * @param commentId The ID of the comment to delete.
     */
    fun deleteComment(userId: String, commentId: Long) {
        val authorId = repository.getAuthorId(commentId)
        if (authorId != userId) {
            forbidden("You can only delete your own comments")
        }

        repository.deleteById(commentId)
    }

    private fun toModel(userId: String?, comment: CommentEntity): CommentModel {
        val isFollowed = userId != null && userRepository.isFollowed(userId, comment.author.id)
        return CommentModel.fromEntity(comment, isFollowed)
    }
}