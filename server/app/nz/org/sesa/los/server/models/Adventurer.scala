package nz.org.sesa.los.server.models

import anorm._
import play.api.Play.current
import play.api._
import play.api.db._

object Adventurer {
    def getRow(adventurerName : String) = {
        DB.withConnection { implicit c =>
            val rows = SQL("""SELECT adventurers.id AS id,
                                     adventurers.name AS name,
                                     adventurers.level AS level,
                                     adventurers.x AS x,
                                     adventurers.y AS y,
                                     realms.name AS realm,
                                     realms.w AS w,
                                     realms.h AS h,
                                     adventurers.hp AS hp,
                                     adventurers.xp AS xp
                              FROM adventurers, realms
                              WHERE adventurers.realm_id = realms.id AND
                                    adventurers.name = {name}""").on(
                "name" -> adventurerName
            )

            rows().toList match {
                case Nil => None
                case row::_ => Some(row)
            }
        }
    }

    def moveDenialFor(adventurerRow : Row, tile : Realm.Tile) = {
        val monsters = Realm.getMonsters(adventurerRow[String]("realm"), adventurerRow[Int]("x"), adventurerRow[Int]("y"))
        if (monsters.length > 0) {
            Some(if (monsters.length > 1) "Monsters block your path." else "A monster blocks your path.")
        } else {
            tile.terrain match {
                case "river" | "lake" | "ocean" => Some("You try to flap your wings like a bird to fly over the water, but fail miserably.")
                case "lava" => Some("Um yeah, that's like, lava.")
                case "impassable" => Some("You walk into the wall and, to nobody's surprise, it hurts.")
                case _ => None
            }
        }
    }
}
