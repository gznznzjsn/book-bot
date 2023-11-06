package bookbot.server

import zio.{Task, ZIO, ZLayer}

final case class BookBotServer(
                                commandsBot: CommandsBot,
                                listeners: TelegramListener[CommandsBot]*
                              ) {

  def start(): Task[Unit] = for {
    _ <- ZIO.collectAll(listeners.map(l => l.listen(commandsBot)))
    _ <- commandsBot.startPolling()
  } yield ()

}

object BookBotServer {

  val layer: ZLayer[CommandsBot with Seq[TelegramListener[CommandsBot]], Nothing, BookBotServer] =
    ZLayer.fromFunction(BookBotServer.apply _)

}
