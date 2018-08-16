import com.google.gson.JsonArray
import net.darkdevelopers.darkbedrock.darkframe.bungee.DarkFrame
import net.darkdevelopers.darkbedrock.darkness.bungee.commands.Command
import net.darkdevelopers.darkbedrock.darkness.bungee.messages.Colors
import net.darkdevelopers.darkbedrock.darkness.bungee.messages.Messages
import net.darkdevelopers.darkbedrock.darkness.general.configs.ConfigData
import net.darkdevelopers.darkbedrock.darkness.general.configs.gson.GsonConfig
import net.darkdevelopers.darkbedrock.darkness.general.modules.Module
import net.darkdevelopers.darkbedrock.darkness.general.modules.ModuleDescription
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 05.07.2018 13:55.
 * Last edit 15.08.2018
 */
class PingTrackerModule : Module, Listener, Command("PingTracker") {

    override val description: ModuleDescription = ModuleDescription("PingTrackerModule", "1.1", "Lars Artmann | LartyHD", "This module tracks the ping requests")

    private val minutePings = mutableSetOf<String>()
    private val hourPings = mutableSetOf<String>()
    private val dayPings = mutableSetOf<String>()
    private val players = mutableSetOf<UUID>()
    private lateinit var config: GsonConfig

    init {
        logPing(TimeUnit.MINUTES.toMillis(1), "der letzten Minute", minutePings)
        logPing(TimeUnit.HOURS.toMillis(1), "der letzten Stunde", hourPings)
        logPing(TimeUnit.DAYS.toMillis(1), "der letzten 24 Stunden", dayPings)
    }

    override fun start() {
        config = GsonConfig(ConfigData(description.folder))
        config.getAs<JsonArray>("players")?.forEach { players.add(UUID.fromString(it.asString)) }
        ProxyServer.getInstance().pluginManager.registerListener(DarkFrame.instance, this)
    }

    override fun stop() {
        config.put("players", players.toTypedArray())
        config.save()
    }

    override fun perform(sender: CommandSender, args: Array<String>) = isPlayer(sender) {
        val uniqueId = it.uniqueId ?: throw NullPointerException("uniqueId can not be null")
        if (players.contains(uniqueId)) {
            players.remove(uniqueId)
            sender.sendMessage(TextComponent("${Messages.PREFIX}${ChatColor.GRAY}Du bekommst keine weiteren Ping logs"))
        } else {
            players.add(uniqueId)
            sender.sendMessage(TextComponent("${Messages.PREFIX}${ChatColor.GRAY}Du bekommst absofort Ping logs"))
        }
    }

    @EventHandler
    fun onProxyPingEvent(event: ProxyPingEvent) {
        val ip = event.connection?.address?.hostString ?: return
        minutePings.add(ip)
        hourPings.add(ip)
        dayPings.add(ip)
    }

    private fun logPing(sleep: Long, time: String, pings: MutableSet<String>) = thread {
        try {
            while (true) {
                Thread.sleep(sleep)
                val message = "In $time ${when (pings.size) {
                    0 -> "wurden ${Colors.IMPORTANT} keine ${Colors.TEXT}Ping's"
                    1 -> "wurde ${Colors.IMPORTANT} ein ${Colors.TEXT}Ping"
                    else -> "wurden ${Colors.IMPORTANT} ${pings.size} ${Colors.TEXT}Ping's"
                }} dokumentiert"
                println(message)
                players.forEach { ProxyServer.getInstance().getPlayer(it)?.sendMessage(TextComponent(message)) }
                pings.clear()
            }
        } catch (ignored: InterruptedException) {
        }
    }

}