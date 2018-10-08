/**
 * Copyright (C) 2016 Typesafe Inc. <http://www.typesafe.com>
 */
package akka.persistence.dynamodb.journal

import com.amazonaws.services.dynamodbv2.model._

import scala.concurrent.Await
import akka.actor.ActorSystem

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.persistence.Persistence
import akka.util.Timeout
import java.util.UUID
import java.util.logging.Logger

import akka.persistence.PersistentRepr

import scala.collection.JavaConverters._
import akka.persistence.dynamodb._

trait DynamoDBUtils {

  val logger = Logger.getLogger(getClass.getName)

  val system: ActorSystem
  import system.dispatcher

  lazy val settings = {
    val c = system.settings.config
    val config = c.getConfig(c.getString("akka.persistence.journal.plugin"))
    new DynamoDBJournalPluginConfig(config)
  }
  import settings._

  lazy val client = dynamoClient(system, settings)

  implicit val timeout = Timeout(5.seconds)

  def ensureJournalTableExists(read: Long = 10L, write: Long = 10L): Unit = {
    val create = schema
      .withTableName(Table)
      .withProvisionedThroughput(new ProvisionedThroughput(read, write))

    var names = Vector.empty[String]
    lazy val complete: ListTablesResult => Future[Vector[String]] = aws =>
      if (aws.getLastEvaluatedTableName == null) Future.successful(names ++ aws.getTableNames.asScala)
      else {
        names ++= aws.getTableNames.asScala
        client
          .listTables(new ListTablesRequest().withExclusiveStartTableName(aws.getLastEvaluatedTableName))
          .flatMap(complete)
      }
    val list = client.listTables(new ListTablesRequest).flatMap(complete)

    list.foreach {
      tables ⇒
        logger.info(s"Tables: ${tables}")
    }

    val setup = for {
      exists <- list.map(_ contains Table)
      _ <- {
        if (exists) {
          logger.info(s"Table $Table exists")
          Future.successful(())
        } else {
          logger.info(s"Creating $Table")
          client.createTable(create)
        }
      }
    } yield ()

    setup.foreach { _ ⇒
      logger.info("Setup Complete")
    }
    Await.result(setup, 5.seconds)
  }

  private val writerUuid = UUID.randomUUID.toString
  def persistenceId: String = ???

  var nextSeqNr = 1
  def seqNr() = {
    val ret = nextSeqNr
    nextSeqNr += 1
    ret
  }
  var generatedMessages: Vector[PersistentRepr] = Vector(null) // we start counting at 1

  def persistentRepr(msg: Any) = {
    val ret = PersistentRepr(msg, sequenceNr = seqNr(), persistenceId = persistenceId, writerUuid = writerUuid)
    generatedMessages :+= ret
    ret
  }
}
