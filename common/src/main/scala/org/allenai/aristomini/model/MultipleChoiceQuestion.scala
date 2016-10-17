package org.allenai.aristomini.model

case class Choice (label: String, text: String)

object Choice {
  def fromSeqOfPairs(pairs: Seq[(String, String)]) : Seq[Choice] = {
    val choices : Seq[Choice] = pairs.map {
      case (label: String, choiceText: String) => Choice(label, choiceText)
    }
    choices
  }
}

/** A Multiple Choice question
  * @param stem question text
  * @param choices map of choice names (e.g., "A") to choice strings
  */
case class MultipleChoiceQuestion(stem: String, choices: Seq[Choice]) {

  /** A pithy representation of this question, for display purposes */
  def oneLine: String = {
    val sb = StringBuilder.newBuilder
    sb.append(s"$stem ")
    for (choice <- choices) {
      sb.append(s"(${choice.label}) ${choice.text} ")
    }
    sb.toString
  }
}
