package com.example.demozio

object models {

  case class DatabaseConfig(jdbcUrl: String, user: String, password: String, schema: String)

  case class AppConfig(httpPort: Int, database: DatabaseConfig)

  case class User(email: String, password: String)

}
