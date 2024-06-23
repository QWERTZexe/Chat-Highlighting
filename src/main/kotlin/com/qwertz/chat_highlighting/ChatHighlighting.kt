package com.qwertz.chat_highlighting

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import com.qwertz.chat_highlighting.command.ChatHighlightingCommand
import com.qwertz.chat_highlighting.config.ChatHighlightingConfig
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod(modid = ChatHighlighting.MODID, name = ChatHighlighting.NAME, version = ChatHighlighting.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter")
object ChatHighlighting {
    // Register the config and commands.

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ChatHighlightingConfig
        CommandManager.INSTANCE.registerCommand(ChatHighlightingCommand())
        MinecraftForge.EVENT_BUS.register(this)
    }

    const val MODID: String = "@ID@"
    const val NAME: String = "@NAME@"
    const val VERSION: String = "@VER@"
    private val gson = Gson()
    private val regex = Regex("\\(\"([^\"]+)\";(\\(?[0-9a-f]+[^)]*\\)?)\\)")
    private val regexColorCodes = Regex("§[0-9A-FK-OR]")

    @SubscribeEvent
    fun onChatReceived(e: ClientChatReceivedEvent) {
        // Add the received message to the list
        if (e.type.toInt() == 2) return
        if (ChatHighlightingConfig.enabled) {
            gson.fromJson(IChatComponent.Serializer.componentToJson(e.message), JsonObject::class.java).let {
                handle(it)
                e.message = IChatComponent.Serializer.jsonToComponent(gson.toJson(it))
            }
        }
    }

    fun handle(jsonObject: JsonObject): JsonObject {
        if (jsonObject.has("extra")) {
            for (array in jsonObject.getAsJsonArray("extra")) {
                if (array.isJsonObject) {
                    handle(array.asJsonObject)
                }
            }
        }
        if (jsonObject.has("text")) {
            jsonObject.addProperty("text", handleText(jsonObject.get("text").asString))
        }
        return jsonObject
    }

    fun handleText(text: String): String {
        val result = regex.findAll(ChatHighlightingConfig.Wordstring).map { it.groupValues[1] to it.groupValues[2].split(";") }.toMap()
        val finalText = text
        result.keys.forEach { key ->
            if (key in text) {
                val preformatting = result[key]?.joinToString(separator = "§", prefix = "§")
                val b = Regex(".*(?=$key)").find(finalText)
                val colorcodes = regexColorCodes.findAll(b.toString()).map { it.value }.joinToString("")
                return finalText.replace(key, "$preformatting$key§f$colorcodes")
            }
        }
        return finalText
    }
}
