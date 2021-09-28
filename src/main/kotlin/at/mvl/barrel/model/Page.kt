package at.mvl.barrel.model

import java.io.Serializable
import javax.persistence.*

@Entity
class Page(
    @EmbeddedId var id: PageId,
    @ManyToOne @MapsId("scoreId") var score: Score,
    @ManyToOne @MapsId("bookId") var book: Book,
    var beginPrefix: String,
    var beginNumber: Long,
    var beginSuffix: String,
    var endPrefix: String,
    var endNumber: Long,
    var endSuffix: String
)

@Embeddable
class PageId(
    var scoreId: Long,
    var bookId: Long
) : Serializable
