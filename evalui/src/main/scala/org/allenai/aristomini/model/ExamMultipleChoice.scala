package org.allenai.aristomini.model

import org.allenai.aristomini.jackson.JacksonMapper

import java.io.File
import scala.io.Source

/** A multiple choice exam. */
case class ExamMultipleChoice(description: String, questions: Seq[ExamQuestion]) {
  def numQuestions: Int = questions.size
}

object ExamMultipleChoice {

  /** Read from an AI2-authored JSONL file.
    * @param filename name of file to reads
    * @return an ExamMultipleChoice instance with questions from the named file
    */
  def fromAI2JSONLFile(filename: String): ExamMultipleChoice = {

    val jsonLines: Iterator[String] = Source.fromFile(new File(filename)).getLines()
    val questions: Seq[ExamQuestion] = jsonLines.map {
      case line: String => {
        JacksonMapper.default.readValue(line, classOf[ExamQuestion])
      }
    }.toSeq

    ExamMultipleChoice(
      description = s"${filename} (${questions.size} questions)",
      questions
    )
  }

}
