package com.hrm.latex

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform