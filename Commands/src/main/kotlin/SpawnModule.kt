import com.google.gson.JsonObject
import net.darkdevelopers.darkbedrock.darkframe.spigot.DarkFrame
import net.darkdevelopers.darkbedrock.darkness.general.configs.ConfigData
import net.darkdevelopers.darkbedrock.darkness.general.configs.gson.GsonConfig
import net.darkdevelopers.darkbedrock.darkness.general.configs.gson.GsonService
import net.darkdevelopers.darkbedrock.darkness.general.configs.gson.GsonStringMapWithSubs
import net.darkdevelopers.darkbedrock.darkness.general.functions.getOrKey
import net.darkdevelopers.darkbedrock.darkness.general.modules.Module
import net.darkdevelopers.darkbedrock.darkness.general.modules.ModuleDescription
import net.darkdevelopers.darkbedrock.darkness.spigot.commands.Command
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.sendIfNotNull
import net.darkdevelopers.darkbedrock.darkness.spigot.listener.Listener
import net.darkdevelopers.darkbedrock.darkness.spigot.messages.SpigotGsonMessages
import net.darkdevelopers.darkbedrock.darkness.spigot.utils.isPlayer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 23.12.2018 16:53.
 * Current Version: 1.0 (23.12.2018 - 23.12.2018)
 */
class SpawnModule : Module {

    override val description: ModuleDescription = ModuleDescription(
        javaClass.canonicalName,
        "1.0",
        "Lars Artmann | LartyHD",
        "Adds SetSpawn and Spawn command"
    )

    private lateinit var config: Config
    private lateinit var listener: org.bukkit.event.Listener
    private var location: Location? = null

    override fun load() {
        config = Config()
    }

    override fun start() {
        SpawnCommand()
        SetSpawnCommand()
        listener = SpawnListener()
    }


    private inner class Config {

        val spawnKey = "spawn"
        val configData = ConfigData(description.folder, "config.json")
        val jsonObject = GsonService.loadAsJsonObject(configData)
        val spawnElement = jsonObject.getOrJsonObject(spawnKey)
        val spawn = GsonConfig.multiPlaceJsonObject(spawnElement, spawnKey, configData.directory)
        val messages = SpigotGsonMessages(GsonConfig(configData).load()).availableMessages
        val permissions = GsonStringMapWithSubs(jsonObject.getAsOrJsonObject("permissions")).available
        val commandNames = GsonStringMapWithSubs(jsonObject.getAsOrJsonObject("command-names")).available

        fun saveLocation(location: Location) {
            spawn.addProperty("World", location.world.name)
            spawn.addProperty("X", location.x)
            spawn.addProperty("Y", location.y)
            spawn.addProperty("Z", location.z)
            spawn.addProperty("Yaw", location.yaw)
            spawn.addProperty("Pitch", location.pitch)
            if (spawnElement.isJsonPrimitive) {
                GsonService.save(ConfigData(description.folder, spawnElement.asString), spawn)
            } else {
                jsonObject.add(spawnKey, spawn)
                GsonService.save(configData, jsonObject)
            }
        }

        fun getLocation(): Location? {
            return Location(
                Bukkit.getWorld(spawn["World"]?.asString ?: return null) ?: return null,
                spawn["X"]?.asDouble ?: return null,
                spawn["Y"]?.asDouble ?: return null,
                spawn["Z"]?.asDouble ?: return null,
                spawn["Yaw"]?.asFloat ?: return null,
                spawn["Pitch"]?.asFloat ?: return null
            )
        }

    }

    private inner class SpawnCommand : Command(
        DarkFrame.instance,
        config.commandNames.getOrKey("Spawn"),
        config.permissions.getOrKey("SpawnNewModule.Command.Spawn")
    ) {

        override fun perform(sender: CommandSender, args: Array<String>) = sender.isPlayer {
            if (location == null) {
                location = config.getLocation()
                if (location == null) {
                    config.messages["Spawn.Teleportation.Failed"].sendIfNotNull(sender)
                    return@isPlayer
                }
            }

            config.messages["Spawn.Teleportation.Success"].sendIfNotNull(sender)
            it.teleport(location)
            config.messages["Spawn.Teleportation.Successfully"].sendIfNotNull(sender)
        }

    }

    private inner class SetSpawnCommand : Command(
        DarkFrame.instance,
        config.commandNames.getOrKey("SetSpawn"),
        config.permissions.getOrKey("SpawnNewModule.Command.SetSpawn")
    ) {

        override fun perform(sender: CommandSender, args: Array<String>) = sender.isPlayer {
            fun Float.round() = (this * 100).toInt() / 100F

            config.messages["Spawn.Set.Success"].sendIfNotNull(sender)

            val cloned = it.location.clone()
            val location = Location(
                cloned.world,
                cloned.blockX + 0.5,
                cloned.y,
                cloned.blockZ + 0.5,
                cloned.yaw.round(),
                cloned.pitch.round()
            )

            config.saveLocation(location)
            this@SpawnModule.location = location

            config.messages["Spawn.Set.Successfully"].sendIfNotNull(sender)
        }

    }

    private inner class SpawnListener : Listener(DarkFrame.instance) {

        private val prefix = "Spawn.Events.Bypass."

        @EventHandler
        fun on(event: BlockBreakEvent) = block(event, event.player)

        @EventHandler
        fun on(event: BlockPlaceEvent) = block(event, event.player)

        @EventHandler
        fun on(event: PlayerPickupItemEvent) = block(event)

        @EventHandler
        fun on(event: PlayerDropItemEvent) = block(event)

        @EventHandler
        fun on(event: PlayerJoinEvent) {
            val player = event.player
            if (!player.hasPlayedBefore() && location != null) player.teleport(location)
            player.changeGameMode(event)
        }

        @EventHandler
        fun on(event: PlayerChangedWorldEvent) = event.player.changeGameMode(event)

        @EventHandler
        fun on(event: EntityDamageEvent) {
            if (event.entity is Player) block(event, event.entity as Player)
        }

        @EventHandler
        fun on(event: FoodLevelChangeEvent) {
            val player = event.entity as Player
            if (event.foodLevel < player.foodLevel) block(event, player)
        }

        @EventHandler
        fun on(event: PlayerInteractEvent) {
            if (event.action != Action.PHYSICAL) return
            block(event)
        }

        private fun block(event: PlayerEvent) = block(event, event.player)

        private fun block(event: Event, player: Player) = check(event, player) { cancel(event as Cancellable) }

        private fun Player.changeGameMode(event: Event) = check(event, this) { gameMode = GameMode.ADVENTURE }

        private inline fun check(event: Event, player: Player, block: () -> Unit) {
            val location = location ?: return
            if (player.world != location.world) return
            if (player.checkPerm(event.permissionsKey())) return
            block()
        }

        private fun CommandSender.checkPerm(permissionsKey: String): Boolean {
            return hasPermission(config.permissions[permissionsKey] ?: return false)
        }


        private fun Event.permissionsKey() = "$prefix${eventName.removeSuffix("Event")}"

    }

    private fun JsonObject.getOrJsonObject(key: String) = this.get(key) ?: JsonObject()

    private fun JsonObject.getAsOrJsonObject(key: String) = this.getAsJsonObject(key) ?: JsonObject()

}