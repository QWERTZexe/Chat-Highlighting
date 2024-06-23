package com.qwertz.chat_highlighting.config

import com.qwertz.chat_highlighting.ChatHighlighting
import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.data.OptionSize

object ChatHighlightingConfig : Config(Mod(ChatHighlighting.NAME, ModType.UTIL_QOL, "/ChatHighlighting.png"), ChatHighlighting.MODID + ".json") {

    @Info(
        text = "Example: (\"mycoolstring\";6;l);(\"thisisred\";4)",
        type = InfoType.INFO
    )
    var example = Runnable {  }

    @Text(
        name = "WORDS:  (SEPERATE WITH ';')",
        size = OptionSize.DUAL,
        multiline = true
    )
    var Wordstring = "(\"mycoolstring\";6;l);(\"thisisred\";4)"

    init {
        initialize()
    }

}