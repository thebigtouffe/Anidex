import pandas as pd
import re

PREPROCESS = False

if PREPROCESS:
    df = pd.read_csv('INPN (France).txt', sep='\t')

    # Select animals with names
    df = df[df.REGNE=="Animalia"]
    df = df[df.NOM_VERN.str.len()>0]

    # Select entries with url
    df = df[df.URL.str.contains("http")==True]

    df.to_csv('inpn.csv')

from django.core.management.base import BaseCommand
from classification.models import *

class Command(BaseCommand):
    help = 'Populate classification'

    def handle(self, *args, **options):
        df = pd.read_csv('data/inpn.csv', sep=";")
        for index, row in df.iterrows():
            
            nom = re.sub("[\(].*?[\)] ", "", row.LB_NOM)
            print(nom)

            naturaliste = None
            date_découverte = None
            try:
                decouverte = re.findall("([A-Z][A-Za-z,&.\- ]*)(, ([0-9]{4}))", row.LB_AUTEUR)
                if decouverte:
                    nom_naturaliste = decouverte[0][0]
                    naturaliste, n_created = Naturaliste.objects.get_or_create(nom=nom_naturaliste)
                    date = decouverte[0][2]
            except:
                pass

            phylum, p_created = Phylum.objects.get_or_create(nom_latin=row.PHYLUM)
            classe, c_created = Classe.objects.get_or_create(nom_latin=row.CLASSE, phylum=phylum)
            ordre, o_created = Ordre.objects.get_or_create(nom_latin=row.ORDRE, classe=classe)
            famille, f_created = Famille.objects.get_or_create(nom_latin=row.FAMILLE, ordre=ordre)
            genre, g_created = Genre.objects.get_or_create(nom_latin=nom.split(' ')[0], famille=famille)
            
            try:
                espèce, e_created = Espèce.objects.get_or_create(nom_latin=' '.join(nom.split(' ')[1:]),
                                                                 genre=genre,
                                                                 naturaliste=naturaliste,
                                                                 date_découverte=date,
                                                                 inpn_id=row.CD_NOM)
            except:
                pass

