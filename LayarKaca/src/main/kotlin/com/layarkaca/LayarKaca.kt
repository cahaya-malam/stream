package com.layarkaca

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink

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
        
        // Website menggunakan div dengan class "poster"
        val items = document.select("div.poster").mapNotNull { posterDiv ->
            val link = posterDiv.selectFirst("a") ?: return@mapNotNull null
            val href = fixUrl(link.attr("href")) ?: return@mapNotNull null
            
            val title = posterDiv.selectFirst(".poster-title")?.text()?.trim()
                ?: link.selectFirst("img")?.attr("alt")?.trim()
                ?: return@mapNotNull null
            
            val posterUrl = link.selectFirst("img")?.attr("src")?.let { fixUrlNull(it) }
            
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = posterUrl
            }
        }
        
        return newHomePageResponse(request.name, items)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/search/?s=${query.replace(" ", "+")}"
        val document = app.get(searchUrl).document
        
        return document.select("div.poster").mapNotNull { posterDiv ->
            val link = posterDiv.selectFirst("a") ?: return@mapNotNull null
            val href = fixUrl(link.attr("href")) ?: return@mapNotNull null
            
            val title = posterDiv.selectFirst(".poster-title")?.text()?.trim()
                ?: link.selectFirst("img")?.attr("alt")?.trim()
                ?: return@mapNotNull null
            
            if (!title.contains(query, ignoreCase = true)) return@mapNotNull null
            
            val posterUrl = link.selectFirst("img")?.attr("src")?.let { fixUrlNull(it) }
            
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = posterUrl
            }
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        
        val title = document.selectFirst("h1.entry-title, h1")?.text()?.trim() ?: return null
        
        val poster = document.selectFirst("img.wp-post-image, .poster img, img[src*=.jpg]")?.attr("src")?.let { fixUrl(it) }
        
        val description = document.selectFirst("div.entry-content, .synopsis, blockquote")?.text()?.trim()
        
        val year = Regex("/20(\\d{2})/").find(url)?.groupValues?.get(1)?.toIntOrNull()?.plus(2000)
            ?: Regex("\\b(20\\d{2}|19\\d{2})\\b").find(title)?.value?.toIntOrNull()
        
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = description
            this.year = year
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return true
    }
}
