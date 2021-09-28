package at.mvl.barrel.security

import at.mvl.barrel.configuration.BarrelConfigurationProperties
import io.jsonwebtoken.*
import io.jsonwebtoken.lang.Strings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtConfiguration: BarrelConfigurationProperties.SecurityConfiguration.JwtConfiguration
) :
    BasicAuthenticationFilter(authenticationManager) {

    private val log: Logger = LoggerFactory.getLogger(JwtAuthorizationFilter::class.java)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        log.trace("doFilterInternal({},{},{})", request, response, chain)
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
            log.debug("No Authorization, continue")
        } else {
            val authentication = getAuthentication(request)
            if (authentication == null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "provided invalid authorization token")
                return
            }
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): Authentication? {
        log.trace("getAuthentication({})", request)
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!Strings.startsWithIgnoreCase(token, "Bearer ")) {
            log.debug("Token does not start with 'Bearer '")
            return null
        }
        try {
            val jwt = Jwts.parser().setSigningKey(jwtConfiguration.secret).parseClaimsJws(token.removePrefix("Bearer "))
            log.debug(
                "Received token for {} from {} which expires at {}, issued at {}",
                jwt.body.subject,
                jwt.body.issuer,
                jwt.body.expiration,
                jwt.body.issuedAt
            )
            val user = userDetailsService.loadUserByUsername(jwt.body.subject)
            return UsernamePasswordAuthenticationToken(user.username, null, user.authorities)
        } catch (e: ExpiredJwtException) {
            log.warn(
                "Received expired token from {}, issued by {} at {}, expired at {}: {}",
                e.claims.subject,
                e.claims.issuer,
                e.claims.issuedAt,
                e.claims.expiration,
                e.message
            )
        } catch (e: UnsupportedJwtException) {
            log.warn("Received unsupported token: {}", e.message)
        } catch (e: MalformedJwtException) {
            log.warn("Received malformed token: {}", e.message)
        } catch (e: SignatureException) {
            log.warn("Received token with invalid signature: {}", e.message)
        } catch (e: IllegalArgumentException) {
            log.warn("Received illegal token: {}", e.message)
        }
        return null
    }
}
