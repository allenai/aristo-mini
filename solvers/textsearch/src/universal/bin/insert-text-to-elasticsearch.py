#!/usr/bin/env python

import json
import re
import sys
import urllib

# Reads text input on STDIN, splits it into sentences, gathers groups of
# sentences and issues bulk insert commands to an Elasticsearch server running
# on localhost.

ELASTIC_SEARCH_URL = 'http://localhost:9200/knowledge/sentence/_bulk'
DOCUMENTS_PER_POST = 100000

def sentences_to_elasticsearch_payload(sentences):
  payload_lines = []
  for sentence in sentences:
    payload_lines += [ json.dumps({"index":{}}) ]
    payload_lines += [ json.dumps({"body":sentence}) ]
  return "\n".join(payload_lines)

def bulk_load_elasticsearch(sentences, url):
  payload = sentences_to_elasticsearch_payload(sentences)
  response_file = urllib.urlopen(url, payload)
  response = json.loads(response_file.read())
  print "Posted %d documents (%d bytes) to %s. Elasticsearch errors = %s" % (
    len(sentences),
    len(payload),
    ELASTIC_SEARCH_URL,
    str(response.get("errors", "?"))
  )

def lines_to_sentences(line_stream):
  for line in line_stream:
    line_cleaned = re.sub(r'([^a-zA-Z0-9\.])', " ", line).strip()
    for sentence in line_cleaned.split("."):
      if len(sentence) == 0:
        continue
      yield sentence

def groups(stream, size):
  batch = []
  for item in stream:
    batch += [item]
    if len(batch) % size == 0:
      yield batch
      batch = []
  if len(batch) > 0:
    yield batch

def main():
  sentence_count = 0

  for sentences in groups(lines_to_sentences(sys.stdin), DOCUMENTS_PER_POST):
    bulk_load_elasticsearch(sentences, ELASTIC_SEARCH_URL)
    sentence_count += len(sentences)

  print "Documents posted:", sentence_count

if __name__ == "__main__":
  main()
