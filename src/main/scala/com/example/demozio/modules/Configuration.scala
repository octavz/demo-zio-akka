package com.example.demozio
package modules

import com.example.demozio.models.AppConfig
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Settings {
  trait Service {
    def appConfig: Either[Throwable, AppConfig]
  }
}

trait Settings {
  val settings: Settings.Service
}

//lets read the config with pureconfig
trait SettingsLive extends Settings {
  override val settings: Settings.Service = new Settings.Service {
    override def appConfig: Either[Throwable, AppConfig] =
      ConfigSource.default.at("com.example.demozio").load[AppConfig] match {
        case Right(c) => Right(c)
        case Left(_) => Left(new Exception("Something went bad reading configuratino"))
      }
  }
}

