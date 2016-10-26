package org.allenai.aristomini.evaluate

import org.allenai.aristomini.model._
import org.allenai.aristomini.solver.SolverAnswer

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
  private val solverAnswers: mutable.Map[Integer, SolverAnswer] =
    scala.collection.mutable.Map[Integer, SolverAnswer]()

  private val startTimeMillis: Long = System.currentTimeMillis()
  private var lastAnswerMillis: Long = 0
  private var queued: Int = 0

  // Start the evaluation immediately.
  start()

  /** Start an evaluation */
  private def start(): Unit = {
    examQuestions.zipWithIndex.foreach {
      case (examQuestion: ExamQuestion, questionNumber: Int) =>
        // enqueue work to solve this question
        Future {
          this.synchronized {
            queued += 1
          }
          try {
            val solverAnswer = Evaluator.solveOneQuestion(examQuestion.question)
            // update state in a thread-safe way
            this.synchronized {
              solverAnswers.update(questionNumber, solverAnswer)
              lastAnswerMillis = System.currentTimeMillis()
            }
          } finally {
            this.synchronized {
              queued -= 1
            }
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
  def progress: EvaluationProgress = EvaluationProgress(
    solverAnswers.size,
    examQuestions.size,
    startTimeMillis,
    lastAnswerMillis,
    queued
  )

  /** Calculate a score for answers submitted so far. This assumes every question is worth one
    * single point.
    * @return an EvaluationScore
    */
  def score: EvaluationScore = {
    var correct = 0
    var answered = 0

    examQuestions.zipWithIndex.foreach {
      case (examQuestion: ExamQuestion, questionNumber: Int) => {
        val answerOpt = solverAnswerForQuestion(questionNumber)
        if (answerOpt.nonEmpty) {
          answered += 1
          val (_, isCorrect) = examQuestion.candidateAnswerIsCorrect(
            answerOpt.get.multipleChoiceAnswer
          )
          if (isCorrect) {
            correct += 1
          }
        }
      }
    }

    EvaluationScore(correct, answered)
  }

  /** The answer for a given question.
    * @param questionNumber the question number ot look up
    * @return an optional answer, None if it hasn't been answered yet.
    */
  def solverAnswerForQuestion(questionNumber: Integer): Option[SolverAnswer] = {
    solverAnswers.get(questionNumber)
  }

}
