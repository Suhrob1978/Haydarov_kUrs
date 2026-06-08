package com.kinopolka.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KinopolkaApplication

fun main(args: Array<String>) {
    runApplication<KinopolkaApplication>(*args)
}
