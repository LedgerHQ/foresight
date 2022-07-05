package foresight.model

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

//noinspection TypeAnnotation
object JsonProtocol extends DefaultJsonProtocol {
  implicit object HexNumberFormat extends JsonFormat[HexNumber] {
    def write(n: HexNumber) = JsString(s"${n.value}")

    def read(json: JsValue) = json match {
      case JsString(quantity) => HexNumber(quantity)
      case _                  => deserializationError("String expected")
    }
  }

  implicit val transactionFormat = jsonFormat13(ClientTransaction)
  implicit val headFormat        = jsonFormat3(ClientHead)
}

case class HexNumber(value: String) extends AnyVal {
  def toBigDecimal: BigDecimal =
    if (value == "0x") 0 else BigDecimal(BigInt(value.replace("0x", ""), 16))
}
