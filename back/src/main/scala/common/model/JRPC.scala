package common.model

import spray.json._

object JRPC {
  final case class Request(id: Int, method: String, params: Vector[JsValue]) {
    def encode: JsObject =
      JsObject(
        "id" -> JsNumber(id),
        "method" -> JsString(method),
        "params" -> JsArray.apply(params)
      )
  }

  final case class Response(result: JsValue)
  object Response {
    def decode(json: JsObject): Response =
      json.getFields("result") match {
        case Seq(o) => Response(o)
        case _      => throw DeserializationException("Missing field `result`.")
      }
  }

  /*
  final case class Response(id: Int, result: JsValue)
  object Response {
    def decode(json: JsObject): Response =
      json.getFields("id", "result") match {
        case Seq(JsNumber(id), o) => Response(id.intValue, o)
        case _                    => throw DeserializationException("Missing field `id` or `result`.")
      }
  }
  */
}
