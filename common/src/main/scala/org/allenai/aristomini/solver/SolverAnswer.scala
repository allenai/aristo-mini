package org.allenai.aristomini.solver

import org.allenai.aristomini.model.MultipleChoiceAnswer

/** An answer from a solver.
  * @param solverInfo information about the solver
  * @param multipleChoiceAnswer the answer provided by the solver.
  */
case class SolverAnswer (solverInfo: String, multipleChoiceAnswer: MultipleChoiceAnswer)
