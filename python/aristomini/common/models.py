"""
typed models for our data. these are the exact analogues of the case classes used by the scala
code (which is why the fields have unfortunate, non-pythonic names)
"""
from typing import NamedTuple, Sequence, Any, Dict

# pylint: disable=invalid-name

Choice = NamedTuple("Choice", [("label", str), ("text", str)])


ChoiceConfidence = NamedTuple("ChoiceConfidence",
                              [("choice", Choice), ("confidence", float)])

MultipleChoiceAnswer = NamedTuple("MultipleChoiceAnswer",
                                  [("choiceConfidences", Sequence[ChoiceConfidence])])

SolverAnswer = NamedTuple("SolverAnswer",
                          [("solverInfo", str),
                           ("multipleChoiceAnswer", MultipleChoiceAnswer)])

MultipleChoiceQuestion = NamedTuple("MultipleChoiceQuestion",
                                    [("stem", str), ("choices", Sequence[Choice])])


def parse_question(blob: Dict[str, Any]) -> MultipleChoiceQuestion:
    """parses a question from a json blob. is possibly too lenient to malformed json"""
    return MultipleChoiceQuestion(
        stem=blob.get("stem", ""),
        choices=[Choice(c["label"], c["text"]) for c in blob.get("choices", [])]
    )
