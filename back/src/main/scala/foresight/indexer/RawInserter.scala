package foresight.indexer

import akka.NotUsed
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.stream.scaladsl.Flow
import common.model._
import foresight.model._
import foresight.model.Raw.BaseFeeByHeight
import scala.concurrent._
import slick.jdbc.GetResult

//noinspection TypeAnnotation
final case class RawInserter(session: SlickSession) {
  import session.profile.api._

  implicit val ec = session.db.executor.executionContext
  implicit val se = session

  def insertHeaderQuery(raw: Raw.Block) =
    sqlu"""INSERT INTO raw_blocks (height, created_at, data) VALUES (
        ${raw.height.value}, 
        ${raw.createdAt}, 
        ${raw.header.toString()}::jsonb
        ) ON CONFLICT (height) 
        DO 
          UPDATE SET
          created_at = EXCLUDED.created_at,
          data = EXCLUDED.data
          """

  def insertRawTransactionQuery(raw: Raw.PendingTransaction) =
    sqlu"""INSERT INTO raw_transactions(hash, created_at, data) VALUES (
        ${raw.hash},
        ${raw.createdAt},
        ${raw.data.toString()}::jsonb
        ) ON CONFLICT (hash) DO NOTHING"""

  def insertProcessedTransactionQuery(raw: Raw.PendingTransaction) = {
    val processed = Processed.Transaction.fromPending(raw)
    sqlu"""INSERT INTO processed_transactions(
          hash,
          type,
          created_at,
          block_height,
          block_hash,
          sender,
          receiver,
          gas,
          gas_price,
          max_fee_per_gas,
          max_priority_fee_per_gas,
          nonce,
          transaction_index,
          input,
          value
        ) VALUES (
          ${processed.hash},
          ${processed.transactionType.value},
          ${processed.createdAt},
          ${processed.blockHeight.map(_.value)},
          ${processed.blockHash},
          ${processed.sender},
          ${processed.receiver},
          ${processed.gas},
          ${processed.gasPrice},
          ${processed.maxFeePerGas},
          ${processed.maxPriorityFeePerGas},
          ${processed.nonce},
          ${processed.transactionIndex},
          ${processed.input},
          ${processed.value}
        )"""
  }

  def updateMinedTransactionQuery(height: Height)(raw: Raw.MinedTransaction) =
    sqlu"""INSERT INTO raw_transactions(hash, block_height, created_at, mined_at, data) VALUES (
        ${raw.hash},
        ${height.value},
        ${raw.minedAt},
        ${raw.minedAt},
        ${raw.data.toString()}::jsonb
        ) ON CONFLICT (hash)
        DO
          UPDATE SET 
            block_height = ${height.value}, 
            mined_at = ${raw.minedAt.value}
          """

  def updateMinedProcessedTransactionQuery(
      height: Height
  )(raw: Raw.MinedTransaction) = {
    val processed = Processed.Transaction.fromMined(raw)
    sqlu"""UPDATE processed_transactions
            SET block_hash = ${processed.blockHash},
                mined_at = ${processed.minedAt},
                block_height = ${processed.blockHeight.map(_.value)}
            WHERE hash = ${processed.hash}
        """
  }

  def updateBaseFeeQuery(fee: BaseFeeByHeight) = {
    sqlu"""
          UPDATE processed_transactions
          SET base_fee = ${fee.baseFee}
          WHERE block_height = ${fee.height.value}
        """
  }

  def updateNextBaseFeeQuery(nextBase: BigDecimal) = {
    sqlu"""
          UPDATE processed_transactions
          SET base_fee = $nextBase
          WHERE block_height = NULL
        """
  }

  def updateBaseFeeBatch(batch: Raw.BaseFeeBatch) = {
    DBIO
      .fold(
        batch.batch.map(updateBaseFeeQuery) ++ Seq(
          updateNextBaseFeeQuery(batch.nextBase)
        ),
        0
      )(_ + _)
      .transactionally

  }

  def updateDroppedTransactionQuery(raw: Raw.DroppedTransaction) =
    sqlu"""UPDATE raw_transactions
          SET dropped_at = ${raw.droppedAt.value}
          WHERE hash = ${raw.hash}"""

  def insertPendingTransaction(raw: Raw.PendingTransaction) = {
    DBIO
      .fold(
        Seq(
          insertRawTransactionQuery(raw),
          insertProcessedTransactionQuery(raw)
        ),
        0
      )(_ + _)
      .transactionally
  }
  def insertBlockQuery(raw: Raw.Block) = {
    val header = insertHeaderQuery(raw)
    val transactions =
      raw.transactions.map(updateMinedTransactionQuery(raw.height))
    val processedTransactions =
      raw.transactions.map(updateMinedProcessedTransactionQuery(raw.height))
    DBIO
      .fold(Seq(header) ++ transactions ++ processedTransactions, 0)(_ + _)
      .transactionally
  }

  def getProcessedTransactionByBlockHeightQuery(blockHeight: Int) =
    sql"""SELECT 
         hash,
          type,
          block_height,
          created_at,
          mined_at,
          dropped_at,
          block_hash,
          sender,
          receiver,
          value,
          gas,
          gas_price,
          max_fee_per_gas,
          max_priority_fee_per_gas,
          input,
          nonce,
          transaction_index,
          status,
          tip
         FROM 
            processed_transactions
        WHERE 
            block_height = $blockHeight
       """.as(
      GetResult(r =>
        Processed.Transaction(
          hash = r.nextString(),
          transactionType = r.nextString() match {
            case "Legacy" => Processed.TransactionType.Legacy
            case _        => Processed.TransactionType.EIP1559
          },
          blockHeight = r.nextIntOption().map(Height(_)),
          createdAt = r.nextTimestamp(),
          minedAt = r.nextTimestampOption(),
          droppedAt = r.nextTimestampOption(),
          blockHash = r.nextStringOption(),
          sender = r.nextString(),
          receiver = r.nextString(),
          value = r.nextBigDecimal(),
          gas = r.nextBigDecimal(),
          gasPrice = r.nextStringOption().map(HexNumber(_).toBigDecimal),
          maxFeePerGas = r.nextStringOption().map(HexNumber(_).toBigDecimal),
          maxPriorityFeePerGas =
            r.nextStringOption().map(HexNumber(_).toBigDecimal),
          input = r.nextString(),
          nonce = r.nextBigDecimal(),
          transactionIndex =
            r.nextStringOption().map(HexNumber(_).toBigDecimal),
          tip = r.nextBigDecimalOption()
        )
      )
    )

  def getProcessedTransactionQuery =
    sql"""SELECT 
         hash,
          type,
          block_height,
          created_at,
          mined_at,
          dropped_at,
          block_hash,
          sender,
          receiver,
          value,
          gas,
          gas_price,
          max_fee_per_gas,
          max_priority_fee_per_gas,
          input,
          nonce,
          transaction_index,
          value,
          status,
          tip
         FROM 
            processed_transactions
        where mined_at is null and created_at > now() - interval '3 hours'
        order by gas_price desc
        limit 500
       """.as(
      GetResult(r =>
        Processed.Transaction(
          hash = r.nextString(),
          transactionType = r.nextString() match {
            case "Legacy" => Processed.TransactionType.Legacy
            case _        => Processed.TransactionType.EIP1559
          },
          blockHeight = r.nextIntOption().map(Height(_)),
          createdAt = r.nextTimestamp(),
          minedAt = r.nextTimestampOption(),
          droppedAt = r.nextTimestampOption(),
          blockHash = r.nextStringOption(),
          sender = r.nextString(),
          receiver = r.nextString(),
          value = r.nextBigDecimal(),
          gas = r.nextBigDecimal(),
          gasPrice = r.nextStringOption().map(HexNumber(_).toBigDecimal),
          maxFeePerGas = r.nextStringOption().map(HexNumber(_).toBigDecimal),
          maxPriorityFeePerGas =
            r.nextStringOption().map(HexNumber(_).toBigDecimal),
          input = r.nextString(),
          nonce = r.nextBigDecimal(),
          transactionIndex =
            r.nextStringOption().map(HexNumber(_).toBigDecimal),
          tip = r.nextBigDecimalOption()
        )
      )
    )

  def getMemPoolQuery =
    sql"""SELECT 
         count(*)
         FROM 
            processed_transactions
        WHERE 
             mined_at is null and created_at > now() - interval '3 hours'
       """.as[Int](
      GetResult(r => r.nextInt())
    )

  def getProcessedTransactionByAddressQuery(address: String) =
    sql"""SELECT
         hash,
          type,
          block_height,
          created_at,
          mined_at,
          dropped_at,
          block_hash,
          sender,
          receiver,
          value,
          gas,
          gas_price,
          max_fee_per_gas,
          max_priority_fee_per_gas,
          input,
          nonce,
          transaction_index,
          value,
          status,
          tip
         FROM
            processed_transactions
        WHERE
            (sender = $address OR receiver = $address) AND (
            created_at > NOW() - interval '5 second' OR
            mined_at > NOW() - interval '5 second'
            )
       """.as(
      GetResult(r =>
        Processed.Transaction(
          hash = r.nextString(),
          transactionType = r.nextString() match {
            case "Legacy" => Processed.TransactionType.Legacy
            case _        => Processed.TransactionType.EIP1559
          },
          blockHeight = r.nextIntOption().map(Height(_)),
          createdAt = r.nextTimestamp(),
          minedAt = r.nextTimestampOption(),
          droppedAt = r.nextTimestampOption(),
          blockHash = r.nextStringOption(),
          sender = r.nextString(),
          receiver = r.nextString(),
          value = r.nextBigDecimal(),
          gas = r.nextBigDecimal(),
          gasPrice = r.nextStringOption().map(HexNumber(_).toBigDecimal),
          maxFeePerGas = r.nextStringOption().map(HexNumber(_).toBigDecimal),
          maxPriorityFeePerGas =
            r.nextStringOption().map(HexNumber(_).toBigDecimal),
          input = r.nextString(),
          nonce = r.nextBigDecimal(),
          transactionIndex =
            r.nextStringOption().map(HexNumber(_).toBigDecimal),
          tip = r.nextBigDecimalOption()
        )
      )
    )

  def getProcessedTransactionByAddress(address: String): Future[List[Processed.Transaction]] =
    session.db.run(getProcessedTransactionByAddressQuery(address)).map(_.toList)

  def getProcessedTransaction: Future[List[Processed.Transaction]] =
    session.db.run(getProcessedTransactionQuery).map(_.toList)

  def getMemPool: Future[List[Int]] =
    session.db.run(getMemPoolQuery).map(_.toList)

  def getProcessedTransactionByBockHeight(
      blockHeight: Int
  ): Future[List[Processed.Transaction]] =
    session.db
      .run(getProcessedTransactionByBlockHeightQuery(blockHeight))
      .map(_.toList)

  def insertBlock() = Flow[Raw.Block].via(Slick.flow(insertBlockQuery))

  def updateBaseFee(): Flow[Raw.BaseFeeBatch, Int, NotUsed] =
    Flow[Raw.BaseFeeBatch].via(Slick.flow(updateBaseFeeBatch))

  def insertTransaction() =
    Flow[Raw.PendingTransaction].via(Slick.flow(insertPendingTransaction))

}
