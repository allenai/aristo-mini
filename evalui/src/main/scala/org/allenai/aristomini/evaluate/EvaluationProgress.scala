package org.allenai.aristomini.evaluate

/** Progress of an evaluation.
  * @param numSolved number of questions solved.
  * @param numTotal number of questions in the evaluation
  * @param startTimeMillis time when the evaluation was started
  * @param lastAnswerMillis time of receipt of most recent answer
  * @param queued number of questions queued
  */
case class EvaluationProgress(
  numSolved: Int,
  numTotal: Int,
  startTimeMillis: Long,
  lastAnswerMillis: Long,
  queued: Int) {
  val finished: Boolean = numSolved == numTotal
  val workRemains = queued > 0
}
