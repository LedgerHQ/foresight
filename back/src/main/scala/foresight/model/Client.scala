package foresight.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import common.model.Height
import foresight.model.Processed.Transaction
import foresight.model.Processed.Transactions
import foresight.model.Processed.TransactionType
import foresight.model.Processed.TransactionType.EIP1559
import foresight.model.Processed.TransactionType.Legacy
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import spray.json._
import spray.json.DefaultJsonProtocol
import spray.json.JsonFormat
import spray.json.JsString
import spray.json.JsValue
import spray.json.deserializationError

case class ClientTransaction(
    hash: String,
    blockHash: Option[String],
    blockNumber: Option[HexNumber],
    from: String,
    to: Option[String],
    value: HexNumber,
    gas: HexNumber,
    gasPrice: Option[HexNumber],
    maxFeePerGas: Option[HexNumber],
    maxPriorityFeePerGas: Option[HexNumber],
    input: String,
    nonce: HexNumber,
    transactionIndex: Option[HexNumber]
)

case class ClientHead(
    hash: String,
    timestamp: HexNumber,
    number: HexNumber
)

case class ClientFeeHistory(
    oldestBlock: HexNumber,
    baseFeePerGas: List[HexNumber]
)

//noinspection TypeAnnotation
object JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object HexNumberFormat extends JsonFormat[HexNumber] {
    def write(n: HexNumber) = JsString(s"${n.value}")

    def read(json: JsValue) = json match {
      case JsString(quantity) => HexNumber(quantity)
      case _                  => deserializationError("String expected")
    }
  }
  implicit object BigDecimalFormat extends JsonFormat[BigDecimal] {
    def write(n: BigDecimal) = if (n != null) {
      JsString(s"${n}")
    } else {
      JsNull
    }

    def read(json: JsValue) = json match {
      case JsString(quantity) => BigDecimal(quantity)
      case _                  => deserializationError("String expected")
    }
  }

  implicit object TransactionTypeFormat extends JsonFormat[TransactionType] {
    def write(n: TransactionType) = JsString(s"${n.value}")

    def read(json: JsValue) = json match {
      case JsString("Legacy")  => Legacy
      case JsString("EIP1559") => EIP1559
      case _                   => deserializationError("String expected")
    }
  }
  implicit object heightFormat extends JsonFormat[Height] {
    def write(h: Height) = JsNumber(h.value)

    def read(json: JsValue) = json match {
      case JsNumber(n) => Height(n.toInt)
      case _           => deserializationError("Number expected")
    }
  }
  implicit object TimestampFormat extends JsonFormat[Timestamp] {
    val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    def write(ts: Timestamp) = {
      if (ts != null) {
        JsString(
          s"${ISO_OFFSET_DATE_TIME.format(ts.toLocalDateTime.atZone(ZoneOffset.UTC))}"
        )
      } else {
        JsNull
      }

    }

    def read(json: JsValue) = json match {
      case JsString(str) =>
        new Timestamp(
          ZonedDateTime.parse(str, ISO_OFFSET_DATE_TIME).toInstant.toEpochMilli
        )
      case _ => deserializationError("String expected")
    }
  }
  implicit val itemFormat = jsonFormat17(Processed.Transaction.apply)

  implicit val orderFormat = jsonFormat1(Transactions.apply)

  implicit val transactionFormat = jsonFormat13(ClientTransaction)
  implicit val headFormat        = jsonFormat3(ClientHead)
  implicit val feeHistoryFormat  = jsonFormat2(ClientFeeHistory)
}

case class HexNumber(value: String) extends AnyVal {
  def toBigDecimal: BigDecimal =
    if (value == "0x") 0 else BigDecimal(BigInt(value.replace("0x", ""), 16))
}
