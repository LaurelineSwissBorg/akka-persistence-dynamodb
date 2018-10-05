/**
 * Copyright (C) 2016 Typesafe Inc. <http://www.typesafe.com>
 */
package akka.persistence.dynamodb.journal

import akka.persistence.dynamodb.{ DynamoDBPluginConfig, DynamoDBPluginConfigBase }
import com.typesafe.config.Config

class DynamoDBJournalPluginConfig(c: Config) extends DynamoDBPluginConfigBase(c) {

  val Table = c getString "journal-table"

  val SequenceShards = c getInt "sequence-shards"
  val ReplayParallelism = c getInt "replay-parallelism"
  val LogConfig = c getBoolean "log-config"

  override def toString: String = "DynamoDBJournalPluginConfig(" +
    "Table:" + Table +
    ",JournalName:" + JournalName +
    ",ClientDispatcher:" + ClientDispatcher +
    ",SequenceShards:" + SequenceShards +
    ",ReplayParallelism" + ReplayParallelism +
    ",Tracing:" + Tracing +
    ",MaxBatchGet:" + MaxBatchGet +
    ",MaxBatchWrite:" + MaxBatchWrite +
    ",MaxItemSize:" + MaxItemSize +
    ")"
}
