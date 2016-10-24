package org.allenai.aristomini.evalui

import org.allenai.aristomini.evaluate.Evaluator

import scala.xml.{ Elem, NodeSeq }

/** Elements that are commonly used on all UI pages. */
object Common {

  /** A page wrapper for some content (Elem) and supplemental head elements (useful for
    * application-specific browser hints, like page refresh.)
    * @param content content to use for the page
    * @param headSupplement optional elements to include in the HTML head element
    * @return A string of HTML content
    */
  def pageWrapper(content: Elem, headSupplement: NodeSeq = NodeSeq.Empty): String =
    <html>
      <head>
        <meta charset="UTF-8"/>
        { headSupplement }
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
