package net.darkdevelopers.darkbedrock.darkness.spigot.states

import net.darkdevelopers.darkbedrock.darkness.spigot.countdowns.Countdown
import net.darkdevelopers.darkbedrock.darkness.spigot.listener.game.InGameListener

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 06.07.2018 13:13.
 * Last edit 06.07.2018
 */
class InGameGameState(override val listener: InGameListener) : GameState {
    override val countdown: Countdown? = null
}