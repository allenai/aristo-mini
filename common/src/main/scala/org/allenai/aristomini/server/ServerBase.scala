package org.allenai.aristomini.server

import com.datasift.dropwizard.scala.ScalaApplication
import io.dropwizard.setup.Environment

/** Boilerplate for starting a Dropwizard server.
  *
  * See documentation here: https://github.com/datasift/dropwizard-scala
  */
abstract class ServerBase(service: Object, name: String = "Server")
  extends ScalaApplication[ServiceConfiguration] {

  override def getName = name

  override def run(configuration: ServiceConfiguration, environment: Environment) {
    service match {
      case services: Seq[_] => services.foreach(environment.jersey().register(_))
      case _ => environment.jersey().register(service)
    }
    environment.healthChecks().register("health-checker", HealthChecker)
  }
}
