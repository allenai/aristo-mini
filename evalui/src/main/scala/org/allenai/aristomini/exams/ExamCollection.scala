package org.allenai.aristomini.exams

import org.allenai.aristomini.model.ExamMultipleChoice

/** A collection of exams. */
object ExamCollection {

  /** Exams are encoded as a map from an integer ("exam identifier") to an ExamNDMC instance */
  val exams: Map[Int, ExamMultipleChoice] = Seq(
    ExamMultipleChoice.fromAI2JSONLFile("data/questions/AI2-Elementary-NDMC-Feb2016-Dev.jsonl"),
    ExamMultipleChoice.fromAI2JSONLFile("data/questions/AI2-Elementary-NDMC-Feb2016-Train.jsonl"),
    ExamMultipleChoice.fromAI2JSONLFile("data/questions/AI2-8thGr-NDMC-Feb2016-Dev.jsonl"),
    ExamMultipleChoice.fromAI2JSONLFile("data/questions/AI2-8thGr-NDMC-Feb2016-Train.jsonl")
  ).zipWithIndex.map {
    case (exam, index) => index -> exam
  }.toMap

}
