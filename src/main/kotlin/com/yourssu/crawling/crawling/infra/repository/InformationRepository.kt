package com.yourssu.crawling.crawling.infra.repository

import com.yourssu.crawling.crawling.infra.entity.Information
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface InformationRepository : ElasticsearchRepository<Information, String> {
    @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"title^7\",\"content^3\"],\"fuzziness\":\"AUTO\"}}")
    fun findByInfoOrderByScoreDesc(query: String, pageable: Pageable): Page<Information>
}
