package at.mvl.barrel.security

import at.mvl.barrel.configuration.BarrelConfigurationProperties
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
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
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    loginUrl: String,
    private val jwtConfiguration: BarrelConfigurationProperties.SecurityConfiguration.JwtConfiguration
) :
    AbstractAuthenticationProcessingFilter(loginUrl, authenticationManager) {

    private val authConverter = BasicAuthenticationConverter()

    private val log: Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    private val jwtBuilder: JwtBuilder

    init {
        log.trace("init({})", authenticationManager)
        jwtBuilder = Jwts.builder().setIssuer(jwtConfiguration.issuer)
    }

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

    private fun writeAuthorizationRealm(response: HttpServletResponse) {
        log.trace("writeAuthorizationRealm({})", response)
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.addHeader(
            HttpHeaders.WWW_AUTHENTICATE,
            "Basic realm=\"Credentials are required to obtain JWT for Barrel services\""
        )
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        log.trace("successfulAuthentication({},{},{},{})", request, response, chain, authResult)
        val inst = Instant.now()
        val exp = inst.plus(jwtConfiguration.expiration, ChronoUnit.MINUTES)
        val jwt = jwtBuilder.setSubject(authResult.name).setIssuedAt(Date.from(inst))
            .setExpiration(Date.from(exp)).signWith(SignatureAlgorithm.HS256, jwtConfiguration.secret)
            .compact()
        log.trace("Generated token for {} until {}", authResult.name, Date.from(inst))
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
    }
}
