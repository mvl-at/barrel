package at.mvl.barrel.security

import at.mvl.barrel.configuration.BarrelConfigurationProperties
import io.jsonwebtoken.*
import io.jsonwebtoken.lang.Strings
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

class JwtTokenService(
    barrelConfigurationProperties: BarrelConfigurationProperties,
    private val userDetailsService: UserDetailsService
) {

    private val logger: Logger = LoggerFactory.getLogger(JwtTokenService::class.java)

    private val jwtConfig: BarrelConfigurationProperties.SecurityConfiguration.JwtConfiguration =
        barrelConfigurationProperties.security.jwt

    fun addTokenToHeader(auth: Authentication, response: HttpServletResponse) {
        logger.trace("addTokenToHeader({},{})")
        response.setHeader(HttpHeaders.AUTHORIZATION, "${jwtConfig.prefix} ${generateToken(auth, false)}")
    }

    fun addRenewalTokenToBody(auth: Authentication, response: HttpServletResponse) {
        logger.trace("addRenewalTokenToBody({},{})", auth, response)
        response.writer.println(generateToken(auth, true))
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/jwt")
    }

    fun authenticationFromToken(request: HttpServletRequest): Authentication? {
        logger.trace("authenticationFromToken({})", request)
        val pair = parseAuthenticationFromRequest(request) ?: return null
        if (pair.second) {
            logger.info("Token from {} is a renewal token, returning null", pair.first.name)
            return null
        }
        return pair.first
    }

    fun authenticationFromRenewalToken(request: HttpServletRequest): Authentication? {
        logger.trace("authenticationFromRenewalToken({})", request)
        val pair = parseAuthenticationFromRequest(request) ?: return null
        if (!pair.second) {
            logger.info("Token from {} is not a renewal token, returning null", pair.first.name)
            return null
        }
        return pair.first
    }

    fun hasAuthorization(request: HttpServletRequest): Boolean {
        return request.getHeader(HttpHeaders.AUTHORIZATION) != null
    }

    private fun generateToken(auth: Authentication, renewal: Boolean): String {
        logger.trace("generateToken({},{})", auth, renewal)
        val inst = Instant.now()
        val exp = inst.plus(if (renewal) jwtConfig.expirationRenewal else jwtConfig.expiration, ChronoUnit.MINUTES)
        return Jwts.builder()
            .setIssuer(jwtConfig.issuer)
            .setSubject(auth.name)
            .setIssuedAt(Date.from(inst))
            .setExpiration(Date.from(exp))
            .claim(jwtConfig.renewalAttribute, renewal)
            .signWith(SignatureAlgorithm.HS256, jwtConfig.secret)
            .compact()
    }

    fun parseAuthenticationFromRequest(request: HttpServletRequest): Pair<Authentication, Boolean>? {
        logger.trace("parseAuthenticationFromRequest({},{})", request, request)
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!Strings.startsWithIgnoreCase(token, "Bearer ")) {
            logger.debug("Token does not start with 'Bearer '")
            return null
        }
        try {
            val jwt = Jwts.parser().setSigningKey(jwtConfig.secret).parseClaimsJws(token.removePrefix("Bearer "))
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
        } catch (e: SignatureException) {
            logger.warn("Received token with invalid signature: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.warn("Received illegal token: {}", e.message)
        }
        return null
    }
}
