package at.mvl.barrel.security

import at.mvl.barrel.configuration.BarrelConfigurationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RoleMap(@Autowired private val barrelConfigurationProperties: BarrelConfigurationProperties) {

    fun roles(): BarrelConfigurationProperties.RoleMappingConfiguration {
        return barrelConfigurationProperties.roles
    }
}
