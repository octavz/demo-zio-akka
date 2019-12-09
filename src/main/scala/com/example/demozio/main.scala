package com.example.demozio

import zio._
import zio.console._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._

import akka.stream.ActorMaterializer

import modules._
import models._
import configuration._
import repository._

object main  extends App {

  private val env = new SettingsLive with Repository with Console.Live {
    override val repository: Repository.Service = new RepositoryServiceLive {
      override val xa: doobie.Transactor[Task] = ???
    }
  }

  import io.circe.generic.auto._
  import de.heikoseeberger.akkahttpcirce._
  import FailFastCirceSupport._

  def route = get {
    path("user") {
      val json = """{"email": "test@example.com", "password": "12345"}"""
      complete(HttpEntity(ContentTypes.`application/json`, json))
    }
  } ~
   post {
     path("user") {
       entity(Directives.as[User]) { user =>
         val zio = saveUser(user).provide(env)
         complete(unsafeRunToFuture(zio))
       }
     }
   }

  def akkaApp = for {
    config <- appConfig
    implicit0(as: ActorSystem) <- actorSystem
    implicit0(am: ActorMaterializer) <- ZIO(ActorMaterializer())
    listener <- ZIO.fromFuture(_ => Http().bindAndHandle(route, "0.0.0.0", config.httpPort))
  } yield listener

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
   val zio = for {
     config <- appConfig
     _ <- migration.migrate(
       config.database.schema,
       config.database.jdbcUrl,
       config.database.user,
       config.database.password)
     _ <- akkaApp *> ZIO.never
    } yield ()

    zio.provide(env).foldM(handleError, _ => ZIO.succeed(0))
  }

  def handleError(e: Throwable): UIO[Int] =
    ZIO(e.printStackTrace()).as(1).orDie

}
