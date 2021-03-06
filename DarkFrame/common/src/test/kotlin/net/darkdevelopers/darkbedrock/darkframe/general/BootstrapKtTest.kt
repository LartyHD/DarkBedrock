/*
 * © Copyright by Astride UG (haftungsbeschränkt) 2018 - 2019.
 */

package net.darkdevelopers.darkbedrock.darkframe.general

import org.junit.Test
import java.awt.GraphicsEnvironment
import kotlin.concurrent.thread

/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 05.07.2018 12:24.
 * Last edit 09.05.2019
 */
class BootstrapKtTest {
    @Test
    fun display_the_main_method_a_JOptionPane_when_is_graphic() {
        if (GraphicsEnvironment.isHeadless()) return
        //given
        var bool = true

        //when
        thread {
            main()
            bool = false
        }

        //then
        Thread.sleep(2)
        assert(bool)
    }

}