"""text search solver"""

from elasticsearch import Elasticsearch
from elasticsearch_dsl import Q, Search

from aristomini.common.solver import SolverBase
from aristomini.common.models import MultipleChoiceQuestion, MultipleChoiceAnswer, ChoiceConfidence


class TextSearchSolver(SolverBase):
    """guesses at random"""
    def __init__(self,                   # pylint: disable=too-many-arguments
                 host: str="localhost",
                 port: int=9200,
                 index_name: str="knowledge",
                 field_name: str="body",
                 topn: int=1) -> None:
        self.client = Elasticsearch([host], port=port)
        print(self.client)
        self.fields = [field_name]
        self.index_name = index_name
        self.topn = topn

    def score(self, question_stem: str, choice_text: str) -> float:
        """get the score from elasticsearch"""
        query_text = "{0} {1}".format(question_stem, choice_text)
        query = Q('multi_match', query=query_text, fields=self.fields)
        search = Search(using=self.client, index=self.index_name).query(query)[:self.topn]
        response = search.execute()
        return sum(hit.meta.score for hit in response)

    def solver_info(self) -> str:
        return "text_search"

    def answer_question(self, question: MultipleChoiceQuestion) -> MultipleChoiceAnswer:
        """answer the question"""
        return MultipleChoiceAnswer(
            [ChoiceConfidence(choice, self.score(question.stem, choice.text))
             for choice in question.choices]
        )

if __name__ == "__main__":
    solver = TextSearchSolver()  # pylint: disable=invalid-name
    solver.run()
