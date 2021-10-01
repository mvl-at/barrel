package at.mvl.barrel.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OrderBy

@Entity
class Book(
    @Id var id: Long,
    var name: String,
    var annotation: String,

    @OneToMany(mappedBy = "book")
    @OrderBy("begin.number, begin.prefix, begin.suffix")
    var pages: List<Page>
)
