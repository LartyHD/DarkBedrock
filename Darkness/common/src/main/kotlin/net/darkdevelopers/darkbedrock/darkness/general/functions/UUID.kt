/*
 * © Copyright by Astride UG (haftungsbeschränkt) and Lars Artmann | LartyHD 2019.
 */

@file:Suppress("unused")

package net.darkdevelopers.darkbedrock.darkness.general.functions

import java.util.*

/*
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 24.05.2019 01:52.
 * Last edit 24.05.2019
 */

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 24.05.2019 01:46.
 *
 * @throws IllegalArgumentException if string has not 4 `-`
 * @throws NumberFormatException if string has `--` or strings between `-` dont match with `[0-9]`
 *
 * Current Version: 1.0 (24.05.2019 - 24.05.2019)
 */
fun String.toUUID(): UUID = UUID.fromString(this)

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 24.05.2019 02:04.
 * Current Version: 1.0 (24.05.2019 - 24.05.2019)
 */
fun String.toUUIDOrNull(): UUID? = try {
    this.toUUID()
} catch (ex: IllegalArgumentException) {
    null
} catch (ex: NumberFormatException) {
    null
}