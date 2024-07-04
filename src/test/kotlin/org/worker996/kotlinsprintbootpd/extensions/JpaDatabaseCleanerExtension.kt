package org.worker996.kotlinsprintbootpd.extensions

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.stereotype.Component

@Component
class JpaDatabaseCleanerExtension : BeforeEachCallback {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun beforeEach(context: ExtensionContext?) {
        entityManager.transaction.begin()
        entityManager.createQuery("DELETE FROM ArticleEntity").executeUpdate()
        entityManager.createQuery("DELETE FROM UserEntity").executeUpdate()
        entityManager.transaction.commit()
    }
}