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
) : Serializable
