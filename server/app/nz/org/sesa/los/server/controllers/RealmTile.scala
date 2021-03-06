package nz.org.sesa.los.server.controllers

import anorm._
import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import net.liftweb.json
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Extraction._

import nz.org.sesa.los.server.Position
import nz.org.sesa.los.server.util
import nz.org.sesa.los.server.models

object RealmTile extends Controller {
    def view(realmName : String, x : Int, y : Int) = Action { request =>
        DB.withTransaction { implicit c =>
            models.Realm.getRow(realmName) match {
                case None => {
                    NotFound(json.pretty(json.render(
                        ("why" -> s"No such realm.")
                    ))).as("application/json")
                }

                case Some(row) => {
                    val w = row[Int]("realms.w")
                    val h = row[Int]("realms.h")

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
                            !models.Adventurer.moveDenialFor(new Position(x, y, realmName),
                                                             models.Realm.loadTiles(realmName)(ni)).isDefined
                        }
                    }

                    val i = y * w + x
                    val tile = models.Realm.loadTiles(realmName)(i)

                    Ok(json.pretty(json.render(
                        ("terrain" -> tile.terrain) ~
                        ("features" -> models.Realm.getFeatures(realmName, x, y).map({ row =>
                            ("id" -> row[Int]("features.id")) ~
                            ("kind" -> row[String]("features.kind")) ~
                            ("attrs" -> json.parse(row[Option[String]]("features.attrs").getOrElse("null")))
                        })) ~
                        ("monsters" -> models.Realm.getMonsters(realmName, x, y).map({ row =>
                            ("id" -> row[Int]("monsters.id")) ~
                            ("kind" -> row[String]("monsters.kind")) ~
                            ("hearts" -> row[Int]("monsters.hearts")) ~
                            ("maxHearts" -> row[Int]("monsters.max_hearts")) ~
                            ("drops" -> json.parse(row[String]("monsters.drops")))
                        })) ~
                        ("adventurers" -> models.Realm.getAdventurers(realmName, x, y).map({ row =>
                            row[String]("adventurers.name")
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
}
