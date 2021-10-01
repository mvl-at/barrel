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

package at.mvl.barrel.model

import java.io.Serializable
import javax.persistence.*

@Entity
class Page(
    @EmbeddedId var id: PageId,
    @ManyToOne @MapsId("scoreId") var score: Score,
    @ManyToOne @MapsId("bookId") var book: Book,
    @Embedded @AttributeOverrides(
        AttributeOverride(
            name = "prefix",
            column = Column(name = "begin_prefix")
        ),
        AttributeOverride(
            name = "number",
            column = Column(name = "begin_number")
        ),
        AttributeOverride(
            name = "suffix",
            column = Column(name = "begin_suffix")
        )
    )
    var begin: PageNumber,

    @Embedded @AttributeOverrides(
        AttributeOverride(
            name = "prefix",
            column = Column(name = "end_prefix")
        ),
        AttributeOverride(
            name = "number",
            column = Column(name = "end_number")
        ),
        AttributeOverride(
            name = "suffix",
            column = Column(name = "end_suffix")
        )
    )
    var end: PageNumber
)

@Embeddable
class PageId(
    var scoreId: Long,
    var bookId: Long
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PageId

        if (scoreId != other.scoreId) return false
        if (bookId != other.bookId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scoreId.hashCode()
        result = 31 * result + bookId.hashCode()
        return result
    }
}

@Embeddable
class PageNumber(
    var prefix: String,
    var number: Long,
    var suffix: String
)
