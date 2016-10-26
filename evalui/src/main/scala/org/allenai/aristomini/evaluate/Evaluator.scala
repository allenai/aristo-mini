package org.allenai.aristomini.evaluate

import org.allenai.aristomini.client.{ SolverClient, SolverInfo }
import org.allenai.aristomini.exams.ExamCollection
import org.allenai.aristomini.model.MultipleChoiceQuestion
import org.allenai.aristomini.solver.SolverAnswer

/** An evaluator that can be asked to solve one question, or evaluate an entire exam. */
object Evaluator {

  // Hard-coded connection
  private val solverClient = new SolverClient("localhost", 8000)

  // Previously attempted evaluations
  private val evaluations = scala.collection.mutable.Map[Int, Evaluation]()

  /** Info about the solver connected to this evaluator.
    * @return an instance of SolverInfo
    */
  def connectedSolverInfo: SolverInfo =
    solverClient.solverInfo()

  /** Solve one question and return a the answer. This blocking on the answer to arrive. */
  def solveOneQuestion(multipleChoiceQuestion: MultipleChoiceQuestion): SolverAnswer =
    solverClient.answer(multipleChoiceQuestion)

  /** Retrieve the evaluation for the specified exam, or start a new one.
    * @param examId exam identifier
    * @return an Evaluation instance
    */
  def evaluationForExam(examId: Int): Evaluation =
    evaluations.getOrElse(examId, {
      val newEval = new Evaluation(ExamCollection.exams(examId))
      evaluations.update(examId, newEval)
      newEval
    })

  /** Start a new evaluation for a the specified exam, forcibly stopping an existing evaluation
    * if it exists.
    * @param examId exam identifier
    * @return an Evaluation instance
    */
  def newEvaluationForExam(examId: Int): Evaluation = {
    evaluations.remove(examId).foreach(_.stop)
    evaluationForExam(examId)
  }
}

