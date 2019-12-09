package com.example.demozio

import org.flywaydb.core.Flyway
import zio._
import zio.console._

object migration {
  def migrate(schema: String,
              jdbcUrl: String,
              user: String,
              password: String): RIO[Console, Int] =
    putStrLn(s"Migrating database, for schem: $schema with url: $jdbcUrl") *>
      ZIO {
        Flyway.configure()
          .dataSource(jdbcUrl, user, password)
          .schemas(schema)
          .baselineOnMigrate(true)
          .load().migrate()
      }

}
