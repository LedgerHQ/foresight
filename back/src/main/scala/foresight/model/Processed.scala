package foresight.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import common.model.Height
import foresight.model.JsonProtocol.immSeqFormat
import foresight.model.JsonProtocol.jsonFormat1
import foresight.model.JsonProtocol.jsonFormat17
import java.sql.Timestamp
import spray.json.DefaultJsonProtocol
import spray.json.JsonFormat
import spray.json.JsString
import spray.json.JsValue
import spray.json.deserializationError

object Processed {
  sealed trait TransactionType {
    def value: String
  }
  object TransactionType {
    case object Legacy extends TransactionType {
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
      transactionIndex: Option[BigDecimal],
      tip: Option[BigDecimal]
  )
  final case class Transactions(items: List[Processed.Transaction])
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
        maxPriorityFeePerGas =
          clientTx.maxPriorityFeePerGas.map(_.toBigDecimal),
        input = clientTx.input,
        nonce = clientTx.nonce.toBigDecimal,
        transactionIndex = clientTx.transactionIndex.map(_.toBigDecimal),
        transactionType =
          if (clientTx.maxFeePerGas.isDefined) TransactionType.EIP1559
          else TransactionType.Legacy,
        tip = None
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
        maxPriorityFeePerGas =
          clientTx.maxPriorityFeePerGas.map(_.toBigDecimal),
        input = clientTx.input,
        nonce = clientTx.nonce.toBigDecimal,
        transactionIndex = clientTx.transactionIndex.map(_.toBigDecimal),
        transactionType =
          if (clientTx.maxFeePerGas.isDefined) TransactionType.EIP1559
          else TransactionType.Legacy,
        tip = None
      )
    }
  }
}
