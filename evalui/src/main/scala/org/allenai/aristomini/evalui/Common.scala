package org.allenai.aristomini.evalui

import org.allenai.aristomini.evaluate.Evaluator

import scala.xml.Elem

/** Elements that are commonly used on all UI pages. */
object Common {

  def pageWrapper(content: Elem): String =
    <html>
      <head>
        <meta charset="UTF-8"/>
      </head>
      <body>
        <div>
          <h1>
            <a href="/">Aristo mini: Evaluation UI</a>
          </h1>
          <div>Solver connected:
            {solverInfoHtml}
          </div>
          <hr/>
        </div>{content}
      </body>
    </html>
        .toString

  /** A description of the connected solver. */
  private def solverInfoHtml: Elem = {
    val solverInfo = Evaluator.connectedSolverInfo

    if (solverInfo.reachable) {
      <span>
        <a href={solverInfo.uri.toString}>{solverInfo.info}</a>
      </span>
    }
    else {
      <span>
        Unreachable: {solverInfo.uri.toString}
      </span>
    }
  }

}
