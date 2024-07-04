package org.worker996.kotlinsprintbootpd.user

/**
 * Represents a login request with user credentials.
 *
 * @property user the user entity containing email and password
 */
data class LoginRequest(val user: User) {
    data class User(val email: String, val password: String)
}

/**
 * Represents a register request with user data.
 *
 * @property user The user entity containing the email and password.
 */
data class RegisterRequest(val user: User) {
    data class User(
        val username: String,
        val email: String,
        val password: String
    )
}

/**
 * Represents a user update request.
 *
 * This class is used to encapsulate the information required to update a user's details.
 * It contains an instance of [User] class which holds the updated user details.
 *
 * @property user The updated user details.
 */
data class UserUpdateRequest(val user: User) {
    data class User(
        val email: String? = null,
        val password: String? = null,
        val username: String? = null,
        val bio: String? = null,
        val image: String? = null
    )
}

/**
 * Represents a user in the system.
 *
 * @property email The email of the user.
 * @property token The token associated with the user.
 * @property username The username of the user.
 * @property bio The bio of the user (nullable).
 * @property image The image URL of the user (nullable).
 *
 * @constructor Creates a new instance of the UserModel class.
 *
 * @param email The email of the user.
 * @param token The token associated with the user.
 * @param username The username of the user.
 * @param bio The bio of the user (nullable, default value is null).
 * @param image The image URL of the user (nullable, default value is null).
 */
data class UserModel(
    val email: String,
    val token: String,
    val username: String,
    val bio: String? = null,
    val image: String? = null,
) {
    companion object {
        fun fromEntity(entity: UserEntity, token: String): UserModel {
            return UserModel(
                email = entity.email,
                token = token,
                username = entity.username,
                bio = entity.bio,
                image = entity.image
            )
        }
    }
}

/**
 * Represents a user profile.
 *
 * @property username The username of the user.
 * @property bio The biography of the user (optional).
 * @property image The profile image URL of the user (optional).
 * @property following Indicates if the current user is following the profile user.
 */
data class ProfileModel(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean,
) {
    companion object {
        fun fromEntity(entity: UserEntity, following: Boolean): ProfileModel {
            return ProfileModel(
                username = entity.username,
                bio = entity.bio,
                image = entity.image,
                following = following
            )
        }
    }
}

/**
 * Represents the response containing user information.
 *
 * @property user The user model.
 *
 * @constructor Creates a new instance of the UserResponse class.
 *
 * @param user The user model.
 */
data class UserResponse(val user: UserModel)

/**
 * Represents the response of a profile.
 *
 * @property profile The profile information.
 */
data class ProfileResponse(val profile: ProfileModel)