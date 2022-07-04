package foresight.indexer

import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.stream.scaladsl.Flow
import common.indexer._
import common.model._
import foresight.model._
import scala.concurrent._
import slick.jdbc.GetResult

//noinspection TypeAnnotation
final case class RawInserter(session: SlickSession) {
  import session.profile.api._

  implicit val ec = session.db.executor.executionContext
  implicit val se = session

  def insertHeaderQuery(raw: Raw.Block) =
    sqlu"""INSERT INTO raw_blocks VALUES (
        ${raw.height.value}, 
        ${raw.createdAt.value}, 
        ${raw.header.toString()}::jsonb
        )"""

  def insertTransactionQuery(raw: Raw.PendingTransaction) =
    sqlu"""INSERT INTO raw_transactions(hash, created_at, data) VALUES (
        ${raw.hash.value},
        ${raw.createdAt.value},
        ${raw.data.toString()}::jsonb
        )"""

  def updateMinedTransactionQuery(height: Height)(raw: Raw.MinedTransaction) =
    sqlu"""INSERT INTO raw_transactions(hash, block_height, created_at, mined_at, data) VALUES (
        ${raw.hash.value},
        ${height.value}, 
        ${raw.minedAt.value},
        ${raw.minedAt.value}, 
        ${raw.data.toString()}::jsonb
        ) ON CONFLICT (hash)
        DO
          UPDATE
          SET block_height = ${height.value}, mined_at = ${raw.minedAt.value}
          WHERE hash = ${raw.hash.value}"""

  def updateDroppedTransactionQuery(raw: Raw.DroppedTransaction) =
    sqlu"""UPDATE raw_transactions
          SET dropped_at = ${raw.droppedAt.value}
          WHERE hash = ${raw.hash.value}"""

  def insertBlockQuery(raw: Raw.Block) = {
    val header     = insertHeaderQuery(raw)
    val transactions = raw.transactions.map(updateMinedTransactionQuery(raw.height))
    DBIO.fold(Seq(header) ++ transactions, 0)(_ + _).transactionally
  }

  def topHeightQuery =
    sql"SELECT height from raw_blocks ORDER BY height desc LIMIT 1"
      .as(GetResult(r => Height(r.nextInt())))
      .headOption

  def insertBlock() = Flow[Raw.Block].via(Slick.flow(insertBlockQuery))

  def insertTransaction() = Flow[Raw.PendingTransaction].via(Slick.flow(insertTransactionQuery))

  def topHeight: Future[Option[Height]] =
    session.db.run(topHeightQuery)
}
