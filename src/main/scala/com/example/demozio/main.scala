package com.example.demozio

import com.example.demozio.modules.{Settings, SettingsLive}
import zio._
import zio.console._

object main  extends App {

  private val env = new SettingsLive with Console.Live {}

  //add configuration
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
   val zio = for {
     config <- ZIO.access[Settings](_.settings.appConfig)
     port <- config.fold(t => ZIO.fail(t), c => ZIO.succeed(c.httpPort))
      _ <- putStrLn(port.toString)
    } yield ()

    zio.provide(env).foldM(handleError, _ => ZIO.succeed(0))
  }

  def handleError(e: Throwable): UIO[Int] =
    ZIO(e.printStackTrace()).as(1).orDie

}
