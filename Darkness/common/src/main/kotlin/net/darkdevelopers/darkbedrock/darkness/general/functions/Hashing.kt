/*
 * © Copyright by Astride UG (haftungsbeschränkt) 2018 - 2019.
 */

@file:JvmName("HashingUtils")

package net.darkdevelopers.darkbedrock.darkness.general.functions

import java.security.MessageDigest

/*
 * Created on 01.05.2019 22:10.
 *
 * Based on the Hashing Utils of Sam Clarke <www.samclarke.com>
 *
 * @author Lars Artmann | LartyHD
 */

private const val HEX_CHARS: String = "0123456789ABCDEF"

fun String.md5(): String = hash("MD5")

fun String.sha1(): String = hash("SHA-1")

fun String.sha224(): String = hash("SHA-224")

fun String.sha256(): String = hash("SHA-256")

fun String.sha384(): String = hash("SHA-384")

fun String.sha512(): String = hash("SHA-512")

private fun String.hash(type: String): String {

    val bytes = MessageDigest
        .getInstance(type)
        .digest(this.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString()
}
