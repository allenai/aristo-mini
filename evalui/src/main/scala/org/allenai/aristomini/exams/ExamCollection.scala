package org.allenai.aristomini.exams

import org.allenai.aristomini.model.ExamMultipleChoice

import java.nio.file.{ Files, Paths }
import scala.collection.JavaConverters._

/** A collection of exams. */
object ExamCollection {

  val examFilenames = Files.newDirectoryStream(Paths.get("data/questions"), "*.jsonl").asScala

  /** Exams are encoded as a map from integer ("exam identifier") to ExamMultipleChoice */
  val exams: Map[Int, ExamMultipleChoice] = examFilenames.map(
    filename => ExamMultipleChoice.fromAI2JSONLFile(filename.toString)
  ).zipWithIndex.map {
    case (exam, index) => index -> exam
  }.toMap

}
