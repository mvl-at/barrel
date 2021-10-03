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

package at.mvl.barrel.controller

import at.mvl.barrel.security.JwtTokenService
import at.mvl.barrel.security.LdapUserPasswordManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Richard Stöckl
 *
 * Controller which provides self-service utilities for members.
 * This includes tasks such as receiving information about the member via a token,
 * changing the own or someone else's password, request a new token from a renewal token, etc...
 */
@RestController
@RequestMapping("/selfservice")
class SelfServiceController(
    @Autowired private val jwtTokenService: JwtTokenService,
    @Autowired private val passwordManager: LdapUserPasswordManager
) {

    private val logger: Logger = LoggerFactory.getLogger(SelfServiceController::class.java)

    /**
     * Request information about a member which corresponds to the current [Authentication].
     *
     * @param user the [Authentication] received from the context
     * @return the json encoded [Authentication] or 'null' if the authentication is invalid
     */
    @GetMapping("info")
    fun info(@Autowired user: Authentication?): Authentication {
        logger.trace("info({})", user)
        user ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No valid token provided")
        return user
    }

    /**
     * Change the own user password.
     * The old password is required for this action.
     * @param oldPassword the old password
     * @param newPassword the new password
     * @param authentication the user whose password should be changed
     */
    @PostMapping(
        "password",
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun changePassword(
        @RequestParam("oldPassword", required = true) oldPassword: String,
        @RequestParam("newPassword", required = true) newPassword: String,
        @Autowired authentication: Authentication
    ) {
        logger.trace("changePassword([PROTECTED],[PROTECTED],{})", authentication)
        logger.info("Change password request for {}", authentication.name)
        if (!passwordManager.changePasswordChecked(authentication.name, oldPassword, newPassword)) {
            logger.info("Change password request for {} failed due to invalid password", authentication.name)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "old password in invalid")
        }
        logger.info("{} changed their password", authentication.name)
    }

    /**
     * Change the password of another user without providing the old one.
     * Requesting this with the own username will result in an HTTP 403.
     * Only member managers are allowed to use this.
     * @param username the user whose password should be changed
     * @param newPassword the new password
     * @param authentication the member manager
     */
    @PostMapping(
        "password/{username}",
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @PreAuthorize("hasRole(@roleMap.roles().memberManager) && !#username.equalsIgnoreCase(authentication.name)")
    fun changeOtherPassword(
        @PathVariable("username") username: String,
        @RequestParam(name = "newPassword", required = true) newPassword: String,
        @Autowired authentication: Authentication
    ) {
        logger.trace("changePassword({},[PROTECTED],{})", username, authentication)
        logger.info("Change password request from {} for {}", authentication.name, username)
        try {
            passwordManager.changePasswordUnchecked(username, newPassword)
        } catch (e: IllegalArgumentException) {
            logger.debug("Cannot find user with username {}: {}", username, e.message)
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        logger.info("Change password request from {} for {} was successful", authentication.name, username)
    }

    /**
     * Request a token renewal.
     * The request must contain a valid renewal token.
     *
     * @param request the request provided by the context
     * @param response the response provided by the context
     */
    @GetMapping("renew")
    fun renew(request: HttpServletRequest, response: HttpServletResponse) {
        logger.trace("renew({})", request)
        val auth = jwtTokenService.authenticationFromRenewalToken(request)
        if (auth == null) {
            logger.info("Invalid token renew request")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token renew")
        }
        jwtTokenService.addTokenToHeader(auth, response)
    }
}
