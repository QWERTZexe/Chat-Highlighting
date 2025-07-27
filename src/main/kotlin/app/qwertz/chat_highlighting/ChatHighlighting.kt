package app.qwertz.chat_highlighting

import cc.polyfrost.oneconfig.events.event.ChatReceiveEvent
import net.minecraftforge.fml.common.Mod
import kotlinx.coroutines.*
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import app.qwertz.chat_highlighting.command.ChatHighlightingCommand
import net.minecraftforge.common.MinecraftForge
import java.util.Collections.synchronizedList
import net.minecraft.client.Minecraft
import app.qwertz.chat_highlighting.ChatHighlighting.Companion.config
import app.qwertz.chat_highlighting.command.IsEnabled
import app.qwertz.chat_highlighting.config.ChatHighlightingConfig
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import tv.twitch.chat.Chat
import kotlin.math.roundToLong
object GlobalData {
    val chatMessages = synchronizedList(mutableListOf<String>())
    var prevconfigenabled: Boolean = config.enabled
}
@Mod(modid = ChatHighlighting.MODID, name = ChatHighlighting.NAME, version = ChatHighlighting.VERSION)
class ChatHighlighting {
    // Register the config and commands.

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent?) {
        config = ChatHighlightingConfig()
        MinecraftForge.EVENT_BUS.register(ChatEventHandler())
        MinecraftForge.EVENT_BUS.register(TickEventHandler())
        ClientCommandHandler.instance.registerCommand(ChatHighlightingCommand())

    }
    companion object {
        const val MODID: String = "@ID@"
        const val NAME: String = "@NAME@"
        const val VERSION: String = "@VER@"

        @Mod.Instance(MODID)
        lateinit var INSTANCE: ChatHighlighting
        lateinit var config: ChatHighlightingConfig
    }
}


class TickEventHandler {
    private var counter = 0
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent) {
        counter++
        if (counter >= 20) {
            counter = 0
             if (config.enabled) {
                 if (GlobalData.prevconfigenabled != config.enabled) {
                     GlobalData.prevconfigenabled = config.enabled
                     resendChatMessagesHighlight()
                 }


                } else {
                 if (GlobalData.prevconfigenabled != config.enabled) {
                     GlobalData.prevconfigenabled = config.enabled
                     resendChatMessages()
                 }
                }

             }
        }
    }
    fun resendChatMessages() {
        Minecraft.getMinecraft().ingameGUI.chatGUI.clearChatMessages()
        GlobalData.chatMessages.forEach { message ->
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(message))
        }
    }
    fun resendChatMessagesHighlight() {
        Minecraft.getMinecraft().ingameGUI.chatGUI.clearChatMessages()
        val regex = Regex("\\(\"([^\"]+)\";(\\(?[0-9a-f]+[^)]*\\)?)\\)")
        val result = regex.findAll(config.Wordstring).map { it.groupValues[1] to it.groupValues[2].split(";") }.toMap()
        GlobalData.chatMessages.forEach { message ->
            var message2 = message
            result.keys.forEach {key ->
                if (key in message) {
                    val preformatting = result[key]?.joinToString(separator = "§", prefix = "§")
                    val regexbeforekeyword = Regex(".*(?=$key)")
                    val b = regexbeforekeyword.find(message2)
                    val regexcolorcodes = Regex("§[0-9A-FK-OR]")
                    val colorcodes = regexcolorcodes.findAll(b.toString()).map { it.value }.joinToString("")
                    message2 = message2.replace(key, "$preformatting$key§f$colorcodes")
                }
                }
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(message2))
            }
        }


class ChatEventHandler {
    @SubscribeEvent
    fun onChatReceived(event: ClientChatReceivedEvent) {
        if (event.type.toString() != "2") {
            // Add the received message to the list
            GlobalData.chatMessages.add(event.message.formattedText)
            if (config.enabled) {
                val regex = Regex("\\(\"([^\"]+)\";(\\(?[0-9a-f]+[^)]*\\)?)\\)")
                val result =
                    regex.findAll(config.Wordstring).map { it.groupValues[1] to it.groupValues[2].split(";") }.toMap()
                event.setCanceled(true)
                var modifiedMessage: IChatComponent?
                if(event.message.siblings.toString() == "[]") {
                    modifiedMessage = ChatComponentText(modifyText(event.message.formattedText))
                }
                else {
                    modifiedMessage = modifyChatComponent(event.message)
                    }
                Minecraft.getMinecraft().thePlayer.addChatMessage(modifiedMessage)
            }
        }
    }

    fun modifyChatComponent(component: IChatComponent): IChatComponent {
        // Base case: If the component is a simple text component, modify its text
        if (component is ChatComponentText) {
            val modifiedText = modifyText(component.chatComponentText_TextValue)
            val newComponent = ChatComponentText(modifiedText)
            newComponent.chatStyle = component.chatStyle
            return newComponent
        }

        // Recursive case: If the component has siblings, modify them recursively
        val modifiedSiblings = component.siblings.map { modifyChatComponent(it) }
        val newComponent = component.createCopy()
        newComponent.siblings.clear()
        newComponent.siblings.addAll(modifiedSiblings)
        return newComponent
    }

    fun modifyText(text: String): String {
        val regex = Regex("\\(\"([^\"]+)\";(\\(?[0-9a-f]+[^)]*\\)?)\\)")
        val result =
            regex.findAll(config.Wordstring).map { it.groupValues[1] to it.groupValues[2].split(";") }.toMap()
        var message2 = text
        result.keys.forEach { key ->
            if (key in message2) {
                val preformatting = result[key]?.joinToString(separator = "§", prefix = "§")
                val regexbeforekeyword = Regex(".*(?=$key)")
                val b = regexbeforekeyword.find(message2)
                val regexcolorcodes = Regex("§[0-9A-FK-OR]")
                val colorcodes = regexcolorcodes.findAll(b.toString()).map { it.value }.joinToString("")
                message2 = message2.replace(key, "$preformatting$key§f$colorcodes")
            }
        }
        val modifiedText = message2
        return modifiedText
    }
}