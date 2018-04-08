import pandas as pd
import re

from django.core.management.base import BaseCommand
from animals.models import *

class Command(BaseCommand):
    help = 'Populate location'

    def handle(self, *args, **options):
        df = pd.read_csv('data/inpn.csv', sep=";")

        territoires = {'GF' : '973', 'MAR' : '972', 'GUA' : '971', 'SM': '978', 'SB': '977', 'SPM': '975', 'MAY': '976', 'REU': '974', 'TAAF': '984', 'PF': '987', 'NC': '988', 'WF': '986'}
        
        for e in Entrée.objects.all():
            if e.catégorie == "E":
                for k in list(territoires.keys()):
                    presence = df[df.LB_NOM == e.nom_latin][k].iloc[0]
                    #print(e, k, presence)
                    if presence == "P" or presence == "E" or presence == "S" or presence == "I"  or presence == "C"  or presence == "J" or presence == "M" or presence == "B":
                        e.localisation.add(Territoire.objects.get(numéro=territoires[k]))
            e.save()
