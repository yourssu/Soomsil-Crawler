package com.yourssu.crawling.crawling.application

import com.yourssu.crawling.crawling.domain.service.Crawler
import org.springframework.stereotype.Service

@Service
class FunSysCrawlingService(
    private val crawler: Crawler
) {
    companion object {
        private const val FUN_END_NUM = 285
        private const val SOURCE_NAME_FUN = "펀시스템"
    }

    suspend fun crawling() {
        crawler.crawling(
            "https://fun.ssu.ac.kr/ko/program/all/list/all",
            "ul.columns-4 li",
            ".content .title",
            "div .description p",
            "a",
            "small.thema_point_color.topic ~ small time",
            FUN_END_NUM,
            SOURCE_NAME_FUN
        )
    }
}