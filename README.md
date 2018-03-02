Aristo mini
===========

   * [Overview](#overview)
   * [Quick-start guide](#quick-start-guide)
   * [Component overview](#component-overview)
   * [Terminology](#terminology)
   * [Solvers](#solvers)
   * [The evaluation UI](#the-evaluation-ui)
   * [Question sets](#question-sets)
   * [Feedback](#feedback)
   * [History](#history)

# Overview

Aristo mini is a light-weight question answering system that can quickly evaluate [Aristo](http://allenai.org/aristo/) science questions with an evaluation web server and the provided baseline solvers. You can also extend the provided solvers with your own implementations to try out new approaches and compare results.

# Quick-start guide

To experiment you'll need `python 3.6`. We recommend you create
a dedicated virtual environment for `aristo-mini` and its dependencies.
Then follow these steps.

1. Clone this repo:
   ```bash
   git clone git@github.com:allenai/aristo-mini.git
   cd aristo-mini
   ```

2. Install the requirements:
   ```bash
   cd aristo-mini
   pip install -r requirements.txt
   ```

3. Add the project to your PYTHONPATH
    ```
    export PYTHONPATH=${PYTHONPATH}:.
    ```

4. Run the random solver in one terminal window:
   ```bash
   python aristomini/solvers/randomguesser.py
   ```

5. Run the evaluation web UI in another terminal window:
   ```bash
   python aristomini/evalui/evalui.py
   ```

6. Try the UI in your browser at [http://localhost:9000/](http://localhost:9000/)

# Component overview

Included are these components:

* **Simple solvers**: Simple example solvers with JSON APIs that can answer multiple choice questions.
* **Simple Evaluation system**: A web UI to a simple evaluation process that pairs questions with a solver to produce a score.
* **Question sets**: A subset of [Aristo's science questions](http://allenai.org/data.html) are included for convenience.

# Terminology

Consider a question that might be represented on an exam like this:

```
What is the color of the sky?

(A) blue
(B) green
(C) red
(D) black
```

Parts of this question are named like this:

* **Question stem**: The non-choices part of the question. Example: `What is the color of the sky?`

* **Answer key**: The correct answer's choice label. Example: `A`

* **Choice**: One of the possible answers, consisting of a **choice label** (e.g., `A`) and **choice text** (e.g., `blue`).

These are modeled as `NamedTuple`s in
[aristomini/common/models.py](aristomini/common/models.py).

# Solvers

## Available solvers

Several solvers are included in this distribution of Aristo mini. You can run one solver at a time for the Evaluation UI to use.

### Random solver

This solver answers questions randomly. It illustrates the question-answer interface for a solver.

As above, you can start it with

```bash
python aristomini/solvers/randomguesser.py
```

Then you can go to [http://localhost:8000/solver-info](http://localhost:8000/solver-info) to confirm that it is running.

To answer a question you can POST to `/answer`. To try it on the command line:

1. Make a JSON file with the question, structured like this:
   ```json
   % cat question.json
   {
      "stem" : "What color is the sky?",
      "choices" : [
         { "label" : "A", "text" : "red" },
         { "label" : "B", "text" : "green" },
         { "label" : "C", "text" : "blue" }
      ]
   }
   ```

2. Submit the request with `curl`:
   ```bash
   % curl -H "Content-Type: application/json" --data @question.json http://localhost:8000/answer
   ```

3. Look at the response:
   ```json
   {
      "multipleChoiceAnswer" : {
         "choiceConfidences" : [
            {
               "choice" : { "text" : "red", "label" : "A" },
               "confidence" : 0.398084282084622
            },
            {
               "choice" : { "text" : "green", "label" : "B" },
               "confidence" : 0.984916549460303
            },
            {
               "confidence" : 0.13567292440745,
               "choice" : { "text" : "blue", "label" : "C" }
            }
         ]
      },
      "solverInfo" : "RandomGuesser"
   }
   ```

### Text search solver

See [aristomini/solvers/textsearch.md](aristomini/solvers/textsearch.md) for setup and running instructions.

### Word vector similarity solver (in Python)

Use the `scripts/train_word2vec_model.py` script to train a Word2Vec model
from a text file of sentences (one per line). For instance, you could use the same sentences
as the [text search solver](aristomini/solvers/textsearch.md)

Then start the solver with the path to the word2vec model:

```
python python/aristomini/solvers/wordvectorsimilarity.py /path/to/word2vec/model
```

## Writing your own solver

### The Easy Way

Modify [aristomini/solvers/mysolver.py](aristomini/solvers/mysolver.py). It has two TODOs for the parts you need to update.

### The Hard Way

Your solver has to be an HTTP server that responds to the `GET /solver-info` and `POST /answer` APIs. The `POST /answer` API has to consume a JSON-formatted question document and must produce a JSON-formatted response document with the answer. You can start reading at [aristomini/common/solver.py](aristomini/common/solver.py) (which is extended by the provided solvers) to understand the input and output document structures.

Since a solver is just a HTTP server, you can write it in any language you like. You should follow the existing solvers for the input and output JSON formats. 

# The evaluation UI

Once started (see above) you can go to [http://localhost:9000/](http://localhost:9000/) and click around.

The UI is hard-coded to connect to a solver on `localhost:8000`. If you started a solver as above, it will be automatically used. You can restart solvers (on `localhost:8000`) while the evaluation UI remains running.

# Question sets

Several question sets are provided in the [questions/](questions/) directory.

These question sets are written in the [JSONL](http://jsonlines.org/) format, each line corresponding to an instance of [MultipleChoiceQuestion](aristomini/common/models.py).

To try other question sets in this format, add them to the above `questions` directory and restart the evaluation UI.

AI2 provides more questions at http://allenai.org/data.html

# Feedback

Please tell us what you think!

* If you have a question or suggestion for a change,
take look at [existing issues](https://github.com/allenai/aristo-mini/issues) or [file a new issue](https://github.com/allenai/aristo-mini/issues/new?labels=question).

* If you'd like to propose a change to this code, please submit a pull request.

# History

* November, 2016: Initial public release, version 1.
* February, 2018: Delete all Scala code.
* March, 2018: Update README.
