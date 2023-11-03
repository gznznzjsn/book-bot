package bookbot.server

import bookbot.model.{Book, BookId}
import bookbot.service.BookService
import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.methods._
import com.bot4s.telegram.models.{ChatId, InlineKeyboardButton, InlineKeyboardMarkup}
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio._
import zio.interop.catz._

import java.time.{Instant, ZoneId}

case class CommandsBot(
                        token: String,
                        bookService: BookService
                      )
  extends TelegramBot[Task](
    token,
    AsyncHttpClientZioBackend.usingClient(zio.Runtime.default, asyncHttpClient())
  ) with Polling[Task] with Commands[Task] with RegexCommands[Task] with Callbacks[Task] {

  def bookChoiceMarkupOpt(books: List[Book]): Option[InlineKeyboardMarkup] = {
    Option(InlineKeyboardMarkup.singleColumn(
      books.map(b => InlineKeyboardButton(s"${b.title}", Option(prefixTag("FINISH")(s"${b.id.id}"))))
    ))
  }

  onCallbackWithTag("FINISH") { implicit cbq =>
    for {
      ack <- ackCallback(Option(cbq.from.firstName + " pressed the button!")).fork
      bookId <- BookId.fromString(cbq.data.get)
      book <- bookService.finish(
        bookId,
        Instant.ofEpochSecond(cbq.message.get.date).atZone(ZoneId.systemDefault()).toLocalDate
      )
      response <- request(
        EditMessageText(
          Option(ChatId(cbq.message.get.source)),
          Option(cbq.message.get.messageId),
          text = s"${book.startDate} вы прочитали - \"${book.title}\", ${book.author}"
        )
      ).fork
      _ <- ack.zip(response).join
    } yield ()
  }

  onRegex("""\s*[Нн]ачала?\s*['"«]\s*([а-яА-Я\s]+)\s*['"»]\s*([а-яА-Я\s]+)\s*""".r) {
    implicit msg => {
      case Seq(title, author) => for {
        book <- bookService.create(
          msg.from.get.id, title, author,
          Instant.ofEpochSecond(msg.date).atZone(ZoneId.systemDefault()).toLocalDate
        ) //todo .get????
        _ <- reply(s"${book.startDate} вы начали читать - \"${book.title}\", ${book.author}")
      } yield ()
    }
  }

  onRegex("""\s*[Тт]екущ(ие|ая)\s*""".r) {
    implicit msg => { _ =>
      for {
        books <- bookService.getCurrent(msg.from.get.id) //todo .get????
        _ <- reply {
          books match {
            case List() => s"На данный момент вы ничего не читаете"
            case _ => books.map(b => s"\"${b.title}\" ${b.author}").mkString("\n")
          }
        }
      } yield ()
    }
  }

  onRegex("""\s*([Пп]рочитал|[Зз]акончил)а?\s*""".r) {
    implicit msg => { _ =>
      for {
        books <- bookService.getCurrent(msg.from.get.id) //todo .get????
        _ <- books match {
          case List() => reply(s"Не могу пометить книгу прочитанной: на данный момент вы ничего не читаете!")
          case List(book) => for {
            book <- bookService.finish(
              book.id,
              Instant.ofEpochSecond(msg.date).atZone(ZoneId.systemDefault()).toLocalDate
            )
            _ <- reply(s"${book.startDate} вы прочитали - \"${book.title}\", ${book.author}")
          } yield ()
          case _ => replyMd(
            s"""Вы читаете одновременно несколько книг.
               | Выберите какую конкретно из них вы закончили:""".stripMargin,
            replyMarkup = bookChoiceMarkupOpt(books)
          )
        }
      } yield ()
    }
  }

  onRegex("""\s*[Вв]се\s*""".r) {
    implicit msg => { _ =>
      for {
        books <- bookService.getForMember(msg.from.get.id) //todo .get????
        _ <- reply {
          books match {
            case List() => s"Вы еще не читали ни одной книги"
            case _ => books.map(b => s"\"${b.title}\" ${b.author}").mkString("\n")
          }
        }
      } yield ()
    }
  }

}

object CommandsBot {

  val layer: ZLayer[String with BookService, Nothing, CommandsBot] =
    ZLayer.fromFunction(CommandsBot.apply _)

}
