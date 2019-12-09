package com.example.demozio

import com.example.demozio.modules.{Settings, SettingsLive}
import zio._
import zio.console._
import configuration._

object main  extends App {

  private val env = new SettingsLive with Console.Live {}

  //add configuration
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
   val zio = for {
     config <- appConfig
      _ <- putStrLn(config.httpPort.toString)
    } yield ()

    zio.provide(env).foldM(handleError, _ => ZIO.succeed(0))
  }

  def handleError(e: Throwable): UIO[Int] =
    ZIO(e.printStackTrace()).as(1).orDie

}
