package com.example.demozio

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import com.example.demozio.modules.{Settings, SettingsLive}
import zio._
import zio.console._
import configuration._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object main  extends App {

  def route = get {
    path("user") {
      val json = """{"email": "test@example.com", "password": "12345"}"""
      complete(HttpEntity(ContentTypes.`application/json`, json))
    }
  }

  def akkaApp = for {
    config <- appConfig
    implicit0(as: ActorSystem) <- actorSystem
    implicit0(am: ActorMaterializer) <- ZIO(ActorMaterializer())
    listener <- ZIO.fromFuture(_ => Http().bindAndHandle(route, "0.0.0.0", config.httpPort))
  } yield listener

  private val env = new SettingsLive with Console.Live {}

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
