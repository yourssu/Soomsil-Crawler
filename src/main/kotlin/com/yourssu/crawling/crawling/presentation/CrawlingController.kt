package com.yourssu.crawling.crawling.presentation

import com.yourssu.crawling.crawling.application.FunSysCrawlingService
import com.yourssu.crawling.crawling.application.SSUCatchCrawlingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crawling")
class CrawlingController(
    private val ssuCatchCrawlingService: SSUCatchCrawlingService,
    private val funSystemCrawlingService: FunSysCrawlingService
) {
    @PutMapping("/fun-system")
    @ResponseStatus(HttpStatus.OK)
    suspend fun crawlingFunSystem(): ResponseEntity<String> {
        return try {
            funSystemCrawlingService.crawling()
            ResponseEntity.ok("Crawling started successfully.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Crawling failed: ${e.message}")
        }
    }

    @PutMapping("/ssucatch")
    @ResponseStatus(HttpStatus.OK)
    suspend fun crawlingSSUCatch(): ResponseEntity<String> {
        return try {
            ssuCatchCrawlingService.crawling()
            ResponseEntity.ok("Crawling started successfully.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Crawling failed: ${e.message}")
        }
    }
}