/*
 * © Copyright - Lars Artmann | LartyHD 2018.
 */
@file:Suppress("DEPRECATION")

package net.darkdevelopers.darkbedrock.darkness.spigot.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.darkdevelopers.darkbedrock.darkness.general.configs.ConfigData
import net.darkdevelopers.darkbedrock.darkness.general.configs.gson.GsonConfig
import net.darkdevelopers.darkbedrock.darkness.general.functions.toNonNull
import net.darkdevelopers.darkbedrock.darkness.spigot.configs.gson.BukkitGsonConfig
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.setWorldBorder
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.setup
import net.darkdevelopers.darkbedrock.darkness.spigot.region.Region
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.concurrent.thread

/**
 * @author Lars Artmann | LartyHD
 * Created by LartyHD on 04.01.2018 20:31.
 * Last edit 12.05.2019
 */
@Deprecated("Use GameMap")
object MapsUtils {
    fun loadMapNames(folder: File): List<String> = GsonConfig(
        ConfigData(
            folder,
            "data.json"
        )
    ).load().getAs<List<String>>("maps")?.apply { if (isEmpty()) throw IndexOutOfBoundsException("No maps in the Config") }
        ?: throw IndexOutOfBoundsException("No maps in the Config")

    fun getRandomMap(mapNames: List<String>): String = if (mapNames.isEmpty())
        throw IndexOutOfBoundsException("No maps in the List")
    else mapNames[Random().nextInt(mapNames.size)]

    fun loadMap(mapName: String): World {
        val world = Bukkit.getWorld(mapName) ?: Bukkit.createWorld(WorldCreator(mapName))
        ?: throw IllegalStateException("world can no be created / loaded")
        world.setup()
        fixBowBug(world)
        return world
    }

    fun equalsBlock(pos1: Location, pos2: Location) =
        pos1.world === pos2.world && pos1.blockX == pos2.blockX && pos1.blockY == pos2.blockY && pos1.blockZ == pos2.blockZ

