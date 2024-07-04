package org.worker996.kotlinsprintbootpd.user

import jakarta.persistence.*
import org.worker996.kotlinsprintbootpd.article.ArticleEntity
import org.worker996.kotlinsprintbootpd.comment.CommentEntity
import java.time.Instant


@Entity
@Table(name = "\"user\"")
data class UserEntity(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: String,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "username", nullable = false, unique = true)
    val username: String,

    @Column(name = "password", nullable = false)
    val password: String,

    @Column(name = "bio")
    val bio: String? = null,

    @Column(name = "image")
    val image: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @ManyToMany
    @JoinTable(
        name = "user_follow",
        joinColumns = [JoinColumn(name = "follower_id")],
        inverseJoinColumns = [JoinColumn(name = "followee_id")]
    )
    val followees: Set<UserEntity> = setOf(),

    @ManyToMany(mappedBy = "followees")
    val followers: Set<UserEntity> = setOf(),

    @ManyToMany
    @JoinTable(
        name = "article_favorite",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "article_id")]
    )
    val favoriteArticles: Set<ArticleEntity> = setOf(),

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: Set<CommentEntity> = setOf()
) {

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}