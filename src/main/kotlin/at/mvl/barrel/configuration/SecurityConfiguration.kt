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

package at.mvl.barrel.configuration

import at.mvl.barrel.security.JwtAuthenticationFilter
import at.mvl.barrel.security.JwtAuthorizationFilter
import at.mvl.barrel.security.JwtTokenService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.ldap.core.support.BaseLdapPathContextSource
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator
import org.springframework.security.ldap.userdetails.LdapUserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * @author Richard Stöckl
 *
 * This class is intended to configure security related settings.
 * In this case HTTP filters and the LDAP Server.
 * Furthermore, it provides [LdapUserDetailsService] and [JwtTokenService] as beans.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfiguration(
    @Autowired private val contextSource: BaseLdapPathContextSource,
    @Autowired private val barrelConfigurationProperties: BarrelConfigurationProperties
) : WebSecurityConfigurerAdapter() {

    private val logger: Logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    /**
     * Configure the HTTP security.
     * In this case CORS will be provided, CSRF disabled, [JwtAuthenticationFilter], [JwtAuthorizationFilter] and [UsernamePasswordAuthenticationFilter] registered and the session are set to be provided stateless.
     *
     * @param http the HttpSecurity to configure
     */
    override fun configure(http: HttpSecurity) {
        http.csrf {
            it.disable()
        }.addFilterAt(
            JwtAuthenticationFilter(
                authenticationManager(),
                barrelConfigurationProperties.security.loginPath,
                jwtTokenService()
            ),
            UsernamePasswordAuthenticationFilter::class.java
        ).addFilter(
            JwtAuthorizationFilter(
                authenticationManager(), jwtTokenService()
            )
        )
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
    }

    /**
     * Configure the authentication manager.
     * In this case, LDAP will be configured as authentication source.
     * This includes which users and groups should be fetched and mapped in which way.
     *
     * @param auth the builder to prepare
     */
    override fun configure(auth: AuthenticationManagerBuilder?) {
        logger.trace("configure({})", auth)
        if (auth == null) {
            logger.warn("Unable to configure LDAP for the AuthenticationManager")
            return
        }
        auth.ldapAuthentication()
            .contextSource(contextSource)
            .userSearchBase(barrelConfigurationProperties.ldap.userSearchBase)
            .userSearchFilter(barrelConfigurationProperties.ldap.userSearchFilter)
            .groupSearchBase(barrelConfigurationProperties.ldap.groupSearchBase)
            .groupSearchFilter(barrelConfigurationProperties.ldap.groupSearchFilter)
            .groupRoleAttribute(barrelConfigurationProperties.ldap.groupRoleAttribute)
            .groupSearchSubtree(barrelConfigurationProperties.ldap.groupSearchSubtree)
    }

    /**
     * Configures an [LdapUserDetailsService] from a [BaseLdapPathContextSource] and provides it as a bean.
     * Configures the user/group searches and mappings such as [configure] with the [AuthenticationManagerBuilder].
     *
     * @param contextSource the LDAP context source to use
     * @return the UserDetailsService
     */
    @Bean
    fun ldapUserDetailsService(contextSource: BaseLdapPathContextSource): LdapUserDetailsService {
        logger.trace("ldapUserDetailsService({})", contextSource)
        val filter = FilterBasedLdapUserSearch(
            barrelConfigurationProperties.ldap.userSearchBase,
            barrelConfigurationProperties.ldap.userSearchFilter,
            contextSource
        )
        val authoritiesPopulator =
            DefaultLdapAuthoritiesPopulator(contextSource, barrelConfigurationProperties.ldap.groupSearchBase)
        authoritiesPopulator.setSearchSubtree(barrelConfigurationProperties.ldap.groupSearchSubtree)
        authoritiesPopulator.setGroupRoleAttribute(barrelConfigurationProperties.ldap.groupRoleAttribute)
        authoritiesPopulator.setGroupSearchFilter(barrelConfigurationProperties.ldap.groupSearchFilter)
        return LdapUserDetailsService(filter, authoritiesPopulator)
    }

    /**
     * Provides the [LdapUserDetailsService] as [UserDetailsService]
     *
     * @return the [LdapUserDetailsService]
     */
    override fun userDetailsService(): UserDetailsService {
        return ldapUserDetailsService(contextSource)
    }

    /**
     * Provides the [LdapUserDetailsService] as [UserDetailsService]
     *
     * @return the [LdapUserDetailsService]
     */
    override fun userDetailsServiceBean(): UserDetailsService {
        return userDetailsService()
    }

    /**
     * Provides the [JwtTokenService] as bean.
     * @return the [JwtTokenService]
     */
    @Bean
    fun jwtTokenService(): JwtTokenService {
        return JwtTokenService(barrelConfigurationProperties, userDetailsService())
    }
}
