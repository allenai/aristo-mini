package org.allenai.aristomini.model

/** A question-answerkey pair */
case class ExamQuestion(question: MultipleChoiceQuestion, answerKey: String) {

  /** Consider a candidate answer and decide if it's correct or not.
    * @param candidate a candidate answer to a multiple choice question
    * @return a tuple of the selected answer label (may be None) and a boolean describing the
    *         correctness of this selected answer.
    */
  def candidateAnswerIsCorrect(candidate: MultipleChoiceAnswer): (Option[String], Boolean) = {
    val bestGuess = candidate.bestGuess
    if (bestGuess.isDefined) {
      val label = candidate.bestGuess.get.choice.label
      (Some(label), label.equals(answerKey))
    } else {
      (None, false)
    }
  }
}
