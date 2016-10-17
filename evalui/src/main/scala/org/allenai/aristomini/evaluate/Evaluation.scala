package org.allenai.aristomini.evaluate

import org.allenai.aristomini.model._

import java.util.concurrent.Executors
import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }

/** An evaluation for an exam */
class Evaluation(exam: ExamMultipleChoice) {

  /** An execution context for doing work in the background */
  private implicit val ec = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(10)

    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable) {}
  }

  // Exam questions to be answered
  private val examQuestions: Seq[ExamQuestion] = exam.questions

  // Answered provided by the solver, keyed on the question position in examQuestions. Initially
  // empty.
  private val solverAnswers: mutable.Map[Integer, MultipleChoiceAnswer] =
    scala.collection.mutable.Map[Integer, MultipleChoiceAnswer]()

  // Start the evaluation immediately.
  start()

  /** Start an evaluation */
  private def start(): Unit = {
    examQuestions.zipWithIndex.foreach {
      case (examQuestion: ExamQuestion, questionNumber: Int) =>
        // enqueue work to solve this question
        Future {
          val answerMC = Evaluator.solveOneQuestion(examQuestion.question)
          // update solverAnswers in a thread-safe way
          solverAnswers.synchronized {
            solverAnswers.update(questionNumber, answerMC)
          }
        }
    }
  }

  /** Stop an evaluation */
  def stop(): Unit = {
    ec.threadPool.shutdownNow()
  }

  /** The current progress of this evaluation.
    * @return an EvaluationProgress instance
    */
  def progress: EvaluationProgress = EvaluationProgress(solverAnswers.size, examQuestions.size)

  /** Calculate a score for answers submitted so far. This assumed every question is worth one
    * single point.
    * @return a score from 0 to 1
    */
  def score: Double = {
    var correct = 0

    examQuestions.zipWithIndex.foreach {
      case (examQuestion: ExamQuestion, questionNumber: Int) => {
        val answerOpt = answerForQuestion(questionNumber)
        if (answerOpt.nonEmpty) {
          val (_, isCorrect) = examQuestion.candidateAnswerIsCorrect(answerOpt.get)
          if (isCorrect) {
            correct += 1
          }
        }
      }
    }

    100 * correct / exam.numQuestions
  }

  /** The answer for a given question.
    * @param questionNumber the question number ot look up
    * @return an optional answer, None if it hasn't been answered yet.
    */
  def answerForQuestion(questionNumber: Integer): Option[MultipleChoiceAnswer] = {
    solverAnswers.get(questionNumber)
  }

}
