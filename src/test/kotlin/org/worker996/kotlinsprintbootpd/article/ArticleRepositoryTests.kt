package org.worker996.kotlinsprintbootpd.article

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Testcontainers
import org.worker996.kotlinsprintbootpd.extensions.JpaDatabaseCleanerExtension
import org.worker996.kotlinsprintbootpd.user.UserEntity
import org.worker996.kotlinsprintbootpd.user.UserRepository

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension::class, JpaDatabaseCleanerExtension::class)
class ArticleRepositoryTests @Autowired constructor(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) : StringSpec({

    "should save and retrieve an article" {
        val user = userRepository.save(UserEntity(
            email = "test@example.com",
            username = "testuser",
            password = "password"
        ))

        val article = ArticleEntity(
            author = user,
            slug = "test-article",
            title = "Test Article",
            description = "Test Description",
            body = "Test Body"
        )

        val savedArticle = articleRepository.save(article)
        val retrievedArticle = articleRepository.findById(savedArticle.id).orElse(null)

        retrievedArticle shouldBe savedArticle
    }
})