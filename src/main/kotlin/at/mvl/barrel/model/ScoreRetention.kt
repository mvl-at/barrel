package at.mvl.barrel.model

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
class ScoreRetention(
    @EmbeddedId var id: ScoreRetentionId
)

@Embeddable
class ScoreRetentionId(
    var scoreId: Long,
    var scoreLocationId: Long
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoreRetentionId

        if (scoreId != other.scoreId) return false
        if (scoreLocationId != other.scoreLocationId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scoreId.hashCode()
        result = 31 * result + scoreLocationId.hashCode()
        return result
    }
}
