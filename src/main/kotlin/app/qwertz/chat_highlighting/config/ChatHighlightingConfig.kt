package app.qwertz.chat_highlighting.config

import app.qwertz.chat_highlighting.ChatHighlighting
import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.data.OptionSize


class ChatHighlightingConfig : Config(Mod(ChatHighlighting.NAME, ModType.UTIL_QOL, "/ChatHighlighting.png"), ChatHighlighting.MODID + ".json") {
    init {
        initialize()
    }
    @Info(    text = "Example: (\"mycoolstring\";6;l);(\"thisisred\";4)",
        type = InfoType.INFO)
    var a: Boolean = true
    @Text(name = "WORDS:  (SEPERATE WITH ';')", size = OptionSize.DUAL, multiline = true)
    var Wordstring: String = "(\"mycoolstring\";6;l);(\"thisisred\";4)"
}