package foresight.model

import common.model._
import scala.util.Try
import spray.json._

object Raw {

  final case class Transaction(hash: String, data: JsValue)

  final case class Block(
      height: Height,
      header: JsValue,
      transactions: Vector[Transaction]
  )

  object Block {
    def fromJson(json: JsObject): Try[Block] = ???

  }
}
