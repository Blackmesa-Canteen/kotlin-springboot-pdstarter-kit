package org.worker996.kotlinsprintbootpd.user

import org.springframework.stereotype.Service
import org.worker996.kotlinsprintbootpd.util.*
import java.security.spec.ECField

@Service
class UserService (private val jwt: Jwt, private val repository: UserRepository) {

    /**
     * Logs in a user with the provided credentials and returns a UserModel representing the logged-in user.
     *
     * @param payload The user credentials.
     * @return The UserModel of the logged-in user.
     * @throws ResponseStatusException if the login credentials are invalid.
     */
    fun login(payload: LoginRequest.User): UserModel {
        val userEntity = repository.findByEmail(payload.email)?.takeIf { PasswordEncoder.matches(
            payload.password,
            it.password
        ) } ?: unauthorized("Invalid email or password")

        return toModel(userEntity)
    }

    /**
     * Registers a user with the provided user data and returns a UserModel representing the registered user.
     *
     * @param payload The user data for registration.
     * @return The UserModel of the registered user.
     * @throws ResponseStatusException if there is an error during registration.
     */
    fun register(payload: RegisterRequest.User): UserModel {
        try {
            val encodedPwd = PasswordEncoder.encode(payload.password)
            return toModel(repository.save(UserEntity(
                email = payload.email,
                password = encodedPwd,
                username = payload.username,
            )))
        } catch (e: Exception) {
            unprocessable(e.message)
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserModel of the retrieved user.
     * @throws ResponseStatusException if the user is not found.
     */
    fun getUserById(id: String): UserModel {
        return toModel(repository.findById(id).orElseThrow { notFound("User not found") })
    }

    /**
     * Updates the user with the provided ID using the given payload and returns the updated user as a UserModel.
     *
     * @param id The ID of the user to update.
     * @param payload The user update request containing the updated user details.
     * @return The UserModel representing the updated user.
     * @throws ResponseStatusException if the user is not found.
     */
    fun updateUser(id: String, payload: UserUpdateRequest.User): UserModel {
        val userEntity = repository.findById(id).orElseThrow { notFound("User not found") }
        val updatedUser = userEntity.copy(
            email = payload.email ?: userEntity.email,
            password = payload.password?.let { PasswordEncoder.encode(it) } ?: userEntity.password,
            username = payload.username ?: userEntity.username,
            bio = payload.bio ?: userEntity.bio,
            image = payload.image ?: userEntity.image
        )
        return toModel(repository.save(updatedUser))
    }

    /**
     * Follows a user by the provided user ID and the username of the user to follow.
     *
     * @param userId The ID of the user performing the follow action.
     * @param followeeUserName The username of the user to be followed.
     * @return The [ProfileModel] of the user being followed.
     * @throws ResponseStatusException if the user to be followed is not found.
     */
    fun followUser(userId: String, followeeUserName: String): ProfileModel {
        val followee = repository.findByUsername(followeeUserName) ?: notFound("User not found")
        repository.followUser(userId, followee.id)
        return ProfileModel.fromEntity(followee, true)
    }

    /**
     * Unfollows a user by the provided user ID and the username of the user to unfollow.
     *
     * @param userId The ID of the user performing the unfollow action.
     * @param followeeUserName The username of the user to be unfollowed.
     * @return The [ProfileModel] of the user being unfollowed.
     * @throws ResponseStatusException if the user to be unfollowed is not found.
     */
    fun unfollowUser(userId: String, followeeUserName: String): ProfileModel {
        val followee = repository.findByUsername(followeeUserName) ?: notFound("User not found")
        repository.unfollowUser(userId, followee.id)
        return ProfileModel.fromEntity(followee, false)
    }

    /**
     * Retrieves the profile of a user specified by their User ID and username.
     *
     * @param userId The ID of the user performing the profile retrieval.
     * @param userName The username of the user whose profile is being retrieved.
     * @return The ProfileModel representing the user's profile.
     * @throws ResponseStatusException if the user is not found.
     */
    fun getProfile(userId: String?, userName: String): ProfileModel {
        val user = repository.findByUsername(userName) ?: notFound("User not found")
        val isFollowed = userId != null && repository.isFollowed(userId, user.id)
        return ProfileModel.fromEntity(user, isFollowed)
    }

    /**
     * Converts a UserEntity object to a UserModel object.
     *
     * @param userEntity The UserEntity to convert.
     * @return The converted UserModel.
     */
    private fun toModel(userEntity: UserEntity): UserModel = UserModel.fromEntity(userEntity, jwt.generateToken(userEntity.id))
}