package io.gbank.api.repository

import io.gbank.api.exception.{ClientNotFoundException, TransactionNotAllowedException}
import io.gbank.api.model.Cliente
import reactivemongo.api.bson.{BSONArray, BSONDocumentReader, BSONDocumentWriter, Macros, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ClienteRepository extends AbstractRepository:
  private implicit def clienteWriter: BSONDocumentWriter[Cliente] =
    Macros.writer[Cliente]
  private implicit def clienteReader: BSONDocumentReader[Cliente] =
    Macros.reader[Cliente]
  private def clientes = connection.flatMap(_.database("gbank").map(_.collection("clientes")))

  def findClienteById(id: Int) =
    clientes.flatMap(_.find(document("clienteId" -> id), Some(document("clienteId" -> 0))).one[Cliente])

  def decreaseLimite(clienteId: Int, valor: Int): Future[Option[Cliente]] =
    clientes.flatMap(
      _.findAndUpdate(
        document("clienteId" -> clienteId),
        document("$inc"      -> document("limite" -> -valor)),
        fetchNewObject = true,
      )
        .map(_.result[Cliente]),
    )

  def decreaseSaldo(clienteId: Int, valor: Int): Future[Option[Cliente]] = clientes
    .flatMap(
      _.findAndUpdate(
        document(
          "clienteId"   -> clienteId,
          "$expr"       -> document(
            "$gte" -> BSONArray("$limite", document("$abs" -> document("$subtract" -> BSONArray("$saldo", valor)))),
          ),
        ),
        document("$inc" -> document("saldo" -> -valor)),
        fetchNewObject = true,
      ),
    )
    .map(_.result[Cliente])
