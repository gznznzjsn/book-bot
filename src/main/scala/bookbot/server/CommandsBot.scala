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
    AsyncHttpClientZioBackend.usingClient(zio.Runtime.default, asyncHttpClient())
  ) with Polling[Task] with Commands[Task] with RegexCommands[Task] {

  onRegex("""\s*[Нн]ачала?\s*['"«]([а-яА-Я\s]+)['"»]\s*([а-яА-Я\s]+)\s*""".r) {
    implicit msg => {
      case Seq(title, author) =>
        for {
          book <- bookService.create(msg.from.get.id, title.trim, author.trim) //todo .get????
          _ <- reply(s"Book (${book.title}, ${book.author}) is created with user id = ${book.memberId}")
        } yield ()
    }
  }

  onRegex("""\s*[Вв]се\s*""".r) {
    implicit msg => {
      _ =>
        for {
          books <- bookService.getForMember(msg.from.get.id) //todo .get????
          _ <- reply(
            if (books.isEmpty) s"Вы еще не читали ни одной книги"
            else books.map(b => s"\"${b.title}\" ${b.author}").mkString("\n")
          )
        } yield ()
    }
  }

}

object CommandsBot {

  val layer: ZLayer[String with BookService, Nothing, CommandsBot] =
    ZLayer.fromFunction(CommandsBot.apply _)

}
