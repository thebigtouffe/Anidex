import pandas as pd
import re

from django.core.management.base import BaseCommand
from animals.models import *

class Command(BaseCommand):
    help = 'Populate groups'

    def handle(self, *args, **options):
        df = pd.read_csv('data/iucn.csv')
        
        for e in Entrée.objects.all():
            if e.catégorie == "E" and not e.conservation:
                print(e)
                try:
                    status = df[(df.Genus==e.genre.nom_latin) & (df.Species==e.espèce.nom_latin)]['Red List status'].iloc[0]
                    e.conservation = status
                except:
                    e.conservation = 'NE'
                e.save()
