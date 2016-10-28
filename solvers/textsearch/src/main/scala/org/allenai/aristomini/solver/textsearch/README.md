# Text Search Solver

This solver uses an Elasticsearch index to score the choices in multiple choice questions.

## How it works

When given a question like this:

```json
{
  "stem" : "What color is the sky?",
  "choices" : [
     { "label" : "A", "text" : "red" },
     { "label" : "B", "text" : "green" },
     { "label" : "C", "text" : "blue" }
  ]
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

### Start a local Elasticsearch server

Download [Elasticsearch 2.4.1](https://www.elastic.co/downloads/past-releases/elasticsearch-2-4-1) and run the server locally with out-of-the-box defaults.

At time of writing, this can be done as follows:

```
wget https://download.elastic.co/elasticsearch/release/org/elasticsearch/distribution/zip/elasticsearch/2.4.1/elasticsearch-2.4.1.zip
unzip elasticsearch-2.4.1.zip
cd elasticsearch-2.4.1
bin/elasticsearch
```

### Configure your local Elasticsearch server

The default settings for Elasticsearch are meant for production use, involving replication and sharding. In the case of this simple solver, we'll be running it on a single machine, so the default number of replicas and shards have to be adjusted.

In addition, we want to explicitly declare that the index `knowledge` will contain documents with property `body` that should be indexed using the [Snowball analyzer](https://www.elastic.co/guide/en/elasticsearch/reference/2.4/analysis-snowball-tokenfilter.html) for word stemming.

To configure your running Elasticsearch server with these settings, issue this command:

```
curl -XPUT 'http://localhost:9200/knowledge' -d '
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "sentence": {
      "dynamic": "false",
      "properties": {
        "body": {
          "analyzer": "snowball",
          "type": "string"
        }
      }
    }
  }
}'
```

### Populate your Elasticsearch server with interesting sentences

This solver includes a Python 2.x script that will insert sentences
from text files into a locally running Elasticsearch solver. To use it:

1. Obtain a text file. For example, the plain text version of the book [All
About Animals](https://openlibrary.org/books/OL25099049M/All_about_animals) is
freely available. Save it into a file like `/tmp/allaboutanimals.txt`.

2. Use the `insert-text-to-elasticsearch.py` script to operate on `/tmp/allaboutanimals.txt`:
   ```bash
   sbt stage
   cd solvers/textsearch/target/universal/stage
   cat /tmp/allaboutanimals.txt | bin/insert-text-to-elasticsearch.py
   ```
   
3. Watch progress and wait for the insertion program to conclude:
   ```
   Posted 7182 documents (570154 bytes) to http://localhost:9200/knowledge/sentence/_bulk. Elasticsearch errors = False
   Documents posted: 7182
   ```

### Start the solver server

```bash
sbt stage
cd solvers/textsearch/target/universal/stage
bin/solver-textsearch
```

The solver will query the locally running Elasticsearch index.
