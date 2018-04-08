import pandas as pd

from django.core.management.base import BaseCommand
from animals.models import *

class Command(BaseCommand):
    help = 'Populate groups'

    def handle(self, *args, **options):
        df = pd.read_csv('data/territoires_fr.csv')
        
        for index, row in df.iterrows():
            t, t_created = Territoire.objects.get_or_create(numéro=row.departmentCode,
                                                            nom=row.departmentName)