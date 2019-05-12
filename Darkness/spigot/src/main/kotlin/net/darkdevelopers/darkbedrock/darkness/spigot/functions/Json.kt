package net.darkdevelopers.darkbedrock.darkness.spigot.functions

import com.google.gson.*

/*
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 09.05.2019 14:16.
 * Current Version: 1.0 (09.05.2019 - 09.05.2019)
 */

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 09.05.2019 12:52.
 * Current Version: 1.0 (09.05.2019 - 09.05.2019)
 */
@Suppress("FunctionName")
fun JsonArray(input: Iterable<JsonElement>): JsonArray = JsonArray().apply { input.forEach { add(it) } }

fun JsonObject(input: Map<String, JsonElement>): JsonObject =
    JsonObject().apply { input.entries.forEach { add(it.key, it.value) } }

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 09.05.2019 13:38.
 * Current Version: 1.0 (09.05.2019 - 09.05.2019)
 */
fun JsonObject.toMap(): Map<String, Any?> = entrySet().map { (key, value) -> key to value.toObject() }.toMap()

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 09.05.2019 14:27.
 * Current Version: 1.0 (09.05.2019 - 09.05.2019)
 */
fun Map<String, JsonElement>.toJsonObject(): JsonObject = JsonObject().apply {
    this@toJsonObject.forEach { (value, key) -> add(value, key) }
}

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 09.05.2019 14:13.
 *
 * only Boolean, Number, String, Char are allowed
 *
 * Current Version: 1.0 (09.05.2019 - 09.05.2019)
 */
fun Any.toJsonPrimitive(): JsonPrimitive? = when (this) {
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Char -> JsonPrimitive(this)
    else -> null
}

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 09.05.2019 14:11.
 * Current Version: 1.0 (09.05.2019 - 09.05.2019)
 */
fun JsonElement.toObject(): Any? = when (this) {
    is JsonNull -> null
    is JsonPrimitive -> when {
        isBoolean -> asBoolean
        isNumber -> asNumber
        isString -> asString
        else -> IllegalStateException("JsonPrimitive value can not be anyone else as Boolean, Number or String")
    }
    is JsonArray -> this.toList()
    is JsonObject -> this.toMap()
    else -> this
}

fun Any?.toJsonElement(serializeNull: Boolean = false): JsonElement? = when (this) {
    null -> if (serializeNull) JsonNull.INSTANCE else null
    is Iterable<*> -> JsonArray(this.mapNotNull { it?.toJsonPrimitive() })
    //TODO     is Map<String, Any?> -> JsonObject()
    else -> toJsonPrimitive()
}

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 09.05.2019 22:18.
 * Current Version: 1.0 (09.05.2019 - 09.05.2019)
 */
fun Map<String, Any?>.toJsonObject(serializeNull: Boolean = false): JsonObject = mapNotNull { (key, value) ->
    (key to value.toJsonElement(serializeNull)).toSecondNotNull()
}.toMap().toJsonObject()