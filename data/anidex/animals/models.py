from django.db import models
from classification.models import Ordre, Famille, Genre, Espèce

import base64
import requests
import unidecode
import urllib

from .eol import API as EOL

from django.db.models.signals import post_save, m2m_changed
from django.dispatch import receiver
from django.db import transaction

def removeAccent(text):
    unidecode.unidecode(text)


class Source(models.Model):
    nom = models.CharField(max_length=200)
    url = models.URLField(null=True, blank=True)
    def __str__(self):
        return str(self.nom)


class Habitat(models.Model):
    nom = models.CharField(max_length=200)
    def __str__(self):
        return str(self.nom)


class Biome(models.Model):
    nom = models.CharField(max_length=200)
    def __str__(self):
        return str(self.nom)


class Groupe1(models.Model):
    nom = models.CharField(max_length=200)
    def __str__(self):
        return str(self.nom)


class Groupe2(models.Model):
    nom = models.CharField(max_length=200)
    def __str__(self):
        return str(self.nom)


class Territoire(models.Model):
    numéro = models.CharField(max_length=10, unique=True)
    nom = models.CharField(max_length=250, null=True, blank=True)
    def __str__(self):
        return str(self.nom)


class Entrée(models.Model):
    
    STATUTS_CONSERVATION = (
        ('EX', 'Éteint'),
        ('EW', 'Éteint dans la nature'),
        ('CR', 'En danger critique'),
        ('EN', 'En danger'),
        ('VU', 'Vulnérable'),
        ('NT', 'Presque menacée'),
        ('LC', 'Préoccupation mineure'),
        ('NE', 'Non évalué'),
    )

    CATÉGORIES = (
        ('O', 'Ordre'),
        ('F', 'Famille'),
        ('G', 'Genre'),
        ('E', 'Espèce'),
    )

    RÉGIME = (
        ('O', 'Omnivore'),
        ('H', 'Herbivore'),
        ('C', 'Carnivore'),
        ('Ci', 'Insectivore'),
        ('Ch', 'Hématophage'),
        ('Cp', 'Piscivore'),
        ('Cm', 'Molluscivore'),
        ('P', 'Planctonivore'),
        ('A', 'Autre'),
    )

    numéro = models.IntegerField(blank=True, null=True, editable=False)
    nom_cherchable = models.CharField(max_length=250, editable=False, blank=True, null=True)
    nom_latin = models.CharField(max_length=200, unique=True)
    nom_vernaculaire = models.CharField(max_length=200)
    autres_noms = models.CharField(max_length=200, blank=True, null=True)

    sources = models.ManyToManyField(Source, blank=True)

    conservation = models.CharField(max_length=2, choices=STATUTS_CONSERVATION, null=True, blank=True)
    catégorie = models.CharField(max_length=2, choices=CATÉGORIES, default="E", null=True, blank=True)
    description = models.TextField(null=True, blank=True)
    cri = models.TextField(null=True, blank=True)

    habitat = models.ManyToManyField(Habitat, blank=True)
    biome = models.ManyToManyField(Biome, blank=True)

    poids_femelle = models.FloatField('Poids moyen d\'une femelle en grammes', default=0.0)
    poids_mâle = models.FloatField('Poids moyen d\'un mâle en grammes', default=0.0)
    poids = models.FloatField('Poids moyen en grammes', default=0.0)

    taille_femelle = models.FloatField('Taille moyen d\'une femelle en centimètres', default=0.0)
    taille_mâle = models.FloatField('Taille moyen d\'un mâle en centimètres', default=0.0)
    taille = models.FloatField('Taille moyen en centimètres', default=0.0)

    espérance_vie = models.IntegerField('Espérance de vie (jours)', default=0)
    régime_alimentaire = models.CharField(max_length=2, choices=RÉGIME, null=True, blank=True)

    miniature_url = models.CharField(max_length=250, null=True, blank=True)
    miniature = models.BinaryField(default=b"")
    photo1_url = models.URLField(max_length=250, null=True, blank=True)
    photo1_droits = models.CharField(max_length=250, null=True, blank=True)
    photo1_source = models.CharField(max_length=250, null=True, blank=True)
    photo2_url = models.URLField(max_length=250, null=True, blank=True)
    photo2_droits = models.CharField(max_length=250, null=True, blank=True)
    photo2_source = models.CharField(max_length=250, null=True, blank=True)
    photo3_url = models.URLField(max_length=250, null=True, blank=True)
    photo3_droits = models.CharField(max_length=250, null=True, blank=True)
    photo3_source = models.CharField(max_length=250, null=True, blank=True)

    localisation = models.ManyToManyField(Territoire, blank=True)

    ordre = models.ForeignKey(Ordre, on_delete=models.CASCADE, null=True, blank=True, editable=False)
    famille = models.ForeignKey(Famille, on_delete=models.CASCADE, null=True, blank=True, editable=False)
    genre = models.ForeignKey(Genre, on_delete=models.CASCADE, null=True, blank=True, editable=False)
    espèce = models.ForeignKey(Espèce, on_delete=models.CASCADE, null=True, blank=True, editable=False)

    groupe1 = models.ForeignKey(Groupe1, on_delete=models.CASCADE, null=True, blank=True)
    groupe2 = models.ForeignKey(Groupe2, on_delete=models.CASCADE, null=True, blank=True)

    def save(self, *args, **kwargs):
        is_to_be_created = True
        if not self.pk:
            is_to_be_created = True

        if self.poids == 0.0:
            self.poids = max(self.poids_femelle, self.poids_mâle)
        if self.taille == 0.0:
            self.taille = max(self.taille_femelle, self.taille_mâle)

        if is_to_be_created:
            if self.catégorie == 'E':
                split = self.nom_latin.split(" ")
                #self.genre = Genre.objects.get(nom_latin=split[0])
                espèce = ' '.join(split[1:])
                for e in Espèce.objects.filter(nom_latin=espèce):
                    if e.genre.nom_latin == split[0]:
                        self.genre = e.genre
                self.espèce = Espèce.objects.get(nom_latin=espèce, genre=self.genre)
            elif self.catégorie == 'G':
                self.genre = Genre.objects.get(nom_latin=nom_latin)
            elif self.catégorie == 'F':
                self.famille = Famille.objects.get(nom_latin=nom_latin)
            elif self.catégorie == 'O':
                self.ordre = Ordre.objects.get(nom_latin=nom_latin)

        nom_cherchable = self.nom_vernaculaire + " " + self.nom_latin
        if self.autres_noms:
            self.nom_cherchable = nom_cherchable + " " + self.autres_noms
        self.nom_cherchable = unidecode.unidecode(nom_cherchable).lower()

        # Auto-add pictures and generate base64 thumbnail
        if not self.miniature_url:
            try:
                api = EOL()

                search = api.Search(urllib.parse.quote(self.nom_latin))
                id = search.results[0]['id']
                page = api.Page(id)
                photos = page.data_objects

                self.miniature_url = photos[0]['eolThumbnailURL']

                p = photos[0]
                compressed_photo_url = p['eolMediaURL'].replace('orig', '580_360')
                self.photo1_url = compressed_photo_url
                try:
                    self.photo1_droits = p['rightsHolder']
                except:
                    pass
                self.photo1_source = p['source']

                if len(photos) > 1:
                    p = photos[1]
                    compressed_photo_url = p['eolMediaURL'].replace('orig', '580_360')
                    self.photo2_url = compressed_photo_url
                    try:
                        self.photo2_droits = p['rightsHolder']
                    except:
                        pass
                    self.photo2_source = p['source']

                if len(photos) > 2:
                    p = photos[2]
                    compressed_photo_url = p['eolMediaURL'].replace('orig', '580_360')
                    self.photo3_url = compressed_photo_url
                    try:
                        self.photo3_droits = p['rightsHolder']
                    except:
                        pass
                    self.photo3_source = p['source']

            except:
                pass

        self.miniature = base64.b64encode(requests.get(self.miniature_url).content)

        super(Entrée, self).save(*args, **kwargs) 

    def __str__(self):
        return str(self.numéro) + " : " + self.nom_vernaculaire + " (" + self.nom_latin + ")"


@receiver(post_save, sender=Entrée)
def update_entrée_localisation(sender, instance, created, **kwargs):
    # Add location data for mainland
    if instance.catégorie == 'E' and created:
        INPN_URL = "https://inpn.mnhn.fr/cartosvg/couchegeo/repartition/atlas/%s/fr_light_l93,fr_light_mer_l93,fr_lit_l93"
        url = INPN_URL % instance.espèce.inpn_id
        svg_map = requests.get(url).text.replace('\r','\n')

        location_data = []
        location_data_available = False
        for line in svg_map.split('\n'):
            if "var date" in line:
                location_data.append(line)
                if not location_data_available:
                    if "var date01 =" in line:
                        location_data_available = True

        territoires = set()
        if location_data_available:
            for l in location_data:
                # If present or possibly present
                if "'4'" in l or "'5'" in l:
                    code = l[l.index("var date")+len("var date"):][0:2]
                    territoires.add(Territoire.objects.get(numéro=code).pk)
        transaction.on_commit(lambda: instance.localisation.add(*Territoire.objects.filter(pk__in=territoires)))

