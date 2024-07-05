package org.worker996.kotlinsprintbootpd.article

import org.hamcrest.CoreMatchers.any
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.worker996.kotlinsprintbootpd.user.UserEntity
import org.worker996.kotlinsprintbootpd.user.UserRepository
import java.util.*

@ExtendWith(MockitoExtension::class)
class ArticleRepositoryTests {

    @Mock
    private lateinit var articleRepository: ArticleRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save and retrieve an article`() {
        val user = UserEntity(
            email = "test@example.com",
            username = "testuser",
            password = "password"
        )

        val article = ArticleEntity(
            author = user,
            slug = "test-article",
            title = "Test Article",
            description = "Test Description",
            body = "Test Body"
        )

        // Mocking the save behavior of userRepository and articleRepository
        `when`(userRepository.save(any<UserEntity>())).thenReturn(user)
        `when`(articleRepository.save(any<ArticleEntity>())).thenReturn(article)
        `when`(articleRepository.findById(article.id)).thenReturn(Optional.of(article))

        // Save the user and article
        val savedUser = userRepository.save(user)
        val savedArticle = articleRepository.save(article)

        // Retrieve the article
        val retrievedArticle = articleRepository.findById(savedArticle.id).orElse(null)

        // Assert the retrieved article is the same as the saved one
        assertEquals(savedArticle, retrievedArticle)
    }
}