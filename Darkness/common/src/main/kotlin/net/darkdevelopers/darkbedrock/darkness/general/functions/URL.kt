/*
 * © Copyright - Lars Artmann aka. LartyHD 2019.
 */

package net.darkdevelopers.darkbedrock.darkness.general.functions

import java.io.InputStreamReader
import java.net.URL

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 25.04.2019 02:32.
 * Current Version: 1.0 (25.04.2019 - 25.04.2019)
 */
fun getTextFromURL(url: String, timeout: Int = 1000): String? {
    val urlConn = URL(url).openConnection()
    urlConn?.getInputStream() ?: return null
    urlConn.readTimeout = timeout

    InputStreamReader(urlConn.getInputStream()).use {
        return it.readText().ifBlank { null }
    }

}