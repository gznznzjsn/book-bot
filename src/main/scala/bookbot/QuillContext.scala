package bookbot

import io.getquill.jdbczio.Quill
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._

import javax.sql.DataSource

/** QuillContext houses the datasource layer which initializes a connection
 * pool. This has been slightly complicated by the way Heroku exposes its
 * connection details. Database URL will only be defined when run from Heroku
 * in production.
 */
object QuillContext extends PostgresZioJdbcContext(SnakeCase) {

  val dataSourceLayer: ZLayer[Any, Throwable, DataSource] =
    Quill.DataSource.fromPrefix("databaseConfig")

}
