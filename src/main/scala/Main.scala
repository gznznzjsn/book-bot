import com.typesafe.config.ConfigFactory
import zio._
import zio.{ZIO, ZIOAppArgs}

object Main extends zio.ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val loadToken = ZIO.attempt(ConfigFactory.load("c").getString("token"))
    for {
      token <- loadToken
      _ <- new CommandsBot(token).startPolling().map(_ => ExitCode.success)
    } yield ()
  }
}