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