    private fun fixBowBug(world: World) {
        val entity = world.spawnEntity(Location(world, 0.0, 100.0, 0.0), EntityType.VILLAGER) as LivingEntity
        entity.maxHealth = 1000.0
        entity.health = entity.maxHealth
        thread {
            try {
                Thread.sleep(1000)
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
            entity.remove()
        }
    }

    fun getMapsAndLoad(
        config: BukkitGsonConfig,
        lambda: (Player, MutableMap<UUID, Holograms>, Map) -> Unit
    ): MutableSet<Map> {
        val maps = mutableSetOf<Map>()
        val mapsArray = config.getAs<JsonArray>("maps").toNonNull()
        mapsArray.forEach { maps.add(getMapAndLoad(config, it as JsonObject, lambda)) }
        return maps
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getMapAndLoad(
        config: BukkitGsonConfig,
        jsonObject: JsonObject,
        lambda: (Player, MutableMap<UUID, Holograms>, Map) -> Unit
    ): Map {
        val name = config.getAs<JsonPrimitive>("name", jsonObject)?.asString.toNonNull()
        val worldName = getWorldName(config, jsonObject)
        val world = loadMap(worldName)
        setWorldBoarder(config, "worldBoarder", jsonObject, world)
        val spawn = config.getLocation("spawn", jsonObject, world)
        val hologram = config.getLocationWithOutYawAndPitch("hologram", jsonObject, world)
        return Map(name, spawn, hologram, getRegion(config, jsonObject, world), lambda)
    }

    fun getWorldName(config: BukkitGsonConfig) = config.getAs<JsonPrimitive>("world")?.asString.toNonNull()

    @Suppress("MemberVisibilityCanBePrivate")
    fun getWorldName(config: BukkitGsonConfig, jsonObject: JsonObject) =
        config.getAs<JsonPrimitive>("world", jsonObject)?.asString.toNonNull()

    @Suppress("MemberVisibilityCanBePrivate")
    fun getRegion(config: BukkitGsonConfig, jsonObject: JsonObject, world: World): Region {
        val region = config.getAs<JsonObject>("region", jsonObject).toNonNull()
        val pos1 = config.getLocationWithOutYawAndPitch("pos1", region, world)
        val pos2 = config.getLocationWithOutYawAndPitch("pos2", region, world)
        return Region(pos1, pos2)
    }

    fun getRegion(config: BukkitGsonConfig, jsonObject: JsonObject): Region {
        val region = config.getAs<JsonObject>("region", jsonObject).toNonNull()
        val pos1 = config.getLocationWithOutYawAndPitch("pos1", region)
        val pos2 = config.getLocationWithOutYawAndPitch("pos2", region)
        return Region(pos1, pos2)
    }

    fun getRegion(config: BukkitGsonConfig, world: World): Region {
        val region = config.getAs<JsonObject>("region").toNonNull()
        val pos1 = config.getLocationWithOutYawAndPitch("pos1", region, world)
        val pos2 = config.getLocationWithOutYawAndPitch("pos2", region, world)
        return Region(pos1, pos2)
    }

    fun getRegion(config: BukkitGsonConfig): Region {
        val region = config.getAs<JsonObject>("region").toNonNull()
        val pos1 = config.getLocationWithOutYawAndPitch("pos1", region)
        val pos2 = config.getLocationWithOutYawAndPitch("pos2", region)
        return Region(pos1, pos2)
    }

    fun setWorldBoarder(config: BukkitGsonConfig, key: String, jsonObject: JsonObject, world: World) =
        setWorldBoarder(config, config.getAsNotNull<JsonObject>(key, jsonObject), world)

    @Suppress("MemberVisibilityCanBePrivate")
    fun setWorldBoarder(config: BukkitGsonConfig, key: String, world: World) =
        setWorldBoarder(config, config.getAsNotNull<JsonObject>(key), world)

    @Suppress("MemberVisibilityCanBePrivate")
    fun setWorldBoarder(config: BukkitGsonConfig, jsonObject: JsonObject, world: World) {
        val center = config.getAs<JsonObject>("center", jsonObject).toNonNull()
        val damage = config.getAs<JsonObject>("damage", jsonObject).toNonNull()
        val warning = config.getAs<JsonObject>("warning", jsonObject).toNonNull()
        val size = config.getAs<JsonPrimitive>("size", jsonObject)?.asDouble.toNonNull()
        val x = config.getAs<JsonPrimitive>("x", center)?.asDouble.toNonNull()
        val z = config.getAs<JsonPrimitive>("z", center)?.asDouble.toNonNull()
        val buffer = config.getAs<JsonPrimitive>("buffer", damage)?.asDouble.toNonNull()
        val amount = config.getAs<JsonPrimitive>("amount", damage)?.asDouble.toNonNull()
        val time = config.getAs<JsonPrimitive>("time", warning)?.asInt.toNonNull()
        val distance = config.getAs<JsonPrimitive>("distance", warning)?.asInt.toNonNull()
        setWorldBoarder(world, size, x, z, buffer, amount, distance, time)
    }


    fun setWorldBoarder(config: BukkitGsonConfig, world: World) {
        val center = config.getAs<JsonObject>("center").toNonNull()
        val damage = config.getAs<JsonObject>("damage").toNonNull()
        val warning = config.getAs<JsonObject>("warning").toNonNull()
        val size = config.getAs<JsonPrimitive>("size")?.asDouble.toNonNull()
        val x = config.getAs<JsonPrimitive>("x", center)?.asDouble.toNonNull()
        val z = config.getAs<JsonPrimitive>("z", center)?.asDouble.toNonNull()
        val buffer = config.getAs<JsonPrimitive>("buffer", damage)?.asDouble.toNonNull()
        val amount = config.getAs<JsonPrimitive>("amount", damage)?.asDouble.toNonNull()
        val time = config.getAs<JsonPrimitive>("time", warning)?.asInt.toNonNull()
        val distance = config.getAs<JsonPrimitive>("distance", warning)?.asInt.toNonNull()
        setWorldBoarder(world, size, x, z, buffer, amount, distance, time)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    @Deprecated(
        "Use world.setWorldBorder", ReplaceWith(
            "world.setWorldBorder(size, x, z, buffer, amount, distance, time)",
            "net.darkdevelopers.darkbedrock.darkness.spigot.functions.setWorldBorder"
        )
    )
    fun setWorldBoarder(
        world: World,
        size: Double,
        x: Double,
        z: Double,
        buffer: Double,
        amount: Double,
        distance: Int,
        time: Int
    ): Unit = world.setWorldBorder(size, x, z, buffer, amount, distance, time)
}