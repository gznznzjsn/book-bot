package bookbot.server

import zio.{Task, ZIO, ZLayer}

final case class BotStarter(
                             commandsBot: CoreBot,
                             listeners: TelegramListener[CoreBot]*
                           ) {

  def start(): Task[Unit] = for {
    _ <- ZIO.collectAll(listeners.map(_.listen(commandsBot)))
    _ <- commandsBot.startPolling()
  } yield ()

}

object BotStarter {

  val layer: ZLayer[CoreBot with Seq[TelegramListener[CoreBot]], Nothing, BotStarter] =
    ZLayer.fromFunction(BotStarter.apply _)

}
