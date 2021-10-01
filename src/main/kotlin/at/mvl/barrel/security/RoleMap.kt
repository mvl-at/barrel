package at.mvl.barrel.security

import at.mvl.barrel.configuration.BarrelConfigurationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * A simple wrapper for the role mappings.
 */
@Component
class RoleMap(@Autowired private val barrelConfigurationProperties: BarrelConfigurationProperties) {

    /**
     * Return the role map from the properties.
     * @return the role map
     */
    fun roles(): BarrelConfigurationProperties.RoleMappingConfiguration {
        return barrelConfigurationProperties.roles
    }
}
