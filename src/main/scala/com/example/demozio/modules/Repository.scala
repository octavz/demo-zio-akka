package com.example.demozio
package modules

import zio._
import zio.interop.catz._
import models._
import doobie._
import doobie.implicits._

sealed trait DatabaseError extends  Throwable
final case class GenericDatabaseError(inner: Throwable) extends DatabaseError

object Repository {
  trait Service {
    def saveUser(user: User): IO[DatabaseError, User]
    def getUserByEmail(email: String): IO[DatabaseError, Option[User]]
  }
}

trait Repository {
  val repository: Repository.Service
}

trait RepositoryServiceLive extends Repository.Service {
  val xa: Transactor[Task]

  override def saveUser(user: User) = {
    val sqlInsert =    sql"""insert into users(email, password) values(${user.email}, ${user.password})""".update.run
    sqlInsert.transact(xa).mapError(GenericDatabaseError).as(user)
  }

  override def getUserByEmail(email: String) = {
    val sqlGetByEmail = sql"""select email, password from users where email=$email""".query[User].option
    sqlGetByEmail.transact(xa).mapError(GenericDatabaseError)
  }
}
