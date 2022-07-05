package foresight.model

import common.model._
import java.sql.Timestamp
import java.time.Instant
import scala.util.Try
import spray.json._

object Raw {

  def sqlTimestampNow: Timestamp =
    Timestamp.from(Instant.now())

  final case class MinedTransaction(
      hash: String,
      blockHeight: Height,
      minedAt: Timestamp,
      data: JsObject
  )
  object MinedTransaction {
    def fromJson(
        json: JsObject,
        height: Height,
        minedAt: Timestamp
    ): Try[MinedTransaction] =
      json.getFields("hash") match {
        case Seq(JsString(hash)) =>
          Try(MinedTransaction(hash, height, minedAt, json))
        case _ =>
          Try(throw DeserializationException("Hash is missing"))
      }
  }

  final case class PendingTransaction(
      hash: String,
      createdAt: Timestamp,
      data: JsObject
  )
  object PendingTransaction {
    def fromJson(
        json: JsObject,
        createdAt: Timestamp
    ): Try[PendingTransaction] =
      json.getFields("hash") match {
        case Seq(JsString(hash)) =>
          Try(PendingTransaction(hash, createdAt, json))
        case _ =>
          Try(throw DeserializationException("Hash is missing"))
      }

  }

  final case class DroppedTransaction(
      hash: String,
      droppedAt: Timestamp,
      data: JsValue
  )

  final case class Block(
      height: Height,
      createdAt: Timestamp,
      header: JsValue,
      transactions: Vector[MinedTransaction]
  )

  object Block {
    def fromJson(json: JsObject, minedAt: Timestamp): Try[Block] =
      json.getFields("number", "transactions") match {
        case Seq(JsString(n), JsArray(data)) =>
          for {
            height <- Try(HexNumber(n).toBigDecimal.toInt)
              .map(Height.apply)
          } yield {
            Block(
              height,
              minedAt,
              JsObject(json.fields.removed("transactions")),
              data.flatMap(js =>
                MinedTransaction
                  .fromJson(js.asJsObject, height, minedAt)
                  .toOption
              )
            )
          }
        case _ =>
          Try(throw DeserializationException("Fields are missing"))
      }
  }
}
