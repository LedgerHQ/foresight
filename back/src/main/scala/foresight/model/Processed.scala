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
      value: BigInt,
      gas: BigInt,
      gasPrice: Option[BigInt],
      maxFeePerGas: Option[BigInt],
      maxPriorityFeePerGas: Option[BigInt],
      input: String,
      nonce: BigInt,
      transactionIndex: Option[BigInt]
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
          clientTx.blockNumber.map(_.toBigInt.toInt).map(Height.apply),
        sender = clientTx.from,
        receiver = clientTx.to.getOrElse("contract creation"),
        value = clientTx.value.toBigInt,
        gas = clientTx.gas.toBigInt,
        gasPrice = clientTx.gasPrice.map(_.toBigInt),
        maxFeePerGas = clientTx.maxFeePerGas.map(_.toBigInt),
        maxPriorityFeePerGas = clientTx.maxPriorityFeePerGas.map(_.toBigInt),
        input = clientTx.input,
        nonce = clientTx.nonce.toBigInt,
        transactionIndex = clientTx.transactionIndex.map(_.toBigInt),
        transactionType =
          if (clientTx.maxFeePerGas.isDefined) TransactionType.EIP1559
          else TransactionType.Legacy
      )
    }
  }
}
