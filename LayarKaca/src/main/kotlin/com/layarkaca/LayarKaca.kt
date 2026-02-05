package com.layarkaca

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addScore
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element

class LayarKaca : MainAPI() {

    override var mainUrl = "https://tv.lk21official.love"
    override var name = "LayarKaca"
    override val hasMainPage = true
    override var lang = "id"
    override val supportedTypes = setOf(TvType.Movie)

    override val mainPage = mainPageOf(
        "$mainUrl/populer" to "Film Terpopuler",
        "$mainUrl/latest" to "Film Terbaru",
        "$mainUrl/rating" to "Rating Tertinggi",
        "$mainUrl/most-commented" to "Komentar Terbanyak"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page > 1) "${request.data}?page=$page" else request.data
        val document = app.get(url).document
        
        val items = document.select("article.mega-item").mapNotNull { element ->
            val title = element.selectFirst("h2, h3")?.text()?.trim() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href")?.let { fixUrl(it) } ?: return@mapNotNull null
            val posterUrl = element.selectFirst("img")?.attr("src")?.let { fixUrlNull(it) }
            
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = posterUrl
            }
        }
        
        return newHomePageResponse(request.name, items)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchQuery = query.replace(" ", "+")
        val searchUrl = "$mainUrl/search/?s=$searchQuery"
        
        val document = app.get(searchUrl).document
        
        return document.select("article.mega-item").mapNotNull { element ->
            val title = element.selectFirst("h2, h3")?.text()?.trim() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href")?.let { fixUrl(it) } ?: return@mapNotNull null
            val posterUrl = element.selectFirst("img")?.attr("src")?.let { fixUrlNull(it) }
            
            // Filter berdasarkan query (case insensitive)
            if (title.contains(query, ignoreCase = true)) {
                newMovieSearchResponse(title, href, TvType.Movie) {
                    this.posterUrl = posterUrl
                }
            } else {
                null
            }
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        
        val title = document.selectFirst("h1, li.last > span[itemprop=name]")?.text()?.trim() 
            ?: return null
        
        val poster = document.selectFirst("img.img-thumbnail, .poster img")?.attr("src")?.let { fixUrl(it) }
        
        val description = document.selectFirst("div.content > blockquote, .synopsis, .plot")?.text()?.trim()
        
        val yearText = document.select("div.content > div:nth-child(7) > h3, .year").text()
        val year = Regex("\\b(19|20)\\d{2}\\b").find(yearText)?.value?.toIntOrNull()
        
        val rating = document.selectFirst("div.content > div:nth-child(6) > h3, .rating")?.text()?.trim()
        
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = description
            this.year = year
            if (!rating.isNullOrEmpty()) addScore(rating)
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // Untuk sementara return false
        return false
    }
}
