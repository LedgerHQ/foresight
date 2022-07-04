package foresight.indexer.repo.dto

import slick.jdbc.PostgresProfile.api._

class ProcessedTransactionDto(tag: Tag)
    extends Table[
      (
          String,
          String,
          Long,
          Long,
          Long,
          Long,
          String,
          String,
          Long,
          Long,
          Long,
          Long,
          String,
          Long,
          String,
          Long,
          Long
      )
    ](tag, "processed_transactions") {
  def hash: Rep[String]        = column[String]("hash")
  def `type`                   = column[String]("type")
  def block_height             = column[Long]("block_height")
  def created_at               = column[Long]("created_at")
  def mined_at                 = column[Long]("mined_at")
  def dropped_at               = column[Long]("dropped_at")
  def block_hash               = column[String]("block_hash")
  def sender                   = column[String]("sender")
  def gas                      = column[Long]("gas")
  def gas_price                = column[Long]("gas_price")
  def max_fee_per_gas          = column[Long]("max_fee_per_gas")
  def max_priority_fee_per_gas = column[Long]("max_priority_fee_per_gas")
  def input                    = column[String]("input")
  def nonce                    = column[Long]("nonce")
  def receiver                 = column[String]("receiver")
  def transaction_index        = column[Long]("transaction_index")
  def value                    = column[Long]("value")
  def * =
    (
      hash,
      `type`,
      block_height,
      created_at,
      mined_at,
      dropped_at,
      block_hash,
      sender,
      gas,
      gas_price,
      max_fee_per_gas,
      max_priority_fee_per_gas,
      input,
      nonce,
      receiver,
      transaction_index,
      value
    )
}
