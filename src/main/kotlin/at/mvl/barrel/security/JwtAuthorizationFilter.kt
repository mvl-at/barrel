package at.mvl.barrel.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtTokenService: JwtTokenService
) :
    BasicAuthenticationFilter(authenticationManager) {

    private val log: Logger = LoggerFactory.getLogger(JwtAuthorizationFilter::class.java)

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
