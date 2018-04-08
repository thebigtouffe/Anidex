import pandas as pd
import re

from django.core.management.base import BaseCommand
from animals.models import *

class Command(BaseCommand):
    help = 'Populate groups'

    def handle(self, *args, **options):
        df = pd.read_csv('data/inpn.csv', sep=";")
        
        for e in Entrée.objects.all():
            print(e)

            if e.catégorie == "E" or e.catégorie == "G":
                point = df[df.LB_NOM == e.nom_latin].iloc[0]
            elif e.catégorie == "F":
                point = df[df.FAMILLE == e.famille.nom_latin].iloc[0]
            elif e.catégorie == "O":
                point = df[df.ORDRE == e.ordre.nom_latin].iloc[0]
            groupe1_nom = point.GROUP1_INPN
            groupe2_nom = point.GROUP2_INPN

            groupe1, g1_created = Groupe1.objects.get_or_create(nom=groupe1_nom)
            groupe2, g2_created = Groupe2.objects.get_or_create(nom=groupe2_nom)

            e.groupe1 = groupe1
            e.groupe2 = groupe2
            e.save()
