package org.worker996.kotlinsprintbootpd.tag

import jakarta.persistence.*
import org.worker996.kotlinsprintbootpd.article.ArticleEntity
import java.time.Instant

@Entity
@Table(name = "tag")
class TagEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @ManyToMany(mappedBy = "tags")
    val articles: Set<ArticleEntity> = setOf(),
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
