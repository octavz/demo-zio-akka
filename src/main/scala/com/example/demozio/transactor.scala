package com.example.demozio

import cats.effect.Blocker
import zio._
import zio.blocking.Blocking
import zio.interop.catz._
import doobie._
import doobie.hikari.HikariTransactor
import modules._
import configuration._

object transactor {

  def newTransactor: ZManaged[Settings with Blocking, DatabaseError, Transactor[Task]] = {
    val zio = for {
      c <- appConfig
      as <- actorSystem
      blockingEX <- ZIO.accessM[Blocking](_.blocking.blockingExecutor)
      connectEC = as.dispatcher
      blocker = Blocker.liftExecutionContext(blockingEX.asEC)
      transactor = HikariTransactor.newHikariTransactor[Task]("org.postgresql.Driver",
        c.database.jdbcUrl,
        c.database.user,
        c.database.password,
        connectEC,
        blocker)
    } yield transactor

    //we had to transform from zio to managed
    zio.toManaged_.flatMap { tr =>
      val reservation = tr.allocated.map {
        case (trans, cleanup) => Reservation(ZIO.succeed(trans), _ => cleanup.orDie)
      }
      ZManaged(reservation)
    }.mapError(GenericDatabaseError)

  }

}
