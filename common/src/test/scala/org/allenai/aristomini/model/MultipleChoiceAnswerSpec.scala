package org.allenai.aristomini.model

import org.scalatest.FunSuite

class MultipleChoiceAnswerSpec extends FunSuite {

  test("unambiguous top choice selected") {
    val answer = MultipleChoiceAnswer(
      Seq(
        ChoiceConfidence(Choice(label="A",text="asdf"), confidence=1.0),
        ChoiceConfidence(Choice(label="B",text="asdf"), confidence=0.9),
        ChoiceConfidence(Choice(label="C",text="asdf"), confidence=0.8),
        ChoiceConfidence(Choice(label="D",text="asdf"), confidence=0.7)
      )
    )
    assert(answer.bestGuess ==
        Some(ChoiceConfidence(Choice(label="A",text="asdf"), confidence=1.0))
    )
  }

  test("unambiguous top choice outside tolerance selected") {
    val answer = MultipleChoiceAnswer(
      Seq(
        ChoiceConfidence(Choice(label="A",text="asdf"), confidence=0.90001),
        ChoiceConfidence(Choice(label="B",text="asdf"), confidence=0.9),
        ChoiceConfidence(Choice(label="C",text="asdf"), confidence=0.8),
        ChoiceConfidence(Choice(label="D",text="asdf"), confidence=0.7)
      )
    )
    assert(answer.bestGuess ==
      Some(ChoiceConfidence(Choice(label="A",text="asdf"), confidence=0.90001))
    )
  }

  test("ambiguous top choice within tolerance handled") {
    val answer = MultipleChoiceAnswer(
      Seq(
        ChoiceConfidence(Choice(label="A",text="asdf"), confidence=0.90000000000000001),
        ChoiceConfidence(Choice(label="B",text="asdf"), confidence=0.9),
        ChoiceConfidence(Choice(label="C",text="asdf"), confidence=0.8),
        ChoiceConfidence(Choice(label="D",text="asdf"), confidence=0.7)
      )
    )
    assert(answer.bestGuess == None)
  }
}

