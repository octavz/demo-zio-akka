package com.example.demozio

import zio._
import zio.console._
import zio.blocking.Blocking
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.github.mlangc.slf4zio.api._
import modules._
import models._
import configuration._
import repository._
import transactor._

object main extends App with LoggingSupport{
  import io.circe.generic.auto._
  import de.heikoseeberger.akkahttpcirce._
  import FailFastCirceSupport._

  private def env(t: doobie.Transactor[Task]) = new SettingsLive with Repository with Console.Live {
    override val repository: Repository.Service = new RepositoryServiceLive {
      override val xa: doobie.Transactor[Task] = t
    }
  }

  def route(xa: doobie.Transactor[Task]) = get {
    path("user" / Segment) { email =>
      val zio = getUserByEmail(email).provide(env(xa))
      complete(unsafeRunToFuture(zio))
    }
  } ~
    post {
      path("user") {
        entity(Directives.as[User]) { user =>
          val zio = saveUser(user).provide(env(xa))
          complete(unsafeRunToFuture(zio))
        }
      }
    }

  def akkaApp = for {
    config <- appConfig
    implicit0(as: ActorSystem) <- actorSystem
    implicit0(am: ActorMaterializer) <- ZIO(ActorMaterializer())
    xa <- ZIO.access[Repository](_.repository.xa)
    listener <- ZIO.fromFuture(_ => Http().bindAndHandle(route(xa), "0.0.0.0", config.httpPort))
  } yield listener

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    newTransactor.provide(new SettingsLive with Blocking.Live {}).orDie.use { xa =>
      val zio = for {
        config <- appConfig
        _ <- migration.migrate(
          config.database.schema,
          config.database.jdbcUrl,
          config.database.user,
          config.database.password)
        _ <- akkaApp *> ZIO.never
      } yield ()

      zio.provide(env(xa)).foldM(handleError, _ => ZIO.succeed(0))
    }

  def handleError(e: Throwable): UIO[Int] =
    logger.errorIO("Error in main", e).as(1)

}
