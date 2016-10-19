package org.allenai.aristomini.model

/** A question choice.
  * @param label label of the choice (e.g., "A")
  * @param text  text of the choice (e.g., "blue")
  */
case class Choice(label: String, text: String)

object Choice {

  /** Make a sequence of Choice instances from a sequence of label-text pairs.
    * @param pairs sequence of label-text pairs
    * @return sequence of Choice instances
    */
  def fromSeqOfPairs(pairs: Seq[(String, String)]): Seq[Choice] = {
    val choices: Seq[Choice] = pairs.map {
      case (label: String, choiceText: String) => Choice(label, choiceText)
    }
    choices
  }
}