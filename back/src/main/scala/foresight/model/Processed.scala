package foresight.model

import common.model.Height
import java.sql.Timestamp

object Processed {
  sealed trait TransactionType {
    def value: String
  }
  object TransactionType {
    case object Legacy  extends TransactionType {
      override def value: String = "Legacy"
    }
    case object EIP1559 extends TransactionType {
      override def value: String = "EIP1559"
}
  }
  case class Transaction(
      hash: String,
      transactionType: TransactionType,
      createdAt: Timestamp,
      minedAt: Option[Timestamp],
      droppedAt: Option[Timestamp],
      blockHash: Option[String],
      blockHeight: Option[Height],
      sender: String,
      receiver: String,
      value: BigDecimal,
      gas: BigDecimal,
      gasPrice: Option[BigDecimal],
      maxFeePerGas: Option[BigDecimal],
      maxPriorityFeePerGas: Option[BigDecimal],
      input: String,
      nonce: BigDecimal,
      transactionIndex: Option[BigDecimal]
  )

  object Transaction {
    def fromPending(raw: Raw.PendingTransaction): Transaction = {
      import JsonProtocol._
      val clientTx = raw.data.convertTo[ClientTransaction]
      Transaction(
        hash = clientTx.hash,
        createdAt = raw.createdAt,
        minedAt = None,
        droppedAt = None,
        blockHash = clientTx.blockHash,
        blockHeight =
          clientTx.blockNumber.map(_.toBigDecimal.toInt).map(Height.apply),
        sender = clientTx.from,
        receiver = clientTx.to.getOrElse("contract creation"),
        value = clientTx.value.toBigDecimal,
        gas = clientTx.gas.toBigDecimal,
        gasPrice = clientTx.gasPrice.map(_.toBigDecimal),
        maxFeePerGas = clientTx.maxFeePerGas.map(_.toBigDecimal),
        maxPriorityFeePerGas = clientTx.maxPriorityFeePerGas.map(_.toBigDecimal),
        input = clientTx.input,
        nonce = clientTx.nonce.toBigDecimal,
        transactionIndex = clientTx.transactionIndex.map(_.toBigDecimal),
        transactionType =
          if (clientTx.maxFeePerGas.isDefined) TransactionType.EIP1559
          else TransactionType.Legacy
      )
    }
    def fromMined(raw: Raw.MinedTransaction): Transaction = {
      import JsonProtocol._
      val clientTx = raw.data.convertTo[ClientTransaction]
      Transaction(
        hash = clientTx.hash,
        createdAt = raw.minedAt,
        minedAt = Some(raw.minedAt),
        droppedAt = None,
        blockHash = clientTx.blockHash,
        blockHeight =
          clientTx.blockNumber.map(_.toBigDecimal.toInt).map(Height.apply),
        sender = clientTx.from,
        receiver = clientTx.to.getOrElse("contract creation"),
        value = clientTx.value.toBigDecimal,
        gas = clientTx.gas.toBigDecimal,
        gasPrice = clientTx.gasPrice.map(_.toBigDecimal),
        maxFeePerGas = clientTx.maxFeePerGas.map(_.toBigDecimal),
        maxPriorityFeePerGas = clientTx.maxPriorityFeePerGas.map(_.toBigDecimal),
        input = clientTx.input,
        nonce = clientTx.nonce.toBigDecimal,
        transactionIndex = clientTx.transactionIndex.map(_.toBigDecimal),
        transactionType =
          if (clientTx.maxFeePerGas.isDefined) TransactionType.EIP1559
          else TransactionType.Legacy
      )
    }
  }
}
