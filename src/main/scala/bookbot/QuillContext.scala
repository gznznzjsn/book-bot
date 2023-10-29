package bookbot

import io.getquill.jdbczio.Quill
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._

import javax.sql.DataSource

object QuillContext extends PostgresZioJdbcContext(SnakeCase) {

  val dataSourceLayer: ZLayer[Any, Throwable, DataSource] =
    Quill.DataSource.fromPrefix("databaseConfig")

}
