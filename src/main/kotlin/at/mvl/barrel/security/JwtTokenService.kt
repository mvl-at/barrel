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
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.lang.Strings
import io.jsonwebtoken.security.SecurityException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Richard Stöckl
 * A service which generates and parses JWT tokens.
 *
 * @param barrelConfigurationProperties the properties for setting the JWT parameters
 * @property userDetailsService the [UserDetailsService] to fetch user information on token parsing
 * @property jwtConfig a shorthand for the JWT properties
 */
class JwtTokenService(
    barrelConfigurationProperties: BarrelConfigurationProperties,
    private val userDetailsService: UserDetailsService,
    private val keyService: KeyService
) {

    private val logger: Logger = LoggerFactory.getLogger(JwtTokenService::class.java)

    private val jwtConfig: BarrelConfigurationProperties.SecurityConfiguration.JwtConfiguration =
        barrelConfigurationProperties.security.jwt

    /**
     * Write a token generated from the [auth] to the [response] Authorization Header.
     * @param auth the authentication to use
     * @param response where the token should be written to
     */
    fun addTokenToHeader(auth: Authentication, response: HttpServletResponse) {
        logger.trace("addTokenToHeader({},{})")
        response.setHeader(HttpHeaders.AUTHORIZATION, "${jwtConfig.prefix} ${generateToken(auth, false)}")
    }

    /**
     * Write a renewal token generated from the [auth] to the [response] body.
     * @param auth the authentication to use
     * @param response where the token should be written to
     */
    fun addRenewalTokenToBody(auth: Authentication, response: HttpServletResponse) {
        logger.trace("addRenewalTokenToBody({},{})", auth, response)
        response.writer.println(generateToken(auth, true))
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/jwt")
    }

    /**
     * Parse the authentication token from a request and return it if it is a normal token.
     * @see parseAuthenticationFromRequest
     *
     * @param request the request from which to parse the token
     * @return an authentication if the [request] contains a valid token and 'null' otherwise
     */
    fun authenticationFromToken(request: HttpServletRequest): Authentication? {
        logger.trace("authenticationFromToken({})", request)
        val pair = parseAuthenticationFromRequest(request) ?: return null
        if (pair.second) {
            logger.info("Token from {} is a renewal token, returning null", pair.first.name)
            return null
        }
        return pair.first
    }

    /**
     * Parse the authentication token from a request and return it if it is a renewal token.
     * @see parseAuthenticationFromRequest
     *
     * @param request the request from which to parse the renewal token
     * @return an authentication if the [request] contains a valid renewal token and 'null' otherwise
     */
    fun authenticationFromRenewalToken(request: HttpServletRequest): Authentication? {
        logger.trace("authenticationFromRenewalToken({})", request)
        val pair = parseAuthenticationFromRequest(request) ?: return null
        if (!pair.second) {
            logger.info("Token from {} is not a renewal token, returning null", pair.first.name)
            return null
        }
        return pair.first
    }

    /**
     * Test if the provided request contains an Authorization attempt.
     * @param request the request to test
     * @return 'true' if the [request] contains an Authorization attempt and 'false' otherwise
     */
    fun hasAuthorization(request: HttpServletRequest): Boolean {
        return request.getHeader(HttpHeaders.AUTHORIZATION) != null
    }

    /**
     * Generate a token for a given [Authentication] as configured in the provided [jwtConfig].
     *
     * @param auth the authentication to use
     * @param renewal whether the resulting token should be a renewal token or not
     * @return the resulting JWT token
     */
    private fun generateToken(auth: Authentication, renewal: Boolean): String {
        logger.trace("generateToken({},{})", auth, renewal)
        val inst = Instant.now()
        val exp = inst.plus(if (renewal) jwtConfig.expirationRenewal else jwtConfig.expiration, ChronoUnit.MINUTES)
        return Jwts.builder().signWith(keyService.privateKey())
            .setIssuer(jwtConfig.issuer)
            .setSubject(auth.name)
            .setIssuedAt(Date.from(inst))
            .setExpiration(Date.from(exp))
            .claim(jwtConfig.renewalAttribute, renewal)
            .compact()
    }

    /**
     * Parse a token from a request using the Authorization Header.
     * This includes both, normal and renewal tokens.
     * Iff the parsed token is a renewal token, the second parameter of the returned [Pair] will be 'true' and 'false' otherwise.
     *
     * The first parameter of the returned [Pair] is the [Authentication] for the username provided in the token.
     * The result will be 'null' in case of:
     *  * the value of the Authorization Header does not start with 'Bearer '
     *  * the token is expired
     *  * the provided value is no token
     *  * the signature of the token is invalid
     *  * the value after 'Bearer ' is empty
     *
     * @return the result of this parse attempt
     */
    fun parseAuthenticationFromRequest(request: HttpServletRequest): Pair<Authentication, Boolean>? {
        logger.trace("parseAuthenticationFromRequest({},{})", request, request)
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!Strings.startsWithIgnoreCase(token, "Bearer ")) {
            logger.debug("Token does not start with 'Bearer '")
            return null
        }
        try {
            val jwt = Jwts.parserBuilder().setSigningKey(keyService.publicKey()).build().parseClaimsJws(token.removePrefix("Bearer "))
            val isRenewal = jwt.body[jwtConfig.renewalAttribute] == true
            logger.debug(
                "Received {}token for {} from {} which expires at {}, issued at {}",
                if (isRenewal) "renewal-" else "",
                jwt.body.subject,
                jwt.body.issuer,
                jwt.body.expiration,
                jwt.body.issuedAt
            )
            val user = userDetailsService.loadUserByUsername(jwt.body.subject)
            return Pair(UsernamePasswordAuthenticationToken(user.username, null, user.authorities), isRenewal)
        } catch (e: ExpiredJwtException) {
            logger.warn(
                "Received expired token from {}, issued by {} at {}, expired at {}: {}",
                e.claims.subject,
                e.claims.issuer,
                e.claims.issuedAt,
                e.claims.expiration,
                e.message
            )
        } catch (e: UnsupportedJwtException) {
            logger.warn("Received unsupported token: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.warn("Received malformed token: {}", e.message)
        } catch (e: SecurityException) {
            logger.warn("Received token with invalid signature: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.warn("Received illegal token: {}", e.message)
        }
        return null
    }
}
