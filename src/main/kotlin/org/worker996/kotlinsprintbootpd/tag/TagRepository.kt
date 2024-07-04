package org.worker996.kotlinsprintbootpd.tag

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TagRepository: JpaRepository<TagEntity, Long> {
    fun findByName(name: String): TagEntity?

    @Query("SELECT t FROM TagEntity t WHERE t.name IN :names")
    fun findByNames(names: List<String>): List<TagEntity>

    @Query("SELECT t FROM TagEntity t WHERE t.name IN :name")
    fun findTagsByName(name: List<String>): List<TagEntity>

    @Query("SELECT t FROM TagEntity t")
    fun listTag(): List<TagEntity>

    @Modifying
    @Transactional
    @Query(
        value = "INSERT INTO tag (name, created_at, updated_at) VALUES (:name, NOW(), NOW()) ON CONFLICT (name) DO NOTHING",
        nativeQuery = true
    )
    fun insertTag(@Param("name") name: String)

    @Modifying
    @Transactional
    @Query("DELETE FROM TagEntity t WHERE t.name IN :names")
    fun deleteByNames(names: List<String>)
}