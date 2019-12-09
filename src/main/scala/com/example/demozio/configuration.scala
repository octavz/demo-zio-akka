package com.example.demozio

import zio._
import modules._

object configuration {
  def appConfig =
    ZIO.access[Settings](_.settings.appConfig) flatMap (ZIO.fromEither(_))
}
