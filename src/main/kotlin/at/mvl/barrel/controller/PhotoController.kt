package at.mvl.barrel.controller

import at.mvl.barrel.model.MemberPhoto
import at.mvl.barrel.repositories.ldap.MemberPhotoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@RestController
@RequestMapping(path = ["\${spring.data.rest.base-path}/photo"])
class PhotoController(@Autowired val memberRepository: MemberPhotoRepository) {

    private val logger: Logger = LoggerFactory.getLogger(PhotoController::class.java)

    @GetMapping("{username}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getPhoto(@PathVariable username: String): ByteArray? {
        logger.trace("getPhoto({})", username)
        return memberRepository.findByUsername(username)?.photo
    }

    @RequestMapping(path = ["{username}"], method = [RequestMethod.PUT], consumes = [MediaType.IMAGE_JPEG_VALUE])
    fun putPhoto(@PathVariable username: String, @RequestBody photo: ByteArray?) {
        logger.trace("putPhoto({},{})", username, if (photo == null) "null" else "not null")
        val member = memberRepository.findByUsername(username)
        if (member == null) {
            logger.debug("No member with username: {}", username)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no such username")
        }
        logger.info("Update photo ({} bytes) of member: {}", photo?.size, username)
        member.photo = photo
        val storedMember = memberRepository.save(member)
        logger.debug("Stored photo ({} bytes) of member: {}", storedMember.photo?.size, storedMember.id)
    }

    private fun createThumbnail(photo: ByteArray?): ByteArray? {
        logger.trace("createThumbnail({} bytes)", photo?.size)
        if (photo == null) return null
        val originalImage = ImageIO.read(photo.inputStream())
        val height = originalImage.height * MemberPhoto.THUMBNAIL_WIDTH / originalImage.width
        val scaledImage =
            originalImage.getScaledInstance(MemberPhoto.THUMBNAIL_WIDTH, height, BufferedImage.SCALE_SMOOTH)
        val scaledBufferedImage = BufferedImage(MemberPhoto.THUMBNAIL_WIDTH, height, BufferedImage.TYPE_INT_RGB)
//        ImageIO.
//        logger.debug("Thumbnail size is {}x{}", scaledImage.)
        return null
    }
}