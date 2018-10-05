/**
 * Copyright (C) 2016 Typesafe Inc. <http://www.typesafe.com>
 */
package akka.persistence.dynamodb
import com.typesafe.config.Config

trait DynamoDBPluginConfig {

  val ClientDispatcher: String
  val Tracing: Boolean
  val MaxBatchGet: Int
  val MaxBatchWrite: Int
  val MaxItemSize: Int
  val Table: String
  val JournalName: String

  val DynamoDB: DynamoDBConfig

  val ApiRequestMaxRetries: Int
  val ApiRequestInitialBackoff: Int

}

case class DynamoDBConfig(config: Config) {

  def AwsRegion = config getString "dynamodb.region"
  def DynamoPort = config getInt "dynamodb.port"
  def DynamoHost = config getString "dynamodb.host"
  def DynamoParallelism = config getInt "dynamodb.parallelism"

}

abstract class DynamoDBPluginConfigBase(c: Config) extends DynamoDBPluginConfig {

  val DynamoDB = DynamoDBConfig(c getConfig "dynamodb")

  val JournalName = c getString "journal-name"
  val ClientDispatcher = c getString "client-dispatcher"
  val Tracing = c getBoolean "tracing"

  val MaxBatchGet = c getInt "aws-api-limits.max-batch-get"
  val MaxBatchWrite = c getInt "aws-api-limits.max-batch-write"
  val MaxItemSize = c getInt "aws-api-limits.max-item-size"

  val ApiRequestMaxRetries = c getInt "aws-api-limits.max-retries"
  val ApiRequestInitialBackoff = c getInt "aws-api-limits.initial-backoff-ms"

}