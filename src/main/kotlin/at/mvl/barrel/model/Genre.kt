package at.mvl.barrel.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
class Genre(
    @Id var id: Long,
    var name: String,
    @ManyToMany var scores: Set<Score>
)
