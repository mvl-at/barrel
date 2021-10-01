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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Richard Stöckl
 *
 * A filter which filters the configured login path and tests for HTTP Basic auth.
 * When valid credentials were provided, a jwt will be generated.
 *
 * @param authenticationManager the authentication manager which should be used for testing the credentials
 * @param loginUrl the path on which this filter should listen on
 * @property jwtTokenService the service which should be used to generate the JWTs
 * @property authConverter used to parse the HTTP Basic auth
 */
class JwtAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    loginUrl: String,
    private val jwtTokenService: JwtTokenService
) :
    AbstractAuthenticationProcessingFilter(loginUrl, authenticationManager) {

    private val authConverter = BasicAuthenticationConverter()

    private val log: Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    /**
     * Try to authenticate this request against the authentication manager using HTTP Basic auth.
     * If no token is provided, the response will contain an HTTP Basic realm header which causes web-browsers to show a simple login dialog.
     * @param request the request which should contain the HTTP Basic auth
     * @param response the response which will be used to write the HTTP Basic realm header for a login prompt
     * @return the authentication of the request or 'null' if authentication was not successful
     */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        log.trace("attemptAuthentication({},{})", request, response)
        val token: UsernamePasswordAuthenticationToken?
        try {
            token = authConverter.convert(request)
        } catch (e: BadCredentialsException) {
            log.debug("Invalid Basic Auth format: {}", e.message)
            return null
        }
        if (token == null) {
            log.debug("No credentials provided")
            writeAuthorizationRealm(response)
            return null
        }
        log.debug("Valid Basic Auth format for {}", token.name)
        return authenticationManager.authenticate(token)
    }

    /**
     * Write the HTTP Basic authorization realm to a response and set the correct (401 - Unauthorized) HTTP status code.
     * @param response the response where to write the realm
     */
    private fun writeAuthorizationRealm(response: HttpServletResponse) {
        log.trace("writeAuthorizationRealm({})", response)
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.addHeader(
            HttpHeaders.WWW_AUTHENTICATE,
            "Basic realm=\"Credentials are required to obtain JWT for Barrel services\""
        )
    }

    /**
     * Write the JWT token to the HTTP Authorization header and the renewal JWT token to the response body.
     * @param request not used here
     * @param response the response where to write the JWTs to
     * @param chain not used here
     * @param authResult the authentication to use for the JWTs
     */
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        log.trace("successfulAuthentication({},{},{},{})", request, response, chain, authResult)
        jwtTokenService.addTokenToHeader(authResult, response)
        jwtTokenService.addRenewalTokenToBody(authResult, response)
    }
}
