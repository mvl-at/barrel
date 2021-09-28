package at.mvl.barrel.model

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
class ScoreLocation(
    @EmbeddedId var id: ScoreLocationId
)

@Embeddable
class ScoreLocationId(
    var scoreId: Long,
    var roomId: Long
) : Serializable
