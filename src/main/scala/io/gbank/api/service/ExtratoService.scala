package io.gbank.api.service

import cats.effect.IO
import io.gbank.api.model._

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global

object ExtratoService:
  def findByClienteId(clienteId: Int): IO[Extrato] = for {
    cliente    <- ClienteService.findById(clienteId)
    transacoes <- TransacaoService.findLastTenTransactions(clienteId)
  } yield Extrato(Saldo(cliente.saldo, Instant.now().toString, cliente.limite), transacoes)
