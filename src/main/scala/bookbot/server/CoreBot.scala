package bookbot.server

import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio._
import zio.interop.catz._

case class CoreBot(token: String)
  extends TelegramBot[Task](
    token,
    AsyncHttpClientZioBackend.usingClient(zio.Runtime.default, asyncHttpClient())
  )
    with Polling[Task]
    with Commands[Task]
    with RegexCommands[Task]
    with Callbacks[Task]

object CoreBot {

  val layer: ZLayer[String, Nothing, CoreBot] =
    ZLayer.fromFunction(CoreBot.apply _)

}
