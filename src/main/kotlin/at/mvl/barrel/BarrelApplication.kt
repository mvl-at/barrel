package at.mvl.barrel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("at.mvl.barrel.configuration")
class BarrelApplication

fun main(args: Array<String>) {
    runApplication<BarrelApplication>(*args)
}
