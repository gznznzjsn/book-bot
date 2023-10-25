import cats.instances.future._
import cats.syntax.functor._
import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.api.{ChatActions, RequestHandler}
import com.bot4s.telegram.clients.ScalajHttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.bot4s.telegram.methods._
import com.bot4s.telegram.models._
import com.typesafe.config.ConfigFactory

import java.net.URLEncoder
import scala.concurrent.Future

/** Text-to-speech bot (using Google TTS API)
 *
 * Google will rightfully block your IP in case of abuse.
 * Usage: /speak Hello World
 */
object TextToSpeechBot extends TelegramBot
  with Polling
  with Commands[Future]
  with InlineQueries[Future]
  with ChatActions[Future] {

  override val client: RequestHandler[Future] = new ScalajHttpClient(ConfigFactory.load("c").getString("token") )

  def ttsUrl(text: String): String =
    s"http://translate.google.com/translate_tts?client=tw-ob&tl=en-us&q=${URLEncoder.encode(text, "UTF-8")}"

  onCommand("speak" | "say" | "talk") { implicit msg =>
    withArgs { args =>
      val text = args.mkString(" ")
      for {
        r <- Future {
          scalaj.http.Http(ttsUrl(text)).asBytes
        }
        if r.isSuccess
        bytes = r.body
        _ <- uploadingAudio // hint the user
        voiceMp3 = InputFile("voice.mp3", bytes)
        _ <- request(SendVoice(msg.source, voiceMp3))
      } yield ()
    }
  }
  onCommand("бе") { implicit msg =>
    reply("пф").void
  }
  onCommand("жо") { implicit msg =>
    reply("ПА").void
  }
}
