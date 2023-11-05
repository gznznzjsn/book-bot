package bookbot

import bookbot.repository.{BookRepositoryLive, MemberRepositoryLive}
import bookbot.server.CommandsBot
import bookbot.service.{BookServiceLive, MemberServiceLive}
import com.typesafe.config.ConfigFactory
import zio.{Task, ZIO, ZLayer}

object Main extends zio.ZIOAppDefault {

  override def run: Task[Unit] = {
    for {
      _ <- ZIO
        .serviceWithZIO[Migrations](_.migrate)
        .provide(
          Migrations.layer,
          QuillContext.dataSourceLayer
        )
      _ <- ZIO
        .serviceWithZIO[CommandsBot](_.startPolling())
        .provide(
          ZLayer.succeed(ConfigFactory.load("c").getString("token")),
          CommandsBot.layer,
          BookServiceLive.layer,
          MemberServiceLive.layer,
          BookRepositoryLive.layer,
          MemberRepositoryLive.layer,
          QuillContext.dataSourceLayer
        )
    } yield ()

  }

}


