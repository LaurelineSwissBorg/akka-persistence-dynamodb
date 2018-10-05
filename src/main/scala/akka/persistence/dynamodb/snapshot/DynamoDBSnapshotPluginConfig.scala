/**
 * Copyright (C) 2016 Typesafe Inc. <http://www.typesafe.com>
 */
package akka.persistence.dynamodb.snapshot

import akka.persistence.dynamodb.{ DynamoDBPluginConfig, DynamoDBPluginConfigBase }
import com.typesafe.config.Config

class DynamoDBSnapshotPluginConfig(c: Config) extends DynamoDBPluginConfigBase(c) {

  val Table = c getString "snapshot-table"

  override def toString: String = "DynamoDBJournalPluginConfig(" +
    "SnapshotTable:" + Table + ")"

}
