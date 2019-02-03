package net.darkdevelopers.darkbedrock.darkframe.bungee.commands

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 04.07.2018 02:52.
 * Last edit 04.07.2018
 */
class ModulesCommand(private val moduleManagers: Map<String, ModuleManager>) : Command(
    commandName = "BungeeModules",
    permission = "darkbedrock.darkframe.bungee.commands.modules",
    usage = "[ModuleName]",
    minLength = 0,
    maxLength = 1,
    aliases = *arrayOf("BModules")
) {
    override fun perform(sender: CommandSender, args: Array<String>) = if (args.isEmpty()) sendListOfModules(sender)
    else {
        val module = getModule(args[0])
        if (module == null) sendUseMessage(sender) else sendInfosOfModule(sender, module)
    }

    private fun sendInfosOfModule(sender: CommandSender, module: Module) {
        val description = module.description
        sender.run {
            sendMessage(TextComponent("$PRIMARY$EXTRA$DESIGN                                                               "))
            sendMessage(TextComponent("${Messages.PREFIX}${SECONDARY}Infos of module $EXTRA${description.name}$IMPORTANT:"))
            sendMessage(TextComponent("${Messages.PREFIX}${SECONDARY}Version$IMPORTANT: $SECONDARY$EXTRA${description.version}"))
            sendMessage(TextComponent("${Messages.PREFIX}${SECONDARY}Author$IMPORTANT: $SECONDARY$EXTRA${description.author}"))
            sendMessage(TextComponent("${Messages.PREFIX}${SECONDARY}Description$IMPORTANT: $SECONDARY$EXTRA${description.description}"))
            sendMessage(TextComponent("${Messages.PREFIX}${SECONDARY}Async$IMPORTANT: $SECONDARY$EXTRA${if (description.async) "enable" else "disabled"}"))
            sendMessage(TextComponent("$PRIMARY$EXTRA$DESIGN                                                               "))
        }
    }

    private fun sendListOfModules(sender: CommandSender) {
        sender.sendMessage(TextComponent("$PRIMARY$EXTRA$DESIGN                                                               "))
        for (key in moduleManagers.keys) {
            sender.sendMessage(TextComponent("${Messages.PREFIX}${SECONDARY}Modules from ModuleManager $key$IMPORTANT:"))
            moduleManagers[key]?.modules?.forEach { sender.sendMessage(TextComponent("${Messages.PREFIX}$PRIMARY${it.javaClass.simpleName}")) }
        }
        sender.sendMessage(TextComponent("$PRIMARY$EXTRA$DESIGN                                                               "))
    }

    private fun getModule(moduleName: String): Module? {
        for (value in moduleManagers.values) {
            val module = value.getModule(moduleName)
            if (module != null) return module
        }
        return null
    }
}