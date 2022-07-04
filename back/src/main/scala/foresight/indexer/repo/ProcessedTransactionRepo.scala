package foresight.indexer.repo

import akka.stream.alpakka.slick.scaladsl.SlickSession
import foresight.indexer._
import foresight.indexer.model.ProcessedTransaction
import foresight.indexer.repo.dto.ProcessedTransactionDto
import java.sql.Date
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import slick._
import slick.jdbc.GetResult
import slick.jdbc.PostgresProfile.api._
import slick.lifted._

final case class ProcessedTransactionRepo(session: SlickSession) {
  import session.profile.api._

  implicit val ec = session.db.executor.executionContext
  implicit val se = session

  val processedTransactions = TableQuery[ProcessedTransactionDto]

  def getProcessedTransactionByUserQuery(user: String) = {
    processedTransactions
      .filter(t => t.sender == Rep(user) || t.receiver == Rep(user))
      .as(
        GetResult(t =>
          ProcessedTransaction(
            hash = t.hash,
            `type` = t.`type`,
            block_height = t.block_height,
            created_at = t.created_at,
            mined_at = t.mined_at,
            dropped_at = t.dropped_at,
            block_hash = t.block_hash,
            sender = t.sender,
            gas = t.gas,
            gas_price = t.gas_price,
            max_fee_per_gas = t.max_fee_per_gas,
            max_priority_fee_per_gas = t.max_priority_fee_per_gas,
            input = t.input,
            nonce = t.nonce,
            receiver = t.receiver,
            transaction_index = t.transaction_index,
            value = t.value
          )
        )
      )
  }

  def getProcessedTransactionByUser: Future[ProcessedTransaction] =
    session.db.run(getProcessedTransactionByUserQuery)
}
