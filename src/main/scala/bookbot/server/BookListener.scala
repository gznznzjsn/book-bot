package bookbot.server

import bookbot.service.BookService
import zio.{Task, ZIO, ZLayer}


final case class BookListener(bookService: BookService) extends TelegramListener[CommandsBot] {

  override def listen(bot: CommandsBot): Task[Unit] = ZIO.attempt {
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
  }

}

object BookListener {

  val layer: ZLayer[BookService, Nothing, TelegramListener[CommandsBot]] =
    ZLayer.fromFunction(BookListener.apply _)

}


