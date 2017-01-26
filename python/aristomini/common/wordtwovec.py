"""
a wrapper class for the gensim Word2Vec model that has extra features we need, as well as some
helper functions for tokenizing and stemming and things like that.
"""

from functools import lru_cache
import math
from typing import Iterable, List

from gensim.parsing.preprocessing import STOPWORDS
from gensim.parsing.porter import PorterStemmer
from gensim.models import Word2Vec
from gensim.utils import simple_preprocess

import numpy as np

stemmer = PorterStemmer()


@lru_cache(maxsize=1024)
def stem(word: str) -> str:
    """stemming words is not cheap, so use a cache decorator"""
    return stemmer.stem(word)


def tokenizer(sentence: str) -> List[str]:
    """use gensim's `simple_preprocess` and `STOPWORDS` list"""
    return [stem(token) for token in simple_preprocess(sentence) if token not in STOPWORDS]


def cosine_similarity(v1: np.ndarray, v2: np.ndarray) -> float:
    """https://en.wikipedia.org/wiki/Cosine_similarity"""
    num = np.dot(v1, v2)
    d1 = np.dot(v1, v1)
    d2 = np.dot(v2, v2)

    if d1 > 0.0 and d2 > 0.0:
        return num / math.sqrt(d1 * d2)
    else:
        return 0.0


class WordTwoVec:
    """a wrapper for gensim.Word2Vec with extra features we need"""
    def __init__(self, model_file: str) -> None:
        self.model = Word2Vec.load(model_file)

    def embed(self, words: Iterable[str]) -> np.ndarray:
        """given a list of words, find their vector embeddings and return the vector mean"""
        # first find the vector embedding for each word
        vectors = [self.model[word] for word in words if word in self.model]

        if vectors:
            # if there are vector embeddings, take the vector average
            return np.average(vectors, axis=0)
        else:
            # otherwise just return a zero vector
            return np.zeros(self.model.vector_size)

    def goodness(self, question_text: str, answer_text: str) -> float:
        """how good is the answer for this question?"""
        q_words = {word for word in tokenizer(question_text)}
        a_words = {word for word in tokenizer(answer_text) if word not in q_words}

        return cosine_similarity(self.embed(q_words), self.embed(a_words))
