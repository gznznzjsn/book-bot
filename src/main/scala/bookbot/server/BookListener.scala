package bookbot.server

import bookbot.model.{Book, BookId}
import bookbot.service.BookService
import com.bot4s.telegram.methods.EditMessageText
import com.bot4s.telegram.models.{ChatId, InlineKeyboardButton, InlineKeyboardMarkup}
import zio.{Task, ZIO, ZLayer}



final case class BookListener(bookService: BookService) extends TelegramListener[CoreBot] {

  override def listen(bot: CoreBot): Task[Unit] = ZIO.attempt {

    bot.onRegex("""\s*[Тт]екущ(ие|ая)\s*""".r) {
      implicit msg => { _ =>
        for {
          books <- bookService.getCurrent(msg.from.get.id) //todo .get????
          _ <- books match {
            case List() => bot.reply(s"На данный момент вы ничего не читаете")
            case _ => bot.reply(books.map(b => s"\"${b.title}\" ${b.author}").mkString("\n"))
          }
        } yield ()
      }
    }

    bot.onCallbackWithTag("FINISH") { implicit cbq =>
      for {
        ack <- bot.ackCallback(Option(cbq.from.firstName + " pressed the button!")).fork
        bookId <- BookId.fromString(cbq.data.get)
        _ <- bookService.finish(bookId, cbq.message.get.date)
        book <- bookService.get(bookId)
        response <- bot.request(
          EditMessageText(
            Option(ChatId(cbq.message.get.source)),
            Option(cbq.message.get.messageId),
            text = s"${book.startDate} вы прочитали - \"${book.title}\", ${book.author}"
          )
        ).fork
        _ <- ack.zip(response).join
      } yield ()
    }

    bot.onRegex("""\s*[Нн]ачала?\s*['"«]([а-яА-Я\s]+)['"»]\s*([а-яА-Я\s]+)\s*""".r) {
      implicit msg => {
        case Seq(title, author) => for { //todo trim author and title
          book <- bookService.create(
            msg.from.get.id, title, author, msg.date) //todo .get????
          _ <- bot.reply(s"${book.startDate} вы начали читать - \"${book.title}\", ${book.author}")
        } yield ()
      }
    }

    bot.onRegex("""\s*([Пп]рочитал|[Зз]акончил)а?\s*""".r) {
      implicit msg => { _ =>
        for {
          books <- bookService.getCurrent(msg.from.get.id) //todo .get????
          _ <- books match {
            case List() => bot.reply(s"Не могу пометить книгу прочитанной: на данный момент вы ничего не читаете!")
            case List(book) => for {
              _ <- bookService.finish(
                book.id,
                msg.date
              )
              _ <- bot.reply(s"${book.startDate} вы прочитали - \"${book.title}\", ${book.author}")
            } yield ()
            case _ => bot.replyMd(
              s"""Вы читаете одновременно несколько книг.
                 | Выберите какую конкретно из них вы закончили:""".stripMargin,
              replyMarkup = bookChoiceMarkupOpt(books)
            )
          }
        } yield ()
      }
    }

    bot.onRegex("""\s*[Вв]се\s*""".r) {
      implicit msg => { _ =>
        for {
          books <- bookService.getForMember(msg.from.get.id) //todo .get????
          _ <- books match {
            case List() => bot.reply(s"Вы еще не читали ни одной книги")
            case _ => bot.reply(books.map(b => s"${
              b.endDate match {
                case Some(_) => "✅"
                case None => "⌛"
              }
            } \"${b.title}\" ${b.author} ").mkString("\n"))
          }
        } yield ()
      }
    }

    def bookChoiceMarkupOpt(books: List[Book]): Option[InlineKeyboardMarkup] = {
      Option(InlineKeyboardMarkup.singleColumn(
        books.map(b => InlineKeyboardButton(s"${b.title}", Option(bot.prefixTag("FINISH")(s"${b.id.id}"))))
      ))
    }

  }

}

object BookListener {

  val layer: ZLayer[BookService, Nothing, TelegramListener[CoreBot]] =
    ZLayer.fromFunction(BookListener.apply _)

}


