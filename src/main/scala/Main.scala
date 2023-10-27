import bookbot.server.CommandsBot
import bookbot.service.BookServiceLive
import com.typesafe.config.ConfigFactory
import zio.{ZIO, _}

object Main extends zio.ZIOAppDefault {


  override def run: Task[Unit] =
    ZIO
      .serviceWithZIO[CommandsBot](_.startPolling)
      .provide(
        ZLayer.succeed(ConfigFactory.load("c").getString("token")),
        CommandsBot.layer,
        BookServiceLive.layer
      )

}