package io.gbank.api.repository

import io.gbank.api.model.{Cliente, TransacaoConcluida, TransacaoRecebida, TransacaoSemClienteId}
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{BSONDocumentReader, BSONDocumentWriter, Macros, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TransacaoRepository extends AbstractRepository:
  private implicit def transacaoConcluidaWriter: BSONDocumentWriter[TransacaoConcluida]       =
    Macros.writer[TransacaoConcluida]
  private implicit def transacaoConcluidaReader: BSONDocumentReader[TransacaoConcluida]       =
    Macros.reader[TransacaoConcluida]
  private implicit def transacaoSemClienteIdReader: BSONDocumentReader[TransacaoSemClienteId] =
    Macros.reader[TransacaoSemClienteId]
  private implicit def clienteReader: BSONDocumentReader[Cliente]                             = Macros.reader[Cliente]
  private def transacoes = connection.flatMap(_.database("gbank").map(_.collection("transacoes")))

  def insert(transacao: TransacaoConcluida) =
    transacoes.flatMap(_.insert.one(transacao))

  def findLastTransactions(clienteId: Int, limit: Int): Future[List[TransacaoSemClienteId]] = transacoes.flatMap(
    _.find(document("clienteId" -> clienteId))
      .sort(document("realizada_em" -> -1))
      .cursor[TransacaoSemClienteId]()
      .collect[List](limit, Cursor.FailOnError[List[TransacaoSemClienteId]]()),
  )

  def debitTransaction(clienteId: Int, valor: Int): Future[Option[Cliente]] =
    ClienteRepository.decreaseSaldo(clienteId, valor)

  def creditTransaction(clienteId: Int, valor: Int): Future[Option[Cliente]] =
    ClienteRepository.decreaseLimite(clienteId, valor)
