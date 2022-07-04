package foresight.indexer

import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.stream.scaladsl.Flow
import common.indexer._
import common.model._
import foresight.model._
import scala.concurrent._
import slick.jdbc.GetResult

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

  def insertTransactionQuery(height: Height)(raw: Raw.Transaction) =
    sqlu"""INSERT INTO raw_extrinsics VALUES (
        ${raw.hash.value},
        ${height.value}, 
        ${raw.createdAt.value}, 
        ${raw.data.toString()}::jsonb
        )"""

  def insertRawQuery(raw: Raw.Block) = {
    val header     = insertHeaderQuery(raw)
    val transactions = raw.transactions.map(insertTransactionQuery(raw.height))
    DBIO.fold(Seq(header) ++ transactions, 0)(_ + _).transactionally
  }

  def topHeightQuery =
    sql"SELECT height from raw_blocks ORDER BY height desc LIMIT 1"
      .as(GetResult(r => Height(r.nextInt())))
      .headOption

  def insert = Flow[Raw.Block].via(Slick.flow(insertRawQuery))

  def topHeight: Future[Option[Height]] =
    session.db.run(topHeightQuery)
}
