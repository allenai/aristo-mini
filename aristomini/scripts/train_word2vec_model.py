"""
a script to train a word2vec model
"""

import argparse

from aristomini.common.wordtwovec import tokenizer

from gensim.models import Word2Vec

parser = argparse.ArgumentParser(description="train a word2vec model")
parser.add_argument("sentences_filename",
                    metavar="sentences-filename",
                    help="file with the sentences to train on, one per line")
parser.add_argument("output_model",
                    metavar="output-model",
                    help="where to save the model file")
parser.add_argument("--size", type=int, default=50, help="dimension of the embedding")
parser.add_argument("--window", type=int, default=5, help="size of the word window")
parser.add_argument("--min-count", type=int, default=5,
                    help="only include words appearing at least this many times")


class TokenizingIterator:
    """a wrapper class for reading one line at a time from a huge file and tokenizing it"""
    def __init__(self, filename: str) -> None:
        self.filename = filename

    def __iter__(self):
        with open(self.filename, 'r') as f:
            for line in f:
                yield tokenizer(line)


if __name__ == "__main__":
    args = parser.parse_args()
    sentences = TokenizingIterator(args.sentences_filename)
    model = Word2Vec(sentences, min_count=args.min_count, window=args.window, size=args.size)
    model.save(args.output_model)
