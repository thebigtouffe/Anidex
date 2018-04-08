import pandas as pd
import re

from django.core.management.base import BaseCommand
from animals.models import *

class Command(BaseCommand):
    help = 'Generate ids'

    def handle(self, *args, **options):
        id = 1
        for e in Entrée.objects.all():
            e.numéro = id
            id += 1
            e.save()
