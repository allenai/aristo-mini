"""
nlp utils
"""
from functools import lru_cache
import re
from typing import List, NamedTuple, Iterable

from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.stem.snowball import SnowballStemmer

STOPWORDS = set(stopwords.words('english'))
_stemmer = SnowballStemmer('english')

def get_sentences(filename: str) -> List[str]:
    """get sentences"""
    with open(filename) as f:
        return [line.strip() for line in f]

@lru_cache(maxsize=4096)
def stemmer(word: str) -> str:
    """memoized wrapper around PorterStemmer"""
    return _stemmer.stem(word)


NGram = NamedTuple("NGram", [("gram", str), ("position", int)])
Token = NamedTuple("Token",
                   [("word", str),
                    ("position", int),
                    ("is_stopword", bool)])


def tokenize(sentence: str, stem: bool=True) -> List[Token]:
    """
    lowercase a sentence, split it into tokens, label the stopwords, and throw out words that
    don't contain alphabetic characters
    """
    pre_tokens = [Token(stemmer(w) if stem else w, i, w in STOPWORDS)
                  for i, w in enumerate(word_tokenize(sentence.lower()))]

    return [token for token in pre_tokens if re.match(r"^[a-z]+$", token.word)]


def ngrams(n: int, tokens: List[Token], skip: bool=False) -> List[NGram]:
    """generate all the ngrams of size n. do not allow ngrams that contain stopwords, except that a
        3-gram may contain a stopword as its middle word"""

    def stopwords_filter(subtokens: List[Token]) -> bool:
        """a filter"""
        if n == 3:
            return not subtokens[0].is_stopword and not subtokens[2].is_stopword
        else:
            return all(not token.is_stopword for token in subtokens)

    def make_gram(subtokens: List[Token]) -> NGram:
        """make a gram using the position of the leftmost work and skipping the middle maybe"""
        words = [token.word if not skip or i == 0 or i == len(subtokens) - 1 else "_"
                 for i, token in enumerate(subtokens)]
        return NGram(" ".join(words), subtokens[0].position)

    # if n is 1, we want len(tokens), etc..
    slices = [tokens[i:(i+n)] for i in range(len(tokens) - n + 1)]

    return [make_gram(slic) for slic in slices if stopwords_filter(slic)]


def distinct_grams(grams: List[NGram]) -> List[str]:
    """return the distinct grams from a bunch of ngrams"""
    return list({gram.gram for gram in grams})


def all_grams_from_tokens(tokens: List[Token]) -> List[NGram]:
    """make all the 1, 2, 3, and skip-3 grams from some tokens"""
    return (ngrams(1, tokens) +
            ngrams(2, tokens) +
            ngrams(3, tokens) +
            ngrams(3, tokens, skip=True))


def all_grams(sentence: str, stem: bool=True) -> List[NGram]:
    """tokenize the sentence and make all the grams"""
    return all_grams_from_tokens(tokenize(sentence, stem))
