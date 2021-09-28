package at.mvl.barrel.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestController
@RestControllerAdvice("/selfservice")
class SelfServiceController {

    private val logger: Logger = LoggerFactory.getLogger(SelfServiceController::class.java)

    @GetMapping("info")
    fun info(@Autowired user: Authentication): Authentication {
        logger.trace("info({})", user)
        return user
    }
}
