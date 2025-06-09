package com.ricoh360.thetableclient

import android.content.Context
import androidx.startup.Initializer

internal lateinit var applicationContext: Context
    private set

object Theta

class ThetaBleInitializer : Initializer<Theta> {

    override fun create(context: Context): Theta {
        applicationContext = context.applicationContext
        return Theta
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
