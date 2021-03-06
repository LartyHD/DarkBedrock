import net.darkdevelopers.darkbedrock.darkframe.spigot.DarkFrame
import net.darkdevelopers.darkbedrock.darkness.general.modules.Module
import net.darkdevelopers.darkbedrock.darkness.general.modules.ModuleDescription
import net.darkdevelopers.darkbedrock.darkness.spigot.commands.Command
import net.darkdevelopers.darkbedrock.darkness.spigot.events.PlayerDisconnectEvent
import net.darkdevelopers.darkbedrock.darkness.spigot.listener.Listener
import net.darkdevelopers.darkbedrock.darkness.spigot.utils.isPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

/*
 * © Copyright - Lars Artmann aka. LartyHD 2018.
 */

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 28.12.2018 16:28.
 * Current Version: 1.0 (28.12.2018 - 28.12.2018)
 */
class MsgModule : Module, Listener(DarkFrame.instance) {

    override val description: ModuleDescription = ModuleDescription(
        javaClass.canonicalName,
        "1.0-SNAPSHOT",
        "Lars Artmann | LartyHD and DevSnox",
        "Msg System"
    )

    private val map = mutableMapOf<UUID, UUID>()
    private val split = "${ChatColor.WHITE}: "
    private val prefix = "${ChatColor.AQUA}MSG$split${ChatColor.GREEN}"
    private val arrow = " ${ChatColor.WHITE}->${ChatColor.GREEN} "

    override fun start() {
        MsgCommand()
        ReplyCommand()
        InnerListener()
    }

    private inner class InnerListener : Listener(DarkFrame.instance) {

        @EventHandler
        fun on(event: PlayerDisconnectEvent) {
            map.remove(event.player.uniqueId)
            map.toMap().forEach { (t, u) -> if (u == event.player.uniqueId) map.remove(t) }
        }

    }

    private inner class MsgCommand : Command(
        DarkFrame.instance,
        "MSG",
        minLength = 2,
        maxLength = Int.MAX_VALUE,
        usage = "<Player> <Message>"
    ) {

        override fun perform(sender: CommandSender, args: Array<String>) = sender.isPlayer { player ->
            getTarget(sender, args[0]) { sendMSG(player, it, args) }
        }

    }

    private inner class ReplyCommand : Command(
        DarkFrame.instance,
        "Reply",
        minLength = 1,
        maxLength = Int.MAX_VALUE,
        usage = "<Message>",
        aliases = *arrayOf("r")
    ) {

        override fun perform(sender: CommandSender, args: Array<String>) = sender.isPlayer { player ->
            val target = Bukkit.getPlayer(map[player.uniqueId])
            if (target == null) sender.sendMessage("${prefix}Dir wurde noch nicht geschrieben oder der Spieler ist offline")
            else sendMSG(player, target, args)
        }

    }

    private fun sendMSG(player: Player, target: Player, args: Array<String>) {
        val messages = args.drop(1).joinToString(" ")
        map[target.uniqueId] = player.uniqueId
        target.sendMessage("${prefix}Du$arrow${player.name}$split$messages")
        player.sendMessage("$prefix${target.name}${arrow}Du$split$messages")
    }


}