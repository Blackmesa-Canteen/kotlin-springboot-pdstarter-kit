package org.worker996.kotlinsprintbootpd.user

import jakarta.persistence.*
import org.worker996.kotlinsprintbootpd.article.ArticleEntity
import org.worker996.kotlinsprintbootpd.comment.CommentEntity
import java.time.Instant
import java.util.UUID

/**
 * This class represents a User in the system.
 *
 * @property id The unique identifier for the User.
 * @property email The email of the User.
 * @property username The username of the User.
 * @property password The password of the User.
 * @property bio The biography of the User (nullable).
 * @property image The image URL of the User (nullable).
 * @property createdAt The timestamp when the User was created.
 * @property updatedAt The timestamp when the User was last updated.
 * @property followees The set of Users that this User follows.
 * @property followers The set of Users that follow this User.
 * @property favoriteArticles The set of Articles that this User has favorited.
 * @property comments The set of Comments that this User has made.
 */
@Entity
@Table(name = "\"user\"")
data class UserEntity(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: String = UUID.randomUUID().toString(),

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