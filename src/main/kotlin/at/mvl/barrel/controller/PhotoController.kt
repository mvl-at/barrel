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

/**
 * @author Richard Stöckl
 *
 * Controller used to manage the photos from members.
 */
@RestController
@RequestMapping("\${spring.data.rest.base-path}/photo")
class PhotoController(@Autowired val memberRepository: MemberPhotoRepository) {

    private val logger: Logger = LoggerFactory.getLogger(PhotoController::class.java)

    /**
     * Return the full resolution photo of a member if exists.
     *
     * @param username the username of the member from uid={0} without attribute names
     * @return the binary value of the photo in jpeg
     */
    @GetMapping("{username}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getPhoto(@PathVariable username: String): ByteArray? {
        logger.trace("getPhoto({})", username)
        return memberRepository.findByUsername(username)?.photo
    }

    /**
     * Provide a photo for a member, binary encoded into the request body.
     * A thumbnail will be created and stored too.
     * The photo must be in the jpeg format.
     *
     * @param username the username of the member from uid={0} without attribute names
     * @param photo the binary, jpeg encoded photo from the request body
     */
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
