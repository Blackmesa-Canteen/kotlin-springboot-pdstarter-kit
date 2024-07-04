package org.worker996.kotlinsprintbootpd.user

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.worker996.kotlinsprintbootpd.web.userId
import org.worker996.kotlinsprintbootpd.web.userIdOrNull

@RestController
class UserController (private val userService: UserService) {

    /**
     * Logs in a user with the provided credentials and returns a ResponseEntity containing the UserResponse.
     *
     * @param loginRequest The login request containing user credentials.
     * @return The ResponseEntity<UserResponse> object containing the user information.
     */
    @PostMapping("/users/login")
    fun userLogin(@RequestBody loginRequest: LoginRequest): ResponseEntity<UserResponse> {
        val userModel = userService.login(loginRequest.user)
        return ok().body(UserResponse(userModel))
    }

    /**
     * Registers a user with the provided register request and returns a response entity containing the user response.
     *
     * @param registerRequest The register request containing user data.
     * @return The ResponseEntity<UserResponse> object containing the user information.
     */
    @PostMapping("/users")
    fun userRegister(@RequestBody registerRequest: RegisterRequest): ResponseEntity<UserResponse> {
        val userModel = userService.register(registerRequest.user)
        return ok().body(UserResponse(userModel))
    }

    /**
     * Retrieves the current user.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @return The ResponseEntity<UserResponse> object containing the current user information.
     */
    @GetMapping("/user")
    fun getCurrentUser(request: HttpServletRequest): ResponseEntity<UserResponse> {
        val userId = request.userId()  // Use the extension function to get the user ID
        val user = userService.getUserById(userId)
        return ok().body(UserResponse(user))
    }

    /**
     * Updates the user with the provided ID using the given payload and returns the updated user as a UserModel.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param userUpdateRequest The user update request containing the updated user details.
     * @return The ResponseEntity<UserResponse> object containing the updated user information.
     */
    @PutMapping("/user")
    fun updateUser(
        request: HttpServletRequest,
        @RequestBody userUpdateRequest: UserUpdateRequest
    ): ResponseEntity<UserResponse> {
        val userId = request.userId()
        val updatedUser = userService.updateUser(userId, userUpdateRequest.user)
        return ok().body(UserResponse(updatedUser))
    }

    /**
     * Retrieves the profile of a user specified by their username.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param username The username of the user whose profile is being retrieved.
     * @return The ResponseEntity<ProfileResponse> object containing the user's profile information.
     */
    @GetMapping("/profiles/{username}")
    fun getUserProfile(
        request: HttpServletRequest,
        @PathVariable username: String
    ): ResponseEntity<ProfileResponse> {
        val userId = request.userIdOrNull()
        val profile = userService.getProfile(userId, username)
        return ok().body(ProfileResponse(profile))
    }

    /**
     * Follows a user by the provided user ID and the username of the user to follow.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param username The username of the user to be followed.
     * @return The ResponseEntity<ProfileResponse> object containing the user's profile information.
     */
    @PostMapping("/profiles/{username}/follow")
    fun followUser(
        request: HttpServletRequest,
        @PathVariable username: String
    ): ResponseEntity<ProfileResponse> {
        val userId = request.userId()
        val profile = userService.followUser(userId, username)
        return ok().body(ProfileResponse(profile))
    }

    /**
     * Unfollows a user by the provided user ID and the username of the user to unfollow.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param username The username of the user to be unfollowed.
     * @return The ResponseEntity<ProfileResponse> object containing the user's profile information.
     */
    @DeleteMapping("/profiles/{username}/follow")
    fun unfollowUser(
        request: HttpServletRequest,
        @PathVariable username: String
    ): ResponseEntity<ProfileResponse> {
        val userId = request.userId()
        val profile = userService.unfollowUser(userId, username)
        return ok().body(ProfileResponse(profile))
    }
}