package net.darkdevelopers.darkbedrock.darkness.spigot.functions

import net.darkdevelopers.darkbedrock.darkness.spigot.utils.Utils
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 22.12.2018 04:55.
 * Current Version: 1.0 (22.12.2018 - 21.03.2019)
 */

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 22.12.2018 04:56.
 * Current Version: 1.0 (22.12.2018 - 22.12.2018)
 */
fun String.toPlayer(): Player? = try {
    Bukkit.getPlayer(UUID.fromString(this))
} catch (ex: IllegalArgumentException) {
    Bukkit.getPlayer(this)
}

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 21.03.2019 01:40.
 * Current Version: 1.0 (21.03.2019 - 21.03.2019)
 */
fun Player.sendPacket(packet: Packet<*>): Unit = (this as CraftPlayer).handle.playerConnection.sendPacket(packet)

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 21.03.2019 01:40.
 * Current Version: 1.0 (21.03.2019 - 21.03.2019)
 */
fun Player.sendActionBar(message: String): Unit =
    sendPacket(PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"$message\"}"), 2.toByte()))

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 21.03.2019 01:44.
 * Current Version: 1.0 (21.03.2019 - 21.03.2019)
 */
fun sendAllParticle(particleType: EnumParticle, loc: Location, speed: Float, amount: Int): Unit =
    Utils.goThroughAllPlayers { it.sendParticle(particleType, loc, speed, amount) }

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 21.03.2019 01:44.
 * Current Version: 1.0 (21.03.2019 - 21.03.2019)
 */
fun Player.sendParticle(particleType: EnumParticle, loc: Location, speed: Float, amount: Int): Unit = player.sendPacket(
    PacketPlayOutWorldParticles(
        particleType,
        true,
        loc.x.toFloat(),
        loc.y.toFloat(),
        loc.z.toFloat(),
        0F,
        0F,
        0F,
        speed,
        amount,
        0
    )
)
