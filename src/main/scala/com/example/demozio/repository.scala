package com.example.demozio

import zio._

import modules._
import models._

object repository {

  def saveUser(user: User) =
    ZIO.accessM[Repository](_.repository.saveUser(user))

  def getUserByEmail(email: String) =
    ZIO.accessM[Repository](_.repository.getUserByEmail(email))

}
