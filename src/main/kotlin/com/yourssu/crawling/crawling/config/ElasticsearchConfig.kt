package com.yourssu.crawling.crawling.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Configuration
class ElasticSearchConfig(
    @Value("\${es.id}")
    val user: String,

    @Value("\${es.password}")
    val password: String,

    @Value("\${es.endpoint}")
    val endpoint: String
) : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo(endpoint)
            .usingSsl(disableSslVerification()!!, allHostsValid())
            .withBasicAuth(user, password)
            .build()
    }

    fun disableSslVerification(): SSLContext? {
        try {
            // ============================================
            // trust manager 생성(인증서 체크 전부 안함)
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {
                }

                override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {
                }
            })

            // trust manager 설치
            val sc: SSLContext = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())

            return sc
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        return null
    }

    fun allHostsValid(): HostnameVerifier {
        // ============================================
        // host name verifier 생성(호스트 네임 체크안함)

        val allHostsValid = HostnameVerifier { hostname: String?, session: SSLSession? -> true }

        // host name verifier 설치
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
        return allHostsValid
    }
}