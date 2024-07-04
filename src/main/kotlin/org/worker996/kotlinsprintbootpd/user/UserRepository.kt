package org.worker996.kotlinsprintbootpd.user

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, String>{
    fun findByEmail(email: String): UserEntity?

    fun findByUsername(username: String): UserEntity?

    /**
     * Checks if a user with the provided followerId is following a user with the provided followeeId.
     *
     * @param followerId The ID of the follower user.
     * @param followeeId The ID of the followee user.
     * @return true if the follower user is following the followee user, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM UserEntity u JOIN u.followers f WHERE u.id = :followeeId AND f.id = :followerId")
    fun isFollowed(@Param("followerId") followerId: String, @Param("followeeId") followeeId: String): Boolean

    @Modifying
    @Transactional
    @Query("INSERT INTO user_follow (follower_id, followee_id) VALUES (:followerId, :followeeId)", nativeQuery = true)
    fun followUser(@Param("followerId") followerId: String, @Param("followeeId") followeeId: String)

    @Modifying
    @Transactional
    @Query("DELETE FROM user_follow WHERE follower_id = :followerId AND followee_id = :followeeId", nativeQuery = true)
    fun unfollowUser(@Param("followerId") followerId: String, @Param("followeeId") followeeId: String)

}