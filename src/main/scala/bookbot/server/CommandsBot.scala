package bookbot.server

import bookbot.service.BookService
import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio._
import zio.interop.catz._

case class CommandsBot(
                        token: String,
                        bookService: BookService
                      )
  extends TelegramBot[Task](
    token,
    AsyncHttpClientZioBackend.usingClient(
      zio.Runtime.default, asyncHttpClient()
    )
  ) with Polling[Task] with Commands[Task] with RegexCommands[Task] {

  onRegex("""create\s+([a-zA-Z\s]+) by ([a-zA-Z\s*]+)""".r) {
    implicit msg => {
      case Seq(title, author) =>
        for {
          book <- bookService.create(msg.from.get.id, title, author) //todo .get????
          _ <- reply(s"Book (${book.title}, ${book.author}) is created with user id = ${book.memberId}")
        } yield ()
    }
  }

}

object CommandsBot {

  val layer: ZLayer[String with BookService, Nothing, CommandsBot] =
    ZLayer.fromFunction(CommandsBot.apply _)

}
