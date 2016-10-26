package org.allenai.aristomini.evalui

import org.allenai.aristomini.evaluate.{ Evaluation, EvaluationProgress, Evaluator }
import org.allenai.aristomini.exams.ExamCollection
import org.allenai.aristomini.jackson.JacksonMapper
import org.allenai.aristomini.model.{ ExamMultipleChoice, ExamQuestion, MultipleChoiceAnswer }

import com.codahale.metrics.annotation.Timed

import java.net.URI
import java.text.SimpleDateFormat
import javax.ws.rs._
import javax.ws.rs.core.Response
import scala.xml.{ Elem, NodeSeq }

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

    val shouldRefreshPage = evaluation.progress.workRemains

    val headSupplement : NodeSeq =
      if (shouldRefreshPage) {
        val refreshIntervalInSeconds = "1"
        <meta http-equiv="refresh" content={refreshIntervalInSeconds}/>
      }
      else {
        NodeSeq.Empty
      }

    val refreshNotice: NodeSeq =
      if (shouldRefreshPage) {
        <p>
          Page will refresh automatically until the evaluation is finished. Or you
          can <a href="?restart=true">abort and restart.</a>
        </p>
      }
      else {
        NodeSeq.Empty
      }

    Common.pageWrapper(
      headSupplement = headSupplement,
      content =
          <div>
            <h1>Exam:
              {exam.description}
            </h1>

            <h2>Evaluation progress</h2>
            <span>
              {describeProgress(evaluation.progress)}

              {refreshNotice}
            </span>

            <h2>Results</h2>
            <p>Score:
              <b>
                {evaluation.score.correct}
                correct /
                {evaluation.score.answered}
                answered =
                {f"${evaluation.score.percentCorrect}%.0f%%"}
                correct.
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
        <th>Question ID</th> <th>Question</th> <th>Key</th> <th>Answer</th> <th>Solver</th>
      </tr>{exam.questions.zipWithIndex.map {
      case (examQuestion: ExamQuestion, questionNumber: Int) => {
        val answerOpt = evaluation.solverAnswerForQuestion(questionNumber)

        val solverAnswer =
          if (answerOpt.isEmpty) {
            <span>pending</span>
          }
          else {
            describeAnswer(examQuestion, answerOpt.get.multipleChoiceAnswer)
          }

        val solverInfo =
          if (answerOpt.isEmpty) {
            <span>pending</span>
          }
          else {
            <code>{answerOpt.get.solverInfo}</code>
          }

        <tr>
          <td>
            {examQuestion.id}
          </td>
          <td>
            {examQuestion.question.oneLine}
          </td>
          <td>
            {examQuestion.answerKey}
          </td>
          <td style="white-space: nowrap;">
            {solverAnswer}
          </td>
          <td style="white-space: nowrap;">
            {solverInfo}
          </td>
        </tr>
      }
    }}
    </table>
  }

  /** Describe progress of an evaluation.
    * @param progress the EvaluationProgress to describe
    * @return a description
    */
  private def describeProgress(progress: EvaluationProgress): Elem = {
    val progressDescription =
      <span>
        Evaluation started at {epochTimeToString(progress.startTimeMillis)}.
        {progress.numSolved} / {progress.numTotal} questions done.
      </span>

    if (progress.finished) {
      <div>
        {progressDescription}

        <p>
          Evaluation finished at {epochTimeToString(progress.lastAnswerMillis)}.
          <a href="?restart=true">Go again.</a>
        </p>

      </div>
    }
    else if (progress.workRemains) {
      <div>
        {progressDescription}
      </div>
    }
    else {
      <div>
        {progressDescription}
        <p>
          No more work is queued. Maybe the solver is unreachable. Start a solver and
          <a href="?restart=true">restart this evaluation.</a>
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

  /** Convert an epoch time in millisconds to a human-readable string.
    * @param millisSinceEpoch milliseconds since epoch
    * @return a human-readable string
    */
  private def epochTimeToString(millisSinceEpoch : Long) : String =
    new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(millisSinceEpoch)

}
