"""skeleton for building your own solver"""
from typing import List

from aristomini.common.solver import SolverBase
from aristomini.common.models import MultipleChoiceQuestion, MultipleChoiceAnswer, ChoiceConfidence

MY_SOLVER_NAME = "my solver"

class MySolver(SolverBase):
    def solver_info(self) -> str:
        return MY_SOLVER_NAME

    def answer_question(self, question: MultipleChoiceQuestion) -> MultipleChoiceAnswer:
        # pylint: disable=unused-variable
        stem = question.stem
        choices = question.choices

        confidences: List[float] = []

        for choice in question.choices:
            label = choice.label
            text = choice.text

            # TODO: compute confidence using stem, label, text?
            confidence = 0
            confidences.append(confidence)

        return MultipleChoiceAnswer(
            [ChoiceConfidence(choice, confidence)
             for choice, confidence in zip(choices, confidences)]
        )

if __name__ == "__main__":
    solver = MySolver()  # pylint: disable=invalid-name
    solver.run()
