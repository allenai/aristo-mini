package org.allenai.aristomini.model

/** A Multiple Choice question
  * @param stem question text
  * @param choices sequence of Choice instances
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
