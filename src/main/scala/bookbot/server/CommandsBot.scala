package bookbot.server

import bookbot.service.{BookService, BookServiceLive}
import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.models.Message
import com.typesafe.config.ConfigFactory
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio._
import zio.interop.catz._

import scala.util.Try

/**
 * Showcases different ways to declare commands (Commands + RegexCommands).
 *
 * Note that non-ASCII commands are not clickable.
 *
 * @param token Bot's token.
 */
case class CommandsBot(
                        token: String,
                        bookService: BookService
                      )
  extends TelegramBot[Task](
    token,
    AsyncHttpClientZioBackend.usingClient(
      zio.Runtime.default, asyncHttpClient()
    )
  )
    with Polling[Task]
    with Commands[Task]
    with RegexCommands[Task] {

  // Extractor
  object Int {
    def unapply(s: String): Option[Int] = Try(s.toInt).toOption
  }

  // Several commands can share the same handler.
  // Shows the 'using' extension to extract information from messages.
  onCommand("/hallo" | "/bonjour" | "/ciao" | "/hola") { implicit msg =>
    using(_.from) { // sender
      user =>
        reply(s"Hello ${user.firstName} from Europe?").ignore
    }
  }

  def secretIsValid(msg: Message): ZIO[Any, Nothing, Boolean] =
    ZIO.succeed(msg.text.fold(false)(_.split(" ").last == "password"))

  whenF[Task, Message](onCommand("secret"), secretIsValid) { implicit msg =>
    reply("42").ignore
  }

  // withArgs extracts command arguments.
  onRegex("""create\s+([a-zA-Z\s]+) by ([a-zA-Z\s*]+)""".r) {
    implicit msg => {
      case Seq(title, author) =>
        for {
          _ <- reply("Started creation...")
          book <- bookService.create(title, author)
          _ <- reply(s"Book (${book.title}, ${book.author}) is created with id = ${book.id}")
        } yield ()
    }
  }
}

object CommandsBot {

  val layer: ZLayer[String with BookService, Nothing, CommandsBot] = ZLayer.fromFunction(CommandsBot.apply _)

}
