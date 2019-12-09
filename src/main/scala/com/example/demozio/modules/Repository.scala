package com.example.demozio
package modules

import zio._
import models._

object Repository {
  trait Service {
    def saveUser(user: User): Task[User]
    def getUserByEmail(email: String): Task[User]
  }
}

trait Repository {
  val repository: Repository.Service
}

//we need a database
trait RepositoryServiceLive extends Repository.Service {
  override def saveUser(user: User): Task[User] = ???
  override def getUserByEmail(email: String): Task[User] = ???
}
