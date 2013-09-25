package nz.org.sesa.los.server.controllers

import anorm._
import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import net.liftweb.json
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Extraction._

import nz.org.sesa.los.server.models

object RealmTile extends Controller {
    def view(realmName : String, x : Int, y : Int, adventurerName : Option[String]) = Action { request =>
        models.Realm.getRow(realmName) match {
            case None => {
                NotFound(json.pretty(json.render(
                    ("why" -> s"No such realm.")
                ))).as("application/json")
            }

            case Some(row) => {
                val w = row[Int]("w")
                val h = row[Int]("h")

                val pred = adventurerName.fold { _ : models.Realm.Tile => true } { adventurerName =>
                    models.Adventurer.getRow(adventurerName).fold { _ : models.Realm.Tile => false } { row =>
                        !models.Adventurer.moveDenialFor(row, _).isDefined
                    }
                }

                val exits = List(
                    (0, -1), // north
                    (1, -1), // northeast
                    (1, 0), // east
                    (1, 1), // southeast
                    (0, 1), // south
                    (-1, 1), // southwest
                    (-1, 0), // west
                    (-1, -1) // northwest
                ).map { case (dx, dy) =>
                    val nx = x + dx
                    val ny = y + dy

                    val ni = ny * w + nx

                    if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                        false
                    } else {
                        pred(models.Realm.loadTiles(realmName)(ni))
                    }
                }

                val i = y * w + x
                val tile = models.Realm.loadTiles(realmName)(i)

                Ok(json.pretty(json.render(
                    ("terrain" -> tile.terrain) ~
                    ("features" -> models.Realm.getFeatures(realmName, x, y).map({ row =>
                        ("id" -> row[Int]("id")) ~
                        ("kind" -> row[String]("kind")) ~
                        ("attrs" -> json.parse(row[Option[String]]("attrs").getOrElse("null")))
                    })) ~
                    ("monsters" -> models.Realm.getMonsters(realmName, x, y).map({ row =>
                        ("id" -> row[Int]("id")) ~
                        ("kind" -> row[String]("kind")) ~
                        ("level" -> row[Int]("level")) ~
                        ("drops" -> json.parse(row[String]("drops")))
                    })) ~
                    ("adventurers" -> models.Realm.getAdventurers(realmName, x, y).map({ row =>
                        row[String]("name")
                    }).filter({ name =>
                        adventurerName.fold {true} (_ != name)
                    })) ~
                    ("pos" ->
                        ("x" -> x) ~
                        ("y" -> y) ~
                        ("realm" -> realmName)
                    ) ~
                    ("exits" -> exits)
                ))).as("application/json")
            }
        }
    }
}
