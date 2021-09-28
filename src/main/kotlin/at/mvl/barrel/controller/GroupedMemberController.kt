package at.mvl.barrel.controller

import at.mvl.barrel.model.Register
import at.mvl.barrel.repositories.ldap.ExternalMemberRepository
import at.mvl.barrel.repositories.ldap.RegisterRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.naming.ldap.LdapName

@RestController
@RequestMapping(path = ["\${spring.data.rest.base-path}/groupedmembers"])
class GroupedMemberController(
    @Autowired val registerRepository: RegisterRepository,
    @Autowired val memberRepository: ExternalMemberRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(GroupedMemberController::class.java)

    @GetMapping("")
    fun getMembersGroupedByRegister(): List<Register> {
        logger.trace("getMembersGroupedByRegister()")
        val registers = registerRepository.findRegisters().toMutableList()
        registers.forEach { r ->
            r.members = r.allMembers.map { mid ->
                val dn = LdapName(mid)
                val member = memberRepository.findByUsername(dn.getRdn(dn.size() - 1).value.toString())
                if (member == null) {
                    logger.error("Cannot find member with dn={}", mid)
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to find dn")
                }
                member
            }.filter { m -> m.listed != null && m.listed!! }.toMutableList()
            r.members!!.sort()
        }
        registers.sort()
        return registers
    }
}
