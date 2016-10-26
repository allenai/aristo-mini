package org.allenai.aristomini.model

/** An exam question.
  *
  * @param id an identifier for this question
  * @param question an instance of MultipleChoiceQuestion representing this question
  * @param answerKey the label of the correct answer
  */
case class ExamQuestion(id: String, question: MultipleChoiceQuestion, answerKey: String) {

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
