package org.worker996.kotlinsprintbootpd.comment

import jakarta.persistence.*
import org.worker996.kotlinsprintbootpd.article.ArticleEntity
import org.worker996.kotlinsprintbootpd.user.UserEntity
import java.time.Instant

@Entity
@Table(name = "comment")
data class CommentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    val author: UserEntity,

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    val article: ArticleEntity,

    @Column(name = "body", nullable = false)
    val body: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}