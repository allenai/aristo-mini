package org.allenai.aristomini.solver

import org.allenai.aristomini.model.{ MultipleChoiceAnswer, MultipleChoiceQuestion }

import com.codahale.metrics.annotation.Timed

import javax.ws.rs._
import javax.ws.rs.core.MediaType

@Path("/")
@Consumes(Array(MediaType.APPLICATION_JSON))
@Produces(Array(MediaType.APPLICATION_JSON))
trait SolverBase {

  def answerQuestion(question: MultipleChoiceQuestion): MultipleChoiceAnswer

  def solverInfo: String

  @POST
  @Path("/answer")
  @Timed
  def answer(question: MultipleChoiceQuestion): SolverAnswer = {
    SolverAnswer(
      solverInfo = solverInfo,
      multipleChoiceAnswer = answerQuestion(question)
    )
  }

  @GET
  @Path("/solver-info")
  @Timed
  def info: String = solverInfo
}
