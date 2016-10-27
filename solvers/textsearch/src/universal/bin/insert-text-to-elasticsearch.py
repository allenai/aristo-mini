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

def payload_from_documents(post_documents):
  payload_lines = []
  for (document_id, sentence) in post_documents:
    payload_lines += [ json.dumps({"index":{"_id":str(document_id)}}) ]
    payload_lines += [ json.dumps({"body":sentence}) ]
  return "\n".join(payload_lines)

def bulk_load_elasticsearch(post_documents):
  payload = payload_from_documents(post_documents)
  f = urllib.urlopen(ELASTIC_SEARCH_URL, payload)
  response = json.loads(f.read())
  print "Posted %d documents (%d bytes) to %s. Elasticsearch errors = %s" % (
    len(post_documents),
    len(payload),
    ELASTIC_SEARCH_URL,
    str(response.get("errors", "?"))
  )

def main():
    document_id = 1
    post_documents = []
    for line in sys.stdin:
      line_cleaned = re.sub(r'([^a-zA-Z0-9\.])', " ", line).strip()
      for sentence in line_cleaned.split("."):
        if len(sentence) == 0:
          continue
        post_documents += [(document_id, sentence)]
        document_id += 1
        if len(post_documents) % DOCUMENTS_PER_POST == 0:
          bulk_load_elasticsearch(post_documents)
          post_documents = []
    
    bulk_load_elasticsearch(post_documents)

if __name__ == "__main__":
    main()
