package bookbot

import org.flywaydb.core.Flyway
import zio._

import javax.sql.DataSource

/** Migrations is a service that uses Flyway to run our migrations.
  *
  * Note that Flyway searches the project's file structure for files that match
  * Flyway's naming convention (see files in db.migration) allowing the user to
  * simply call built-in methods for managing database migrations.
  *
  * For more information on Flyway, see: https://flywaydb.org/documentation/
  */
final case class Migrations(dataSource: DataSource) {

  /** Runs the database migration files..
    */
  val migrate: Task[Unit] =
    for {
      flyway <- loadFlyway
      _      <- ZIO.attempt(flyway.migrate())
    } yield ()

  private lazy val loadFlyway: Task[Flyway] =
    ZIO.attempt {
      Flyway
        .configure()
        .dataSource(dataSource)
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .load()
    }

}

/** Here in the companion object we define the layer that provides the
  * Migrations service.
  */
object Migrations {
  val layer: ZLayer[DataSource, Nothing, Migrations] =
    ZLayer.fromFunction(Migrations.apply _)
}
