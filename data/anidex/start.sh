#!/bin/bash

python3 manage.py makemigrations
python3 manage.py migrate
python3 manage.py extract_inpn
python3 manage.py populate_territories