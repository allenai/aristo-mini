package org.allenai.aristomini.evalui

import org.allenai.aristomini.server.ServerBase

/** The Evaluation UI server */
object EvalUIServer extends ServerBase(
  service = Seq(Root, ExamUI),
  name = "Evaluation UI"
)
