package at.mvl.barrel.configuration

import at.mvl.barrel.model.ExternalMember
import at.mvl.barrel.repositories.ldap.ExternalMemberRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.core.convert.support.GenericConversionService
import org.springframework.data.ldap.repository.config.EnableLdapRepositories
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper
import org.springframework.ldap.odm.typeconversion.impl.ConversionServiceConverterManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct
import javax.naming.ldap.LdapName

@Configuration
@EnableLdapRepositories(basePackages = ["at.mvl.barrel.repositories.ldap.**"])
class LdapConfiguration(@Autowired val ldap: LdapTemplate, @Autowired val memberRepository: ExternalMemberRepository) {

    private val logger: Logger = LoggerFactory.getLogger(LdapConfiguration::class.java)

    @PostConstruct
    fun conversionService(): DefaultConversionService {
        val odm = ldap.objectDirectoryMapper
        val conversionService = DefaultConversionService()
        if (odm !is DefaultObjectDirectoryMapper) {
            logger.warn("ODM is not DefaultObjectDirectoryMapper, cannot add custom converters")
            return conversionService
        }
        registerConverter(
            conversionService,
            GeneralizedTimeToLocalDateConverter(),
            String::class.java,
            LocalDate::class.java
        )
        registerConverter(
            conversionService,
            LocalDateToGeneralizedTimeConverter(),
            LocalDate::class.java,
            String::class.java
        )
        registerConverter(
            conversionService,
            DnToExternalMemberConverter(memberRepository),
            String::class.java,
            ExternalMember::class.java
        )
        registerConverter(
            conversionService,
            ExternalMemberToStringConverter(),
            ExternalMember::class.java,
            String::class.java
        )
        odm.setConverterManager(ConversionServiceConverterManager(conversionService))
        ldap.objectDirectoryMapper = odm
        return conversionService
    }

    private fun registerConverter(
        conversionService: GenericConversionService,
        converter: Converter<*, *>,
        from: Class<*>,
        to: Class<*>
    ) {
        logger.debug("Register converter {}: {} -> {}", converter.javaClass, from, to)
        conversionService.addConverter(converter)
    }
}

class GeneralizedTimeToLocalDateConverter : Converter<String, LocalDate> {

    private val logger: Logger = LoggerFactory.getLogger(GeneralizedTimeToLocalDateConverter::class.java)
    private val format = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX")

    override fun convert(source: String): LocalDate? {
        logger.trace("convert({})", source)
        return LocalDate.parse(source, format)
    }
}

class LocalDateToGeneralizedTimeConverter : Converter<LocalDate, String> {

    private val logger: Logger = LoggerFactory.getLogger(LocalDateToGeneralizedTimeConverter::class.java)
    private val format = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX")

    override fun convert(source: LocalDate): String? {
        logger.trace("convert({})", source)
        return source.format(format)
    }
}

class DnToExternalMemberConverter(private val memberRepository: ExternalMemberRepository) :
    Converter<String, ExternalMember> {
    override fun convert(source: String): ExternalMember? {
        if (source == "") {
            return null
        }
        return memberRepository.findById(LdapName(source)).orElse(null)
    }
}

class ExternalMemberToStringConverter : Converter<ExternalMember, String> {
    override fun convert(source: ExternalMember): String {
        return source.id.toString()
    }
}
