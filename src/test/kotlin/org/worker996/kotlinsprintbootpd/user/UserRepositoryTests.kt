package org.worker996.kotlinsprintbootpd.user

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Testcontainers
import org.worker996.kotlinsprintbootpd.extensions.JpaDatabaseCleanerExtension

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension::class, JpaDatabaseCleanerExtension::class)
class UserRepositoryTests @Autowired constructor(
    private val userRepository: UserRepository
) : StringSpec({

    "should save and retrieve a user" {
        val user = UserEntity(
            email = "test@example.com",
            username = "testuser",
            password = "password"
        )

        val savedUser = userRepository.save(user)
        val retrievedUser = userRepository.findById(savedUser.id).orElse(null)

        retrievedUser shouldBe savedUser
    }
})