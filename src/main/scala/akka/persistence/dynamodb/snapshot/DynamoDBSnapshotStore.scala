/**
 * Copyright (C) 2016 Typesafe Inc. <http://www.typesafe.com>
 */
package akka.persistence.dynamodb.snapshot

import akka.actor.ActorLogging
import akka.persistence.dynamodb._
import akka.persistence.snapshot.SnapshotStore
import akka.persistence.{ SelectedSnapshot, SnapshotMetadata, SnapshotSelectionCriteria }
import akka.serialization.SerializationExtension
import com.typesafe.config.Config

import scala.concurrent.Future

class DynamoDBSnapshotStore(config: Config) extends SnapshotStore with DynamoDBSnapshotRequests with ActorLogging {
  val settings = new DynamoDBSnapshotPluginConfig(config)
  val dynamo = dynamoClient(context.system, settings)
  val serialization = SerializationExtension(context.system)

  override def preStart(): Unit = {
    // eager initialization, but not from constructor
    self ! DynamoDBSnapshotStore.Init
  }

  override def loadAsync(persistenceId: String, criteria: SnapshotSelectionCriteria): Future[Option[SelectedSnapshot]] =
    load(persistenceId, criteria)

  override def saveAsync(metadata: SnapshotMetadata, snapshot: Any): Future[Unit] =
    save(metadata.persistenceId, metadata.sequenceNr, metadata.timestamp, snapshot)

  override def deleteAsync(metadata: SnapshotMetadata): Future[Unit] =
    delete(metadata)

  override def deleteAsync(persistenceId: String, criteria: SnapshotSelectionCriteria): Future[Unit] =
    delete(persistenceId, criteria)

}

object DynamoDBSnapshotStore {
  private case object Init
}
