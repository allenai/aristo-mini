"""server to run the evaluation ui"""

from typing import Any
import argparse
import pathlib
import os
import json
import jinja2

# built in `json` module doesn't serialize namedtuples correctly; `simplejson` does.
import simplejson as json
from flask import Flask, request, send_from_directory
import requests

from aristomini.common.models import Exam, MultipleChoiceQuestion

HOST = 'localhost'
SOLVER_PORT = 8000
EVALUI_PORT = 9000

SOLVER_URL = f"http://{HOST}:{SOLVER_PORT}"

EVALUI_DIR = pathlib.Path(__file__).resolve().parent
EXAM_DIR = EVALUI_DIR.parent.parent / 'questions'
EXAM_PATHS = [exam for exam in EXAM_DIR.glob('*')]
EXAM_NAMES = [path.name for path in EXAM_PATHS]

def read_exam(path: pathlib.Path) -> Exam:
    with open(path, 'r') as f:
        questions = [MultipleChoiceQuestion.from_jsonl(line) for line in f]
        name = path.name

    return Exam(name=name, questions=questions)

EXAMS = [read_exam(path) for path in EXAM_PATHS]

def get_solver_name(solver_url: str = SOLVER_URL) -> str:
    try:
        resp = requests.get(f"{SOLVER_URL}/solver-info")
        if resp.status_code == 200:
            solver_name = resp.content.decode('utf-8')
            return solver_name
        else:
            print(f"received status {resp.status_code} from solver at {solver_url}")
    except requests.exceptions.ConnectionError:
        print(f"ConnectionError: unable to connect to solver at {solver_url}")

    return None

app = Flask(__name__)

@app.route('/')
def index():  # pylint: disable=unused-variable
    with open(EVALUI_DIR / 'index.html', 'r') as f:
        raw_html = f.read()

    template = jinja2.Template(raw_html)

    return template.render(solver_url=SOLVER_URL,
                           exam_names=EXAM_NAMES)

@app.route('/exam/<index>')
def exam(index: str):  # pylint: disable=unused-variable
    solver_name = get_solver_name() or ''

    with open(EVALUI_DIR / 'exam.html', 'r') as f:
        raw_html = f.read()

    template = jinja2.Template(raw_html)

    exam = json.loads(json.dumps(EXAMS[int(index)]))
    return template.render(solver_name=solver_name,
                           solver_url=SOLVER_URL,
                           exam=exam)

@app.route('/static/<path:path>')
def send_static(path):
    return send_from_directory('static', path)

app.run(host=HOST, port=EVALUI_PORT)
