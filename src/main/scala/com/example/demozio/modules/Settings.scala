package com.example.demozio
package modules

import akka.actor.ActorSystem
import com.example.demozio.models.AppConfig
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.error._

object Settings {
  trait Service {
    def appConfig: Either[ConfigError, AppConfig]
    def actorSystem: ActorSystem
  }
}

trait Settings {
  val settings: Settings.Service
}

trait SettingsLive extends Settings {
  override val settings: Settings.Service = new Settings.Service {
    override def appConfig =
      ConfigSource.default.at("com.example.demozio").load[AppConfig] match {
        case Right(c) => Right(c)
        case Left(e) => Left(ConfigError(e))
      }

    override def actorSystem: ActorSystem = ActorSystem("MainServer")
  }
}

case class ConfigError(failures: ConfigReaderFailures) extends Exception {

  private def failureToString(failure: ConfigReaderFailure) = failure match {
    case _: CannotParse | _: CannotReadFile ⇒ s"${failure.description}"
    case f: ConvertFailure                  ⇒ s"${f.description} in ${f.path} at ${f.location}"
    case f: ThrowableFailure                ⇒ s"${f.description} at ${f.location} \n ${f.throwable.getStackTrace.map(v ⇒ s"${v.toString}").mkString("\n")}"
  }

  override def getMessage = failures.toList.map(failureToString).mkString("\n")
}
