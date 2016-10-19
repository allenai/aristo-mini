package org.allenai.aristomini.evalui

import javax.ws.rs._

/** Handler for the root page */
@Path("/")
@Produces(Array("text/html; charset=utf-8"))
object Root {
  @GET
  @Path("/")
  def response: String = Common.pageWrapper(
    <div>
      <ul>
        <li>
          <a href="/eval/exam/">Evaluate an exam</a>
        </li>
      </ul>
    </div>
  )

}
