package io.gbank.api

import cats.effect._
import cats.implicits._
import com.comcast.ip4s._
import io.circe.generic.auto._
import io.circe.syntax._
import io.gbank.api.exception._
import io.gbank.api.model._
import io.gbank.api.service._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp {
  implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]

  val clienteService = HttpRoutes.of[IO] {
    case GET -> Root / "clientes" / IntVar(clienteId) / "extrato" =>
      ExtratoService.findByClienteId(clienteId).attempt.flatMap {
        case Right(r)                         => Ok(r)
        case Left(_: ClientNotFoundException) => NotFound("reason" -> s"Client $clienteId not found")
        case Left(_)                          => ???
      }

    case req @ POST -> Root / "clientes" / IntVar(clienteId) / "transacoes" =>
      req.as[TransacaoRecebida].flatMap { tr =>
        TransacaoService
          .transact(clienteId, tr)(TransacaoService.choiceBetweenDebitAndCredit(tr.tipo))
          .attempt
          .flatMap {
            case Right(r)                                  => Ok(r)
            case Left(cnf: ClientNotFoundException)        => NotFound("reason" -> cnf.getMessage)
            case Left(tna: TransactionNotAllowedException) => UnprocessableEntity("reason" -> tna.getMessage)
            case Left(_)                                   => ???
          }
      }
  }

  val httpApp = clienteService.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
