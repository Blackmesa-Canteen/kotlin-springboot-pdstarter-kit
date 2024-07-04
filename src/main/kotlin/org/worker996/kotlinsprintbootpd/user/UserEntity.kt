package org.worker996.kotlinsprintbootpd.user

import jakarta.persistence.*
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
    var updatedAt: Instant = Instant.now()
) {
    @PrePersist
    fun onCreate() {
        createdAt = Instant.now()
        updatedAt = Instant.now()
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}