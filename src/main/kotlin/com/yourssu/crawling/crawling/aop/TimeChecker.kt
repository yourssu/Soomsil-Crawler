package com.yourssu.crawling.crawling.aop

import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime

class TimeChecker {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        fun <T> check(function: () -> T): T {
            val startAt = LocalDateTime.now()
            logger.info("Start At : $startAt")

            val result = function.invoke()

            val endAt = LocalDateTime.now()

            logger.info("End At : $endAt")
            logger.info("Logic Duration : ${Duration.between(startAt, endAt).toMillis()}ms")

            return result
        }
    }
}