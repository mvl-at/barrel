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
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Richard Stöckl
 * Filter which filters all requests with (potential) JWTs in the Authorization header.
 * On success, the requests will be authenticated against the [SecurityContext].
 *
 * @param authenticationManager used for [BasicAuthenticationFilter]
 * @property jwtTokenService the token service to parse JWTs
 */
class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtTokenService: JwtTokenService
) :
    BasicAuthenticationFilter(authenticationManager) {

    private val log: Logger = LoggerFactory.getLogger(JwtAuthorizationFilter::class.java)

    /**
     * Attach the [Authentication] to the [SecurityContext] if the [JwtTokenService] parsed the token successfully.
     * If the provided token is a renewal token, the chain will proceed and this filter will do nothing.
     * When providing an invalid token, this filter writes an HTTP Bad Request.
     *
     * @param request the request from where the token should be taken
     * @param response the response where the Bad Request should be written to
     * @param chain the chain which will be used to proceed with other filters
     */
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        log.trace("doFilterInternal({},{},{})", request, response, chain)
        if (!jwtTokenService.hasAuthorization(request)) {
            log.debug("No Authorization, continue")
        } else {
            val authenticationPair = jwtTokenService.parseAuthenticationFromRequest(request)
            if (authenticationPair == null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "provided invalid authorization token")
                return
            }
            if (authenticationPair.second) {
                log.info("{} attached a renewal token, proceed without authentication", authenticationPair.first.name)
            } else {
                SecurityContextHolder.getContext().authentication = authenticationPair.first
            }
        }
        chain.doFilter(request, response)
    }
}
