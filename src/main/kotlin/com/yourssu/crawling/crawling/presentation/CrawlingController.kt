package com.yourssu.crawling.crawling.presentation

import com.yourssu.crawling.crawling.application.SSUCatchCrawlingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crawling")
class CrawlingController(
    private val ssuCatchCrawlingService: SSUCatchCrawlingService
) {

    @PutMapping("/ssucatch")
    @ResponseStatus(HttpStatus.OK)
    suspend fun crawlingSSUCatch() {
        ssuCatchCrawlingService.crawling()
    }
}