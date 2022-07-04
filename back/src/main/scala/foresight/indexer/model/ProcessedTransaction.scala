package foresight.indexer.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class ProcessedTransaction(
    hash: String,
    `type`: String,
    block_height: Long,
    created_at: Long,
    mined_at: Long,
    dropped_at: Long,
    block_hash: String,
    sender: String,
    gas: Long,
    gas_price: Long,
    max_fee_per_gas: Long,
    max_priority_fee_per_gas: Long,
    input: String,
    nonce: Long,
    receiver: String,
    transaction_index: Long,
    value: Long
)

object ProcessedTransaction {
  implicit val codec: Codec[ProcessedTransaction] = deriveCodec
}
