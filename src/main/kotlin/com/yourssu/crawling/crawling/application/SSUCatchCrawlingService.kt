package com.yourssu.crawling.crawling.application

import com.yourssu.crawling.crawling.domain.service.Crawler
import org.springframework.stereotype.Service

@Service
class SSUCatchCrawlingService(
    private val crawler: Crawler
) {
    companion object {
        private const val NOTICE_END_PAGE_NUM = 638
        const val SOURCE_NAME_NOTICE = "공지사항"
    }

    // FIXME: 기존 코드
    suspend fun crawling() {
        crawler.crawling(
            "https://scatch.ssu.ac.kr/공지사항/page",
            "ul.notice-lists li:not(.notice_head) ",
            ".notice_col3 a .d-inline-blcok.m-pt-5",
            "div.bg-white p",
            ".notice_col3 a",
            ".notice_col1 .text-info",
            NOTICE_END_PAGE_NUM,
            SOURCE_NAME_NOTICE
        )
    }
}
