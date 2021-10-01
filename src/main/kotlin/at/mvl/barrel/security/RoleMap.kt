/* 
 * Barrel, the backend of the Musikverein Leopoldsdorf.
 * Copyright (C) 2021  Richard Stöckl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

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
