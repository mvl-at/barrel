package at.mvl.barrel.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne

@Entity
class Score(
    @Id var id: Long,
    var title: String,
    @ManyToMany(mappedBy = "scores") var genres: Set<Genre>,
    @ManyToMany(mappedBy = "composerOf") var composers: Set<Author>,
    @ManyToMany(mappedBy = "arrangerOf") var arrangers: Set<Author>,
    var publisher: String,
    @ManyToOne var grade: Grade?,
    var alias: String?,
    var subTitles: String?,
    var annotation: String?,
    var conductorScore: Boolean,
    @ManyToOne var backOf: Score?,

//        @OneToMany(mappedBy = "id") var pages: Set<Page>
)
