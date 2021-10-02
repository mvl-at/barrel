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

import java.security.Key

/**
 * @author Richard Stöckl
 * Interface for providing public and private keys.
 * If possible the implementation should try to retrieve the latest keys when changed.
 */
interface KeyService {

    /**
     * Returns the private key.
     * @return the private key
     */
    fun privateKey(): Key

    /**
     * Returns the public key.
     * @return the public key
     */
    fun publicKey(): Key
}
