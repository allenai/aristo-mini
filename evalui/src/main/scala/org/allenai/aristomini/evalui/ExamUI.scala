package org.allenai.aristomini.evalui

import org.allenai.aristomini.evaluate.{ Evaluation, Evaluator }
import org.allenai.aristomini.exams.ExamCollection
import org.allenai.aristomini.jackson.JacksonMapper
import org.allenai.aristomini.model.{ ExamMultipleChoice, ExamQuestion, MultipleChoiceAnswer }

import com.codahale.metrics.annotation.Timed

import java.net.URI
import javax.ws.rs._
import javax.ws.rs.core.Response
import scala.xml.Elem

/** Handlers for exam-specific requests. */
@Path("/eval/exam/")
@Produces(Array("text/html; charset=utf-8"))
object ExamUI {

  @GET
  @Path("/")
  @Timed
  def splash: String = Common.pageWrapper(
    <div>
      <h1>Evaluate an exam</h1>
      <h2>Available exams</h2>
      <ul>
        {ExamCollection.exams.toSeq.sortBy(_._1).map {
        case (id, exam) => <li>
          <a href={s"id/$id"}>
            {exam.description}
          </a>
        </li>
      }}
      </ul>
    </div>
  )

  @GET
  @Path("/id/{exam_id}")
  @Timed
  def examById(
    @PathParam("exam_id") examId: Int,
    @QueryParam("restart") restart: Boolean
  ): String = {

    if (restart) {
      Evaluator.newEvaluationForExam(examId)
      throw new WebApplicationException(
        Response.seeOther(new URI(s"/eval/exam/id/$examId")).build
      )
    }

    val exam = ExamCollection.exams(examId)
    val evaluation = Evaluator.evaluationForExam(examId)

    Common.pageWrapper(
      <div>
        <h1>Exam:
          {exam.description}
        </h1>

        <h2>Evaluation progress</h2>
        <span>
          {describeEvaluation(evaluation)}
        </span>

        <h2>Results</h2>
        <p>Score:
          <b>
            {f"${evaluation.score}%.0f%%"}
          </b>
        </p>{questionTable(exam, evaluation)}
      </div>
    )
  }

  /** Generate a table for an evaluation of an exam.
    * @param exam       the exam questions to be shown
    * @param evaluation the evaluation of this exam so far
    * @return a table
    */
  private def questionTable(exam: ExamMultipleChoice, evaluation: Evaluation): Elem = {
    <table border="1">
      <tr>
        <th>Answer</th> <th>Key</th> <th>Question</th>
      </tr>{exam.questions.zipWithIndex.map {
      case (examQuestion: ExamQuestion, questionNumber: Int) => {
        val answerOpt = evaluation.answerForQuestion(questionNumber)
        val solverAnswer =
          if (answerOpt.isEmpty) {
            <span>pending</span>
          }
          else {
            describeAnswer(examQuestion, answerOpt.get)
          }

        <tr>
          <td style="white-space: nowrap;">
            {solverAnswer}
          </td>
          <td>
            {examQuestion.answerKey}
          </td>
          <td>
            {examQuestion.question.oneLine}
          </td>
        </tr>
      }
    }}
    </table>
  }

  /** Describe progress of an evaluation.
    * @param evaluation the evaluation to describe
    * @return a description
    */
  private def describeEvaluation(evaluation: Evaluation): Elem = {
    val progress = evaluation.progress
    if (progress.finished) {
      <span>
        Evaluation finished.
        <a href="?restart=true">Go again.</a>
      </span>
    }
    else {
      val unreachableWarning = if (!Evaluator.connectedSolverInfo.reachable) {
        <span>
          Evaluation cannot complete because the solver is unreachable. Start a solver and
          restart this evaluation.
        </span>
      }
      else {
        <span></span>
      }

      <div>
        <p>
          Evaluation in progress.
          {progress.numSolved}
          /
          {progress.numTotal}
          questions done. Refresh page to see progress, or
          <a href="?restart=true">abort and restart.</a>
        </p>
        <p>
          {unreachableWarning}
        </p>
      </div>
    }

  }

  /** Describe an candidate answer for a question-answer pair.
    * @param examQuestion    the exam question
    * @param candidateAnswer the candidate answer
    * @return a description
    */
  private def describeAnswer(
    examQuestion: ExamQuestion,
    candidateAnswer: MultipleChoiceAnswer
  ): Elem = {

    val (selectedAnswer, isCorrect) =
      examQuestion.candidateAnswerIsCorrect(candidateAnswer)

    val checkmark: Elem =
      if (isCorrect) {
        <span style="color: green">✓</span>
      }
      else {
        <span style="color: red">✗</span>
      }

    <span title={JacksonMapper.default.writeValueAsString(candidateAnswer)}>
      {selectedAnswer.getOrElse("?")}{checkmark}
    </span>
  }
}
