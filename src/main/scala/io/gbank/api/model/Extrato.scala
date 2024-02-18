package io.gbank.api.model

case class Saldo(total: Int, data_extrato: String, limite: Int)
case class Extrato(saldo: Saldo, ultimas_transacoes: List[TransacaoSemClienteId])
