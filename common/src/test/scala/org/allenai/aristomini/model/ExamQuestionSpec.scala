package org.allenai.aristomini.model

import org.scalatest.FunSuite

class ExamQuestionSpec  extends FunSuite {
  val dummyExamQuestion = ExamQuestion(
    question = MultipleChoiceQuestion(stem = "What is?", choices = Seq()),
    answerKey = "A"
  )

  test("unambiguous candidate answer has correct answer") {

    val candidateAnswer = MultipleChoiceAnswer(
      Seq(
        ChoiceConfidence(Choice(label="A",text="asdf"), confidence=1.0),
        ChoiceConfidence(Choice(label="B",text="asdf"), confidence=0.9),
        ChoiceConfidence(Choice(label="C",text="asdf"), confidence=0.8),
        ChoiceConfidence(Choice(label="D",text="asdf"), confidence=0.7)
      )
    )
    assert(dummyExamQuestion.candidateAnswerIsCorrect(candidateAnswer) == (Some("A"), true))
  }

  test("unambiguous candidate answer has incorrect answer") {
    val candidateAnswer = MultipleChoiceAnswer(
      Seq(
        ChoiceConfidence(Choice(label="A",text="asdf"), confidence=0.9),
        ChoiceConfidence(Choice(label="B",text="asdf"), confidence=1.0),
        ChoiceConfidence(Choice(label="C",text="asdf"), confidence=0.8),
        ChoiceConfidence(Choice(label="D",text="asdf"), confidence=0.7)
      )
    )
    assert(dummyExamQuestion.candidateAnswerIsCorrect(candidateAnswer) == (Some("B"), false))
  }

  test("ambiguous candidate answer is incorrect") {
    val candidateAnswer = MultipleChoiceAnswer(
      Seq(
        ChoiceConfidence(Choice(label="A",text="asdf"), confidence=1.0),
        ChoiceConfidence(Choice(label="B",text="asdf"), confidence=1.0),
        ChoiceConfidence(Choice(label="C",text="asdf"), confidence=0.8),
        ChoiceConfidence(Choice(label="D",text="asdf"), confidence=0.7)
      )
    )
    assert(dummyExamQuestion.candidateAnswerIsCorrect(candidateAnswer) == (None, false))
  }
}
