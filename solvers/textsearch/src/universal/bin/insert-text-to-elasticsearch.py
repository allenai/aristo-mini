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

def make_payload_from_sentences(sentences):
  payload_lines = []
  for sentence in sentences:
    payload_lines += [ json.dumps({"index":{}}) ]
    payload_lines += [ json.dumps({"body":sentence}) ]
  return "\n".join(payload_lines)

def bulk_load_elasticsearch(sentences):
  payload = make_payload_from_sentences(sentences)
  response_file = urllib.urlopen(ELASTIC_SEARCH_URL, payload)
  response = json.loads(response_file.read())
  print "Posted %d documents (%d bytes) to %s. Elasticsearch errors = %s" % (
    len(sentences),
    len(payload),
    ELASTIC_SEARCH_URL,
    str(response.get("errors", "?"))
  )

def main():
    line_count = 0
    sentence_count = 0
    sentences = []
    for line in sys.stdin:
      line_count += 1
      line_cleaned = re.sub(r'([^a-zA-Z0-9\.])', " ", line).strip()
      for sentence in line_cleaned.split("."):
        if len(sentence) == 0:
          continue
        sentence_count += 1
        sentences += [sentence]
        if len(sentences) % DOCUMENTS_PER_POST == 0:
          bulk_load_elasticsearch(sentences)
          sentences = []
    
    bulk_load_elasticsearch(sentences)
    print "Lines read: ", line_count
    print "Sentences posted: ", sentence_count

if __name__ == "__main__":
    main()
