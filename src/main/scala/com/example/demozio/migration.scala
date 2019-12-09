package com.example.demozio

import zio._
import zio.console._
import com.github.mlangc.slf4zio.api._
import org.flywaydb.core.Flyway

object migration extends LoggingSupport {
  def migrate(schema: String,
              jdbcUrl: String,
              user: String,
              password: String): RIO[Console, Int] =
    logger.infoIO(s"Migrating database, for schem: $schema with url: $jdbcUrl") *>
      ZIO {
        Flyway.configure()
          .dataSource(jdbcUrl, user, password)
          .schemas(schema)
          .baselineOnMigrate(true)
          .load().migrate()
      }

}
