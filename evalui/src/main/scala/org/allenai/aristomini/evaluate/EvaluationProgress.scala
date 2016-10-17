package org.allenai.aristomini.evaluate

/** Progress of an evaluation.
  * @param numSolved number of questions solved.
  * @param numTotal number of questions in the evaluation
  */
case class EvaluationProgress(numSolved: Int, numTotal: Int) {
  val finished: Boolean = numSolved == numTotal
}
