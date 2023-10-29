package bookbot

import org.flywaydb.core.Flyway
import zio._

import javax.sql.DataSource

final case class Migrations(dataSource: DataSource) {

  val migrate: Task[Unit] =
    for {
      flyway <- loadFlyway
      _ <- ZIO.attempt(flyway.migrate())
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

object Migrations {

  val layer: ZLayer[DataSource, Nothing, Migrations] = ZLayer.fromFunction(Migrations.apply _)

}
