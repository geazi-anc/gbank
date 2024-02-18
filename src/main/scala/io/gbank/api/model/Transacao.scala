package io.gbank.api.model

final case class TransacaoRecebida(
    valor: Int,
    tipo: String,
    descricao: String
)

final case class TransacaoConcluida(
    clienteId: Int,
    valor: Int,
    tipo: String,
    descricao: String,
    realizada_em: String
)

final case class TransacaoSemClienteId(
    valor: Int,
    tipo: String,
    descricao: String,
    realizada_em: String
)
