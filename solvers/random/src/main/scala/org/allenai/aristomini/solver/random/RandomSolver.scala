package org.allenai.aristomini.solver.random

import org.allenai.aristomini.model.{ Choice, ChoiceConfidence, MultipleChoiceAnswer, MultipleChoiceQuestion }
import org.allenai.aristomini.solver.SolverBase

/** An random guess solver. */
object RandomSolver extends SolverBase {

  override def solverInfo: String = "RandomGuesser"

  /** Choose random confidences for each of the available choices.
    * @param question the MultipleChoiceQuestion instance.
    * @return an AnswerMC instance
    */
  override def answerQuestion(question: MultipleChoiceQuestion): MultipleChoiceAnswer = {

    // Sleep before responding to simulate work done. :-)
    val sleepTimeMs = (Math.random() * 1000.0).toLong
    Thread.sleep(sleepTimeMs)

    MultipleChoiceAnswer(
      question.choices.map {
        case (choice: Choice) => ChoiceConfidence(choice, Math.random)
      }
    )
  }
}
