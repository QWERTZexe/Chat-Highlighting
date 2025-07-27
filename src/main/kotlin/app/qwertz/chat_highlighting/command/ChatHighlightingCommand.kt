package app.qwertz.chat_highlighting.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import app.qwertz.chat_highlighting.ChatHighlighting
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import app.qwertz.chat_highlighting.ChatHighlighting.Companion.config
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
val CHConfig = config

// Check the value of the enable/disable option for the current mod
class IsEnabled {
    fun EnabledCheck(): Boolean {
        if (CHConfig.enabled) {
            return true
        } else {
            return false
        }
    }
}
@Command(value = ChatHighlighting.MODID, description = "Access the " + ChatHighlighting.NAME + " Config")
class ChatHighlightingCommand : CommandBase() {
    override fun getCommandName() = "highlighting"

    override fun getCommandUsage(sender: ICommandSender) = "/highlighting"

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        // Ensure that this command is only executed on the client side
        if (IsEnabled().EnabledCheck()) {
            CHConfig.openGui()
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("§4[§6§lCHAT HIGHLIGHTING§4]§a: The mod is disabled in OneConfig. Please enable it."))
    }}

    // Make sure the command can be used by any player
    override fun canCommandSenderUseCommand(sender: ICommandSender) = true

}