package io.gbank.api.service

import cats.effect.IO
import io.gbank.api.exception.TransactionNotAllowedException
import io.gbank.api.model._
import io.gbank.api.repository._
import reactivemongo.api.commands.WriteResult

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global

object TransacaoService:
  def insert(clienteId: Int, transacao: TransacaoRecebida): IO[WriteResult] =
    IO.fromFuture(
      IO(
        TransacaoRepository.insert(
          TransacaoConcluida(
            clienteId = clienteId,
            valor = transacao.valor,
            tipo = transacao.tipo,
            descricao = transacao.descricao,
            realizada_em = Instant.now().toString,
          ),
        ),
      ),
    )

  def findLastTenTransactions(clienteId: Int): IO[List[TransacaoSemClienteId]] = IO.fromFuture(IO {
    TransacaoRepository.findLastTransactions(clienteId, 10)
  })

  def transact(clienteId: Int, transacao: TransacaoRecebida)(p: (Int, TransacaoRecebida) => IO[Cliente]): IO[Cliente] =
    p(clienteId, transacao)

  def creditTransaction(clienteId: Int, transacao: TransacaoRecebida): IO[Cliente] = for {
    result <- ClienteService.decreaseLimite(clienteId, transacao.valor)
    _      <- insert(clienteId, transacao)
  } yield result

  def debitTransaction(clienteId: Int, transacao: TransacaoRecebida) = for {
    result <- ClienteService.decreaseSaldo(clienteId, transacao.valor)
    _      <- insert(clienteId, transacao)
  } yield result

  def choiceBetweenDebitAndCredit(tipo: String): (Int, TransacaoRecebida) => IO[Cliente] =
    if tipo.toLowerCase == "d" then debitTransaction else creditTransaction
