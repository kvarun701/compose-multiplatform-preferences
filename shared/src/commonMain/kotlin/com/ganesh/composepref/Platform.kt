package com.ganesh.composepref

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform