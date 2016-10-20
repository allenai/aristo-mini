package org.allenai.aristomini.server

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result

/** A dummy health checker that always reports healthy.
  *
  * Intended to suppress health check warnings on server startup.
  */
object HealthChecker extends HealthCheck {
  override def check = {
    Result.healthy()
  }
}
