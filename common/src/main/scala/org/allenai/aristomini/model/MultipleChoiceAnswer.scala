package org.allenai.aristomini.model

/** A multiple choice answer. Choice confidences are stored in a sequence of ChoiceConfidence
  * instances. */
case class MultipleChoiceAnswer(choiceConfidences: Seq[ChoiceConfidence]) {

  // tolerance within which two confidences are considered equal
  private val equalConfidenceTolerance: Double = 1e-6

  /** The choice with the highest confidence value. In case where there aren't any choices, or
    * when there's a tie for the top confidence, we return None.
    * @return an optional (choice letter, confidence) tuple, or None if there isn't a best choice.
    */
  def bestGuess: Option[ChoiceConfidence] =  {

    // get the choice with the maximum confidence
    // val (letter, maxConfidence: Double) =
    val bestChoice: ChoiceConfidence = choiceConfidences.maxBy(_.confidence)

    // count number of choices with this max confidence
    val numChoicesWithMaxConfidence = choiceConfidences.count {
      case (choiceConfidence: ChoiceConfidence) =>
        Math.abs(choiceConfidence.confidence - bestChoice.confidence) < equalConfidenceTolerance
    }

    if (numChoicesWithMaxConfidence == 1) {
      Some(bestChoice)
    } else {
      None
    }
  }

}
