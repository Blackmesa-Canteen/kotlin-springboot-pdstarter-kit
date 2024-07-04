package org.worker996.kotlinsprintbootpd.tag

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TagService (
    private val repository: TagRepository,) {

    /**
     * Retrieves a list of all tags.
     *
     * @return The list of tags.
     */
    fun listTag(): List<TagEntity> {
        return repository.listTag()
    }

    /**
     * Upserts tags into the system.
     *
     * @param tags The list of tags to be upserted.
     * @return The list of TagEntity objects representing the upserted tags.
     */
    @Transactional
    fun upsertTags(tags: List<String>): List<TagEntity> {
        // Find existing tags
        val existingTags = repository.findByNames(tags).associateBy { it.name }

        // Prepare tags for insertion
        val newTags = tags.filter { it !in existingTags.keys }.map {
            TagEntity(
                name = it,
            )
        }

        // Perform batch insert
        if (newTags.isNotEmpty()) {
            repository.saveAll(newTags)
        }

        // Return the combined list of existing and newly inserted tags
        return repository.findByNames(tags)
    }
}