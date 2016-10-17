package org.allenai.aristomini.jackson

import com.fasterxml.jackson.databind.{ ObjectMapper, SerializationFeature }
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/** A JacksonMapper for serializing objects to JSON strings */
object JacksonMapper {

  val default = new ObjectMapper()
  default.registerModule(DefaultScalaModule)
  default.enable(SerializationFeature.INDENT_OUTPUT)

}
