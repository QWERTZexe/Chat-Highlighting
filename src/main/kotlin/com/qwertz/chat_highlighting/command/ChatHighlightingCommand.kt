package com.qwertz.chat_highlighting.command

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import com.qwertz.chat_highlighting.ChatHighlighting
import com.qwertz.chat_highlighting.config.ChatHighlightingConfig

@Command(value = ChatHighlighting.MODID, description = "Access the " + ChatHighlighting.NAME + " Config")
class ChatHighlightingCommand {

    @Main
    fun handle() {
        if (ChatHighlightingConfig.enabled) {
            ChatHighlightingConfig.openGui()
        } else {
            UChat.chat("§4[§6§lCHAT HIGHLIGHTING§4]§a: The mod is disabled in OneConfig. Please enable it.")
        }
    }

}