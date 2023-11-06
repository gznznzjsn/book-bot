package bookbot

import bookbot.repository.{BookRepositoryLive, MemberRepositoryLive}
import bookbot.server.{BookListener, CommandsBot, BookBotServer, TelegramListener}
import bookbot.service.{BookServiceLive, MemberServiceLive}
import com.typesafe.config.ConfigFactory
import zio.{Task, ZIO, ZLayer}
import zio._


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
        .serviceWithZIO[BookBotServer](_.start())
        .provide(
          ZLayer.succeed(ConfigFactory.load("c").getString("token")),
         ZLayer.collectAll(List(BookListener.layer)), BookBotServer.layer,
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


