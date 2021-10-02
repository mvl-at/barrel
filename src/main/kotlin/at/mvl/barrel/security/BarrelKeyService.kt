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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.security.Key
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * @author Richard Stöckl
 *
 * This service provides the key files as specified in the [BarrelConfigurationProperties].
 * Before every key request, the modification date will be checked and the key refreshed before it will be returned.
 *
 * @param barrelConfigurationProperties the properties to read the key locations from
 * @property privateKeyFile the file where the private key to read from
 * @property publicKeyFile the file where the public key to read from
 * @property privateKey the cached private key
 * @property publicKey the cached public key
 * @property privateKeyLastMod the last known modification timestamp of the public key
 * @property publicKeyLastMod the last known modification timestamp of the public key
 * @constructor the default constructor loads both keys on initialization
 */
@Service
class BarrelKeyService(
    @Autowired barrelConfigurationProperties: BarrelConfigurationProperties
) : KeyService {

    private val logger: Logger = LoggerFactory.getLogger(BarrelKeyService::class.java)

    private val securityConfiguration = barrelConfigurationProperties.security

    private val privateKeyFile = File(securityConfiguration.privateKey)
    private val publicKeyFile = File(securityConfiguration.publicKey)

    private lateinit var privateKey: Key
    private lateinit var publicKey: Key

    private var privateKeyLastMod: Long = 0
    private var publicKeyLastMod: Long = 0

    init {
        reloadPrivateKey()
        reloadPublicKey()
    }

    override fun privateKey(): Key {
        logger.trace("privateKey()")
        if (privateKeyFile.lastModified() > privateKeyLastMod)
            reloadPrivateKey()
        val data = privateKeyFile.readBytes()
        val spec = PKCS8EncodedKeySpec(data)
        logger.debug("Decoded private key: {}", spec)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    override fun publicKey(): Key {
        logger.trace("publicKey()")
        if (publicKeyFile.lastModified() > publicKeyLastMod)
            reloadPublicKey()
        val data = publicKeyFile.readBytes()
        val spec = X509EncodedKeySpec(data)
        logger.debug("Decoded public key: {}", spec)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    /**
     * Reload the private key and set the new last known modification timestamp.
     */
    private fun reloadPrivateKey() {
        logger.trace("reloadPrivateKey()")
        val data = privateKeyFile.readBytes()
        val spec = PKCS8EncodedKeySpec(data)
        logger.debug("Decoded private key: {}", spec)
        privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec)
        privateKeyLastMod = privateKeyFile.lastModified()
        logger.info("Reloaded private key")
    }

    /**
     * Reload the public key and set the new last known modification timestamp.
     */
    private fun reloadPublicKey() {
        logger.trace("reloadPublicKey()")
        val data = publicKeyFile.readBytes()
        val spec = X509EncodedKeySpec(data)
        logger.debug("Decoded public key: {}", spec)
        publicKey = KeyFactory.getInstance("RSA").generatePublic(spec)
        publicKeyLastMod = publicKeyFile.lastModified()
        logger.info("Reloaded public key")
    }
}
