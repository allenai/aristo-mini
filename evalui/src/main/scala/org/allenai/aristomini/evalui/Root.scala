package org.allenai.aristomini.evalui

import javax.ws.rs._

/** Handler for the root page */
@Path("/")
object Root {
  @GET
  @Path("/")
  def response: String = {
    Common.header +
    <div>
      <ul>
        <li><a href="/eval/exam/">Evaluate an exam</a></li>
      </ul>
    </div>
    .toString
  }

}
