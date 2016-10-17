package org.allenai.aristomini.client

import java.net.URI

/** Information about solver.
  * @param uri the address of this solver
  * @param reachable true if the solver is reachable
  * @param info the solver-reported informational message
  */
case class SolverInfo(uri: URI, reachable: Boolean, info: String)
