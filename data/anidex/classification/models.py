from django.db import models

class Naturaliste(models.Model):
    nom = models.CharField(max_length=200)

    def __str__(self):
        return self.nom


class Rang(models.Model):
    nom_latin = models.CharField(max_length=200)
    nom_vernaculaire = models.CharField(max_length=200, null=True, blank=True)

    class Meta:
        abstract = True

    def __str__(self):
        if self.nom_vernaculaire:
            return self.nom_vernaculaire
        else:
            return self.nom_latin

class Phylum(Rang):
    pass


class Classe(Rang):
    phylum = models.ForeignKey(Phylum, on_delete=models.CASCADE)


class Ordre(Rang):
    classe = models.ForeignKey(Classe, on_delete=models.CASCADE, null=True, blank=True)


class Famille(Rang):
    ordre = models.ForeignKey(Ordre, on_delete=models.CASCADE, null=True, blank=True)


class Genre(Rang):
    famille = models.ForeignKey(Famille, on_delete=models.CASCADE, null=True, blank=True)


class Espèce(Rang):
    genre = models.ForeignKey(Genre, on_delete=models.CASCADE, null=True, blank=True)
    naturaliste = models.ForeignKey(Naturaliste, on_delete=models.CASCADE, null=True, blank=True)
    date_découverte = models.IntegerField(null=True, blank=True)

    inpn_id = models.IntegerField(null=True, blank=True)

    class Meta:
        unique_together = ('genre', 'nom_latin')

    def __str__(self):
        return self.genre.nom_latin + " " + self.nom_latin
