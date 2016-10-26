package org.allenai.aristomini.evaluate

/**
  * A score for an evaluation
  * @param correct number of correctly answered questions
  * @param answered number of questions answered
  */
case class EvaluationScore(correct: Int, answered: Int) {

  /** An integer percentage between 0 and 100 */
  val percentCorrect: Int = if (answered > 0) {
    100 * correct / answered
  }
  else {
    0
  }
}
