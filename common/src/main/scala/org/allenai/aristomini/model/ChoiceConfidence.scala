package org.allenai.aristomini.model

/** Confidence of a given choice.
  * @param choice answer choice
  * @param confidence the confidence as a number
  */
case class ChoiceConfidence (choice: Choice, confidence: Double)
