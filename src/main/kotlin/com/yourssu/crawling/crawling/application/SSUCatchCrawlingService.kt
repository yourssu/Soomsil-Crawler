package com.yourssu.crawling.crawling.application

import com.yourssu.crawling.crawling.aop.TimeChecker
import com.yourssu.crawling.crawling.infra.entity.Information
import com.yourssu.crawling.crawling.infra.repository.InformationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SSUCatchCrawlingService(
    @Value("\${general.user-agent}")
    private val userAgent: String,

    private val informationRepository: InformationRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val NOTICE_END_PAGE_NUM = 638
        const val SOURCE_NAME_NOTICE = "공지사항"
    }

    // FIXME: 기존 코드
    suspend fun crawling() {
        crawling(
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

    suspend fun crawling(
        baseUrl: String,
        ulSelector: String,
        titleSelector: String,
        contentSelector: String,
        urlSelector: String,
        dateSelector: String,
        endNumber: Int,
        source: String
    ) {
        TimeChecker.check {
            val jobs = mutableListOf<Deferred<Unit>>()
            val coroutineScope = CoroutineScope(Dispatchers.IO)

            for (pageNumber in 1..endNumber) {
                val deferredJob: Deferred<Unit> =
                    coroutineScope.async {
                        log.info("crawling page number : {}", pageNumber)
                        val document = Jsoup.connect("$baseUrl/$pageNumber")
                            .userAgent(userAgent)
                            .get()

                        val ul = document.select(ulSelector)

                        var faviconElement: Element? = document.head()
                            .select("link[href~=.*\\.ico]")
                            .first()

                        val faviconUrl: String? = if (faviconElement != null) {
                            faviconElement.attr("href")
                        } else {
                            faviconElement = document.head()
                                .select("link[rel=icon]")
                                .first()

                            faviconElement?.attr("href")
                        }

                        ul.forEach {
                            val date = it.selectFirst(dateSelector)?.text() ?: ""
                            val title = it.selectFirst(titleSelector)?.text() ?: ""
                            val contentUrl = it.selectFirst(urlSelector)?.attr("abs:href") ?: ""
                            val paragraphs = Jsoup.connect(contentUrl).get().select(contentSelector)

                            val imgList = paragraphs.select("img").map { img -> img.attr("src") }

                            val content = StringBuilder()
                            for (paragraph in paragraphs) {
                                val trimmedText = paragraph.text().replace("\\s+".toRegex(), " ").trim()
                                if (trimmedText.isNotEmpty()) {
                                    content.append(trimmedText).append("\n")
                                }
                            }

                            informationRepository.save(
                                Information(
                                    title = title,
                                    content = content.toString().trim(),
                                    date = date,
                                    contentUrl = contentUrl,
                                    imgList = imgList,
                                    favicon = faviconUrl,
                                    source = source
                                )
                            )
                        }
                    }
                jobs.add(deferredJob)
            }

            coroutineScope.async {
                jobs.awaitAll()
            }
        }
    }
}