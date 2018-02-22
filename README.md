Aristo mini
===========

   * [Overview](#overview)
   * [Quick-start guide](#quick-start-guide)
   * [Component overview](#component-overview)
   * [Terminology](#terminology)
   * [Solvers](#solvers)
      * [Available solvers](#available-solvers)
         * [Random solver in Scala](#random-solver-in-scala)
         * [Text search solver in Python](#text-search-solver-in-python)
      * [Writing your own solver](#writing-your-own-solver)
         * [Writing a solver in Scala](#writing-a-solver-in-scala)
         * [Writing a solver in Python](#writing-a-solver-in-python)
   * [The evaluation UI](#the-evaluation-ui)
   * [Question sets](#question-sets)
   * [Feedback](#feedback)
   * [History](#history)

# Overview

Aristo mini is a light-weight question answering system that can quickly evaluate [Aristo](http://allenai.org/aristo/) science questions with an evaluation web server and the provided baseline solvers. You can also extend the provided solvers with your own implementation to try out new approaches and compare results.

# Quick-start guide

To experiment you'll need `scala 2.11` and `sbt` installed. Then follow these steps:

1. Clone this repo and run `sbt stage`:
   ```bash
   git clone git@github.com:allenai/aristo-mini.git
   cd aristo-mini
   sbt stage
   ```

2. Run the random solver in one terminal window:
   ```bash
   cd solvers/random/target/universal/stage
   bin/solver-random
   ```

3. Run the evaluation web UI in another terminal window:
   ```bash
   cd evalui/target/universal/stage
   bin/evalui
   ```

4. Try the UI in your browser at [http://localhost:9000/](http://localhost:9000/)

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
  
These are represented with code in [the model/ directory](common/src/main/scala/org/allenai/aristomini/model/).

# Solvers

## Available solvers

Several solvers are included in this distribution of Aristo mini. You can run one solver at a time for the Evaluation UI to use.

### Random solver (in Scala)

This solver answers questions randomly. It illustrates the question-answer interface for a solver.

Once started (see above) you can go to [http://localhost:8000/solver-info](http://localhost:8000/solver-info) to confirm that it is running.

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
### Text search solver (in Scala)

See [solver/textsearch/README.md](solvers/textsearch/src/main/scala/org/allenai/aristomini/solver/textsearch/README.md) for setup and running instructions.

### Random solver (in Python)

To run the random solver written in Python:

1. Install the requirements:
```bash
pip install -r requirements.txt
```

2. Add the project to your PYTHONPATH
```
export PYTHONPATH=${PYTHONPATH}:`pwd`/python
```

3. Start the solver with
```bash
python python/aristomini/solvers/randomguesser.py
```

### Text search solver (in Python)

Follow the above steps to prepare your environment to run the random solver above, then start the Text Search Solver like this:

```bash
python python/aristomini/solvers/textsearch.py
```


### Word vector similarity solver (in Python)

Use the `python/aristomini/scripts/train_word2vec_model.py` script to train a Word2Vec model
from a text file of sentences (one per line). For instance, you could use the same sentences
as the [text search solver](solvers/textsearch/src/main/scala/org/allenai/aristomini/solver/textsearch/README.md)
 
Then start the solver with the path to the word2vec model:

```
python python/aristomini/solvers/wordvectorsimilarity.py /path/to/word2vec/model
```



## Writing your own solver

Your solver has to be an HTTP server that responds to the `GET /solver-info` and `POST /answer` APIs. The `POST /answer` API has to consume a JSON-formatted question document and must produce a JSON-formatted response document with the answer. You can start reading at [SolverBase.scala](common/src/main/scala/org/allenai/aristomini/solver/SolverBase.scala) (which is extended by the provided solvers) to understand the input and output document structures.

**Network location:** By default, the evaluation UI will use a solver running on `localhost:8000`. This is defined in [Evaluator.scala](evalui/src/main/scala/org/allenai/aristomini/evaluate/Evaluator.scala) which you can change to another location if your solver runs on a different host or on a different port.

**Concurrency:** Solvers will be sent a fixed number of questions at time. At the time of writing, this is 10 concurrent requests. This is configured with the thread pool size in [Evaluation.scala](evalui/src/main/scala/org/allenai/aristomini/evaluate/Evaluation.scala).

Since a solver is just a HTTP server, you can write it in any language you like. For example, you might want to use scikit-learn or keras in your solver, in which case it would make sense to write it in Python.

### Writing a solver in Scala


The easiest way to make a new solver in Scala is to copy the Random solver by copying it to a new directory and renaming the Scala classes and packages.

### Writing a solver in Python

The directory `python/aristomini/solvers` contains Python implementations of the Random and TextSearch solvers. These solvers are written using Python's [type hinting](https://docs.python.org/3/library/typing.html) features, which means that you need Python 3.5 or later to run them. (This made them easier to write correctly but is also the author's way of encouraging you to upgrade to 3.5 if you haven't already.)

To implement your own solver, simply inherit from `SolverBase`, override the `solver_info` and `answer_question` methods, and call `.run()`.

# The evaluation UI

Once started (see above) you can go to [http://localhost:9000/](http://localhost:9000/) and click around.

The UI is hard-coded to connect to a solver on `localhost:8000`. If you started a solver as above, it will be automatically used. You can restart solvers (on `localhost:8000`) while the evaluation UI remains running.

# Question sets

Several question sets are provided in the [evalui/src/universal/data/questions/](evalui/src/universal/data/questions/) directory. When the project is built with `sbt stage`, they are copied to the the `data/questions` directory.

These question sets are written in the [JSONL](http://jsonlines.org/) format, each line corresponding to an instance of [ExamQuestion](common/src/main/scala/org/allenai/aristomini/model/ExamQuestion.scala).

To try other question sets in this format, add them to the above `data/questions` directory and restart the evaluation UI.

AI2 provides more questions at http://allenai.org/data.html

# Feedback

Please tell us what you think!

* If you have a question or suggestion for a change,
take look at [existing issues](https://github.com/allenai/aristo-mini/issues) or [file a new issue](https://github.com/allenai/aristo-mini/issues/new?labels=question).

* If you'd like to propose a change to this code, please submit a pull request.

# History

* November, 2016: Initial public release, version 1.
