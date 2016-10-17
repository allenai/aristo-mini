package org.allenai.aristomini.solver.textsearch

import org.allenai.aristomini.model.{ Choice, ChoiceConfidence, MultipleChoiceAnswer, MultipleChoiceQuestion }
import org.allenai.aristomini.solver.SolverBase

import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.{ SearchHit, SearchHits }

import java.net.InetSocketAddress

/** A simple information retrieval solver based on sentences in an Elasticsearch index. */
object TextSearchSolver extends SolverBase {

  override def solverInfo: String = "TextSearchSolver: Uses a search index"

  /** Choose a confidence for each of the available choices.
    * @param question the MultipleChoiceQuestion instance.
    * @return an AnswerMC instance
    */
  override def answerQuestion(question: MultipleChoiceQuestion): MultipleChoiceAnswer = {
    // get the best score for each stem-choice pair
    MultipleChoiceAnswer(
      question.choices.map {
        case (choice: Choice) =>
          ChoiceConfidence(choice, bestScore(question.stem, choice.text).getOrElse(0d))
      }
    )
  }

  /** The elastic search client to use. */
  private val esClient = TextSearchSolver.makeEsClient(
    host = "localhost",
    port = 9300,
    clusterName = "elasticsearch"
  )

  /** Name of the document index and name of the searchable field in each document */
  private val indexName = "knowledge"
  private val fieldName = "body"

  /** Number of matching document to return with each request. We only care about
    * the best score, so only one document is needed. */
  private val topN = 1

  /** Compute the best score for a stem-choice pair.
    * @param questionStem question stem
    * @param choiceText text of a choice
    * @return a numeric score or None
    */
  private def bestScore(questionStem: String, choiceText: String): Option[Double] = {
    // query for Elasticsearch
    val queryText = s"$questionStem $choiceText"

    // submit the query and get the results
    val searchHits: SearchHits = esClient
        .prepareSearch(indexName)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(QueryBuilders.matchQuery(fieldName, queryText))
        .setSize(topN)
        .get.getHits

    // extract hits into an array and extract scores from each hit
    val hitsAsArray: Array[SearchHit] = searchHits.getHits
    val scores: Array[Double] = hitsAsArray.map(_.score.toDouble)

    // By default, results are returned by Elasticsearch sorted by descending score. So,
    // just the top score. (Or None if no results were returned.)
    scores.headOption
  }

  /** Make an Elasticsearch client connected to a server
    * @param host hostname of the service
    * @param port service port
    * @param clusterName name of the cluster to use
    * @return an instance of TransportClient
    */
  private def makeEsClient(host: String, port: Int, clusterName: String): TransportClient = {
    val address = new InetSocketAddress(host, port)
    val clientSettings = Settings.builder()
        .put("cluster.name", clusterName)
        .build()
    TransportClient.builder().settings(clientSettings).build()
        .addTransportAddress(new InetSocketTransportAddress(address))
  }

}
