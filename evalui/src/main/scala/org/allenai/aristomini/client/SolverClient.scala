package org.allenai.aristomini.client

import org.allenai.aristomini.jackson.JacksonMapper
import org.allenai.aristomini.model.MultipleChoiceQuestion
import org.allenai.aristomini.solver.SolverAnswer

import java.net.{ ConnectException, URI }

/** An interface to a specific solver */
class SolverClient(hostname: String, port: Int) {

  val uriPrefixString = s"http://$hostname:$port"
  val infoUri = new URI(s"$uriPrefixString/solver-info")
  val answerUri = new URI(s"$uriPrefixString/answer")

  /** Report on a solver's reachability and its solver-info message
    * @return a SolverInfo instance
    */
  def solverInfo(): SolverInfo = {
    try {
        SolverInfo(infoUri, reachable = true, SimpleHttpClient.get(infoUri))
    } catch {
      case e: ConnectException => SolverInfo(infoUri, reachable = false, e.toString)
    }
  }

  /** Answer a Non-Diagram Multiple Choice question using this solver.
    * @param multipleChoiceQuestion the question to ask
    * @return an instance of SolverAnswer
    */
  def answer(multipleChoiceQuestion: MultipleChoiceQuestion): SolverAnswer = {
    val responseString = SimpleHttpClient.postJson(
      answerUri,
      JacksonMapper.default.writeValueAsString(multipleChoiceQuestion)
    )

    JacksonMapper.default.readValue(responseString, classOf[SolverAnswer])
  }
}
