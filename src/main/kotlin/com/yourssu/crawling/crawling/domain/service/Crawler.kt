package com.yourssu.crawling.crawling.domain.service

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
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class Crawler(
    @Value("\${general.user-agent}")
    private val userAgent: String,

    private val informationRepository: InformationRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

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
        val startAt = LocalDateTime.now()
        log.info("Start At : $startAt")

        val jobs = mutableListOf<Deferred<Unit>>()
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        for (pageNumber in 1..endNumber) {
            val deferredJob: Deferred<Unit> =
                coroutineScope.async {
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

                        faviconElement?.attr("href") // FIXME: 여기마저도 해당 안 될 경우 디폴트 이미지 제공
                    }

                    val informationData = mutableListOf<Information>()

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

                        val information = Information(
                            title = title,
                            content = content.toString().trim(),
                            date = date,
                            contentUrl = contentUrl,
                            imgList = imgList,
                            favicon = faviconUrl,
                            source = source
                        )

                        informationData.add(information)
                    }
                    log.info("crawling page number : {}", pageNumber)
                    informationRepository.saveAll(informationData)
                }
            jobs.add(deferredJob)
        }

        coroutineScope.async {
            jobs.awaitAll()
            val endAt = LocalDateTime.now()

            log.info("End At : $endAt")
            log.info("Logic Duration : ${Duration.between(startAt, endAt).toSeconds()} s")
        }
    }
}