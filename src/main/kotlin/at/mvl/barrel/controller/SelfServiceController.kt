package at.mvl.barrel.controller

import at.mvl.barrel.security.JwtTokenService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
    @Autowired private val jwtTokenService: JwtTokenService
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
