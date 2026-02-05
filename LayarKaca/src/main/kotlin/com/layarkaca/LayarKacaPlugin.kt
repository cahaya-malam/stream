package com.layarkaca

import android.content.Context
import com.lagradost.cloudstream3.extractors.Voe
import com.lagradost.cloudstream3.extractors.StreamTape
import com.lagradost.cloudstream3.extractors.MixDrop
import com.lagradost.cloudstream3.extractors.Mp4Upload
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class LayarKacaPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(LayarKaca())
        
        // Hanya gunakan extractor yang umum dan pasti ada
        registerExtractorAPI(Voe())
        registerExtractorAPI(StreamTape())
        registerExtractorAPI(MixDrop())
        registerExtractorAPI(Mp4Upload())
        registerExtractorAPI(Emturbovid())
//        registerExtractorAPI(Furher())
        registerExtractorAPI(Hownetwork())

    }
}
