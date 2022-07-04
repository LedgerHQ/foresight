package common.model

import spray.json._

object JRPC {
  final case class Request(id: Int, method: String, params: Vector[JsValue]) {
    def encode: JsObject =
      JsObject(
        "id"     -> JsNumber(id),
        "method" -> JsString(method),
        "params" -> JsArray.apply(params)
      )
  }

  final case class Response(id: Int, result: JsValue)
  object Response {
    def decode(json: JsObject): Response =
      json.getFields("id", "result") match {
        case Seq(JsNumber(id), o) => Response(id.intValue, o)
        case _ =>
          throw DeserializationException("Missing field `id` or `result`.")
      }
  }

  object Subscription {
    final case class Response(subscription: String, result: JsValue)

    def decode(json: JsObject): Response = {
      val response = json.fields("params").asJsObject

      response.getFields("subscription", "result") match {
        case Seq(JsString(subscription), result: JsValue) =>
          Response(subscription, result)
        case _ =>
          throw DeserializationException(
            "Missing field `subscription` or `result`."
          )
      }
    }
  }
}
