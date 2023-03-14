package com.ricoh360.thetableclient

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
