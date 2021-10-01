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

@RestController
@RequestMapping("/selfservice")
class SelfServiceController(
    @Autowired private val jwtTokenService: JwtTokenService
) {

    private val logger: Logger = LoggerFactory.getLogger(SelfServiceController::class.java)

    @GetMapping("info")
    fun info(@Autowired user: Authentication?): Authentication {
        logger.trace("info({})", user)
        user ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No valid token provided")
        return user
    }

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
