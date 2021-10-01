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
