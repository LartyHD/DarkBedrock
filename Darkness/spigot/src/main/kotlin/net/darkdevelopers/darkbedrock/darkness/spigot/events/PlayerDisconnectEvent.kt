/*
 * © Copyright by Astride UG (haftungsbeschränkt) 2018 - 2019.
 */
package net.darkdevelopers.darkbedrock.darkness.spigot.events

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Created by LartyHD on 29.11.2017 14:21.
 * Last edit 05.05.2019
 */
class PlayerDisconnectEvent(who: Player, message: String) : PlayerEvent(who) {
    var leaveMessage: String? = message

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic //Important for Bukkit due to the Java ByteCode
        val handlerList: HandlerList = HandlerList()
    }
}
