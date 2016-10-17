# Aristo mini

Aristo mini is a light-weight implementation of AI2's question answering system, [Aristo](http://allenai.org/aristo/).

Using Aristo mini you can quickly evaluate Aristo science questions with an evaluation web server and baseline solvers. You can also extend the provided solvers with your own implementation.

## Quick-start guide

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

## Component overview

Included are these components:

* **Simple solvers**: Simple example solvers with JSON APIs that can answer multiple choice questions.
* **Simple Evaluation system**: A web UI to a simple evaluation process that pairs questions with a solver to produce a score.
* **Question sets**: A subset of [Aristo's science questions](http://allenai.org/data.html) are included for convenience.

## Terminology

* **Raw question**
  A text representation of a full question with choices in parentheses. Example: `What is the color of the sky? (A) blue (B) green (C) red (D) black`
  
* **Question stem**
  The non-answer part of the question. Example: `What is the color of the sky?`
   
* **Answer key**
  The correct answer. Example: `A`
  
* **Choice**
  One of the possible answers, consisting of a **choice letter** and **choice text**. Example: `(A) blue`

## Solvers

### Available solvers

#### Random

This solver answers questions randomly. It illustrates the question-answer interface for a solver.

Once started (see above) you can go to [http://localhost:8000/solver-info](http://localhost:8000/solver-info) to confirm that it is running.

To answer a question you can POSTing to `/answer`. To try it on the command line:

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
   {"choiceConfidences":[{"choice":{"label":"A","text":"red"},"confidence":0.04460128978324118},{"choice":{"label":"B","text":"green"},"confidence":0.1826986186264289},{"choice":{"label":"C","text":"blue"},"confidence":0.013040391469338997}]}
   ```

#### Simple solver

See the documentation in [solver/textsearch/README.md](solvers/simple/src/main/scala/org/allenai/aristomini/solver/textsearch/README.md).

## The evaluation UI

Once started (see above) you can go to [http://localhost:9000/](http://localhost:9000/) and click around.

The UI is hard-coded to connect to a solver on `localhost:8000`. If you started a solver as above, it will be automatically used. You can restart solvers (on `localhost:8000`) while the evaluation UI remains running.
