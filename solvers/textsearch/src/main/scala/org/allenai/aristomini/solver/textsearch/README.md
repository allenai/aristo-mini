# Text Search Solver

This solver uses an Elasticsearch index to score the choices in multiple choice questions.

## How it works

When given a question like this:

```
{
  "stem" : "What color is the sky?",
  "choices" : {
     "A" : "red",
     "B" : "green",
     "C" : "blue"
  }
}
```

This solver will produce three queries to the Elasticsearch index:

* `What color is the sky? red`
* `What color is the sky? green`
* `What color is the sky? blue`

The Elasticsearch **score** of the first document returned is used as a confidence for each choice. For example:

* `What color is the sky? red` **Score: 0.3**
* `What color is the sky? green`  **Score: 0.1**
* `What color is the sky? blue` **Score: 0.6**

These numbers are reported directly in the response: `{"choiceConfidence":{"A":0.3,"B":0.1,"C":0.6}}`

## Starting the Text Search Solver

To use the Text Search Solver, you need to populate a local Elasticsearch index with sentences.

### Set up a local Elasticsearch server

Download ElasticSearch and run the server locally with out-of-the-box defaults.

For instructions, see the [Elasticsearsch website](https://www.elastic.co/downloads/elasticsearch).

### Populate your Elasticsearch server with interesting sentences

1. Download the raw text version of the book [All About Animals](https://archive.org/details/allaboutanimalsf00newy) into a file `allaboutanimals_raw.txt`.

2. Parse the raw text into sentences, one per line, without numbers or punctuation:
   ```
   cat allaboutanimals_raw.txt | tr -cd 'a-zA-Z. ' | tr '.' '\n' > allaboutanimals_sentences.txt
   ```

3. Produce bulk `index` commands in JSONL format for Elasticsearch:
   ```
   cat allaboutanimals_sentences.txt | awk '{print "{\"index\":{\"_id\":\"" NR "\"}}"; print "{\"body\":\"" $0 "\"}"}' > allaboutanimals_bulk.jsonl
   ```

4. Issue the generated `index` commands to your previously started Elasticsearch server, in the index name `knowledge`:
   ```
   curl -s -XPOST localhost:9200/knowledge/sentence/_bulk --data-binary "@allaboutanimals_bulk.jsonl"; echo
   ```

### Start the solver server

```bash
sbt stage
cd solvers/textsearch/target/universal/stage
bin/solver-textsearch
```

The solver will query the locally running Elasticsearch index.
