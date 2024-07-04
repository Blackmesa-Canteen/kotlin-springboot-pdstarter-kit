package org.worker996.kotlinsprintbootpd.comment

import org.worker996.kotlinsprintbootpd.user.ProfileModel
import java.time.Instant

data class CommentCreateRequest(val comment: Comment) {
    data class Comment(val body: String)
}

data class CommentModel(
    val id: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val body: String,
    val author: ProfileModel,
) {
    companion object {
        fun fromEntity(comment: CommentEntity, authorFollowed: Boolean): CommentModel {
            return CommentModel(
                id = comment.id,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt,
                body = comment.body,
                author = ProfileModel.fromEntity(comment.author, authorFollowed),
            )
        }
    }
}

data class CommentResponse(val comment: CommentModel)

data class CommentsResponse(val comments: List<CommentModel>)