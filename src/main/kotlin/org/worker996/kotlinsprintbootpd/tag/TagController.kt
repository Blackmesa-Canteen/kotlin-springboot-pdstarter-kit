package org.worker996.kotlinsprintbootpd.tag

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController (private val tagService: TagService) {

    @GetMapping("/tags")
    fun listTags(): TagsResponse {
        return TagsResponse(tagService.listTag().map { it.name })
    }
}