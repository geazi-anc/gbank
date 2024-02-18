package io.gbank.api.service

import cats.effect.IO
import io.gbank.api.exception._
import io.gbank.api.model.Cliente
import io.gbank.api.repository.ClienteRepository

import scala.concurrent.ExecutionContext.Implicits.global

object ClienteService:
  def findById(clienteId: Int): IO[Cliente] =
    IO.fromFuture(IO(ClienteRepository.findClienteById(clienteId).map {
      case Some(r) => r
      case _       => throw new ClientNotFoundException(s"Client $clienteId not found")
    }))

  def decreaseLimite(clienteId: Int, valor: Int): IO[Cliente] =
    IO.fromFuture(IO(ClienteRepository.decreaseLimite(clienteId, valor).map {
      case Some(c) => c
      case _       => throw new ClientNotFoundException(s"Client $clienteId not found")
    }))

  def decreaseSaldo(clienteId: Int, valor: Int): IO[Cliente] = IO.fromFuture(IO {
    ClienteRepository.decreaseSaldo(clienteId, valor).map {
      case Some(r) => r
      case _       => throw new TransactionNotAllowedException("This transaction is not allowed")
    }
  })
