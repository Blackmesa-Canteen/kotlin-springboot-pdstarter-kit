package org.worker996.kotlinsprintbootpd.article

import jakarta.persistence.*
import org.worker996.kotlinsprintbootpd.tag.TagEntity
import org.worker996.kotlinsprintbootpd.user.UserEntity
import java.time.Instant

@Entity
@Table(name = "article")
data class ArticleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    val author: UserEntity,

    @Column(name = "slug", nullable = false, unique = true)
    val slug: String,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "description", nullable = false)
    val description: String,

    @Column(name = "body", nullable = false)
    val body: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @ManyToMany(mappedBy = "favoriteArticles")
    val favoritedBy: Set<UserEntity> = setOf(),

    @ManyToMany
    @JoinTable(
    name = "article_tag",
    joinColumns = [JoinColumn(name = "article_id")],
    inverseJoinColumns = [JoinColumn(name = "tag_id")])
    val tags: Set<TagEntity> = setOf(),

    ) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}