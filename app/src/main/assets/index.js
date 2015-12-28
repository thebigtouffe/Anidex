var oldQueryLength;
var previousPositions = {"seen":0, "favori":0, "az":0, "poids":0, "taille":0, "esperance":0};
var vue;

String.prototype.sansAccent = function() {
    var accent = [
        /[\300-\306]/g, /[\340-\346]/g, // A, a
        /[\310-\313]/g, /[\350-\353]/g, // E, e
        /[\314-\317]/g, /[\354-\357]/g, // I, i
        /[\322-\330]/g, /[\362-\370]/g, // O, o
        /[\331-\334]/g, /[\371-\374]/g, // U, u
        /[\321]/g, /[\361]/g, // N, n
        /[\307]/g, /[\347]/g, // C, c
    ];
    var noaccent = ['A','a','E','e','I','i','O','o','U','u','N','n','C','c'];

    var str = this;
    for (var i = 0; i < accent.length; i++) {
        str = str.replace(accent[i], noaccent[i]);
    }

    return str;
}

function rememberPositions() {
     if (vue == "seen") previousPositions.seen = window.pageYOffset;
     if (vue == "favori") previousPositions.favori = window.pageYOffset;
     if (vue == "az") previousPositions.az = window.pageYOffset;
     if (vue == "poids") previousPositions.poids = window.pageYOffset;
     if (vue == "taille") previousPositions.taille = window.pageYOffset;
     if (vue == "esperance") previousPositions.esperance = window.pageYOffset;
}

function triAZ (a, b) {
    if (a.nom.toLowerCase().sansAccent() == b.nom.toLowerCase().sansAccent()) {
        return 0;
    }
    else {
        return (a.nom.toLowerCase().sansAccent() < b.nom.toLowerCase().sansAccent()) ? -1 : 1;
    }
}

function triPoids (a, b) {
    if (a.poids == b.poids) {
        return 0;
    }
    else {
        return (a.poids > b.poids) ? -1 : 1;
    }
}

function triTaille (a, b) {
    if (a.taille == b.taille) {
        return 0;
    }
    else {
        return (a.taille > b.taille) ? -1 : 1;
    }
}

function triEsperance (a, b) {
    if (a.esperance == b.esperance) {
        return 0;
    }
    else {
        return (a.esperance > b.esperance) ? -1 : 1;
    }
}

function updateFavori() {
	// les favori récupéré d'Android sont un string qu'on convertit en liste
	favori = Android.getFavori().split(", ");

	var i = 1;
	while (i<favori.length) {
		document.getElementById('fav' + favori[i]).src = "favorite.png";
		i++;
	}

    // Si on est dans la vue favori on actualise la liste des favori affichés
	if (vue == "favori") {
        var i = 0;
        while (i < data.length) {
            numero = data[i].id.toString();
            if (favori.indexOf(numero) == -1) {
                document.getElementById(numero).style.display = "none";
            }
            else {
                document.getElementById(numero).style.display = "block";
            }
            i++;
        }
	}
}

function updateSeen() {
	// les aperçus récupérés d'Android sont un string qu'on convertit en liste
	seen = Android.getSeen().split(", ");

	var i = 1;
	while (i<seen.length) {
		document.getElementById('seen' + seen[i]).src = "seen.png";
		i++;
	}

    // Si on est dans la vue aperçus on actualise la liste des aperçus affichés
	if (vue == "seen") {
        var i = 0;
        while (i < data.length) {
            numero = data[i].id.toString();
            if (seen.indexOf(numero) == -1) {
                document.getElementById(numero).style.display = "none";
            }
            else {
                document.getElementById(numero).style.display = "block";
            }
            i++;
        }
	}
}

function updateParametres() {
	param = Android.getParametres();
}

function showAZ() {
	rememberPositions();
    vue = "az";

    // trie les données dans l'ordre alphabétique
    data.sort(triAZ);

    document.getElementById("search-wrapper").style.display = "block";
	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();

    var i = 0;
    var taille;
    var poids;
    var str = "";
    while (i < data.length) {

        if (data[i].taille > 100) {
            taille = (Math.ceil(data[i].taille/10) / 10).toString().replace(".", ",") + ' m';
        }
        else {
            taille = (Math.ceil(data[i].taille)).toString().replace(".", ",") + ' cm';
        }

        if (data[i].poids > 1000) {
            poids = (Math.ceil(data[i].poids/10) / 100).toString().replace(".", ",") + ' kg';
        }
        else {
            poids = (Math.ceil(data[i].poids)).toString().replace(".", ",") + ' g';
        }

   		str = str + '<div class="liste" onclick="afficherFiche(' + data[i].id + ')" id="' + data[i].id + '">' + "<div class='thumb'> <img class ='imgthumb' src='images/" + data[i].id + ".jpg' /></div>";
   		str = str + "<div class='infos'><div class='noms'><div class='nom1 nomLatin'> " + data[i].latin + "</div><div class='nom2 nomVernaculaire'>" + data[i].nom + '</div></div>';
   		str = str + '<div class="wrapper"><div class="autres-infos">'+ 'Taille : ' + taille + ' Poids : ' + poids + '</div>';
   		str = str + '<div class="countainer"><div class="pictogrammes"><img class="pictogramme" id="seen' + data[i].id + '" src = "not_seen.png"/>  <img class="pictogramme" id="fav'+ data[i].id + '" src = "not_favorite.png"/>';
   		str = str + "</div></div></div></div></div></div>";

   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	updateFavori();
	updateParametres();
	updateSeen();

	window.scrollTo(0,previousPositions.az);

}

function afficherFiche(id) {
    Android.AfficherFiche(id.toString());
}

function effacerRecherche() {
	document.getElementById("champRecherche").value="";
	document.getElementById("champRecherche").focus();
	oldQueryLength = 0;

	var i = 0;
	while (i < data.length) {
		document.getElementById(data[i].id.toString()).style.display = "block";
		i++;
	}
}

function rechercher() {
	var query = document.getElementById("champRecherche").value;
	var occurence = [];

	var i = 0;
	while (i < data.length) {
		occurence.push(0);
		i++;
	}

	if (query.length < oldQueryLength && query.length < 1) {
		oldQueryLength = 0;
		effacerRecherche();
	}

	if (query.length > 1) {

	    // on scrolle vers le haut
	    if (query.length > oldQueryLength) {
	    	    window.scrollTo(0, 0);
	    }

		// on regarde les mots de la requête
		motQuery = query.toLowerCase().sansAccent().split(" ");

		var i = 0;
		while (i < data.length) {
			var j = 0;
			while (j < motQuery.length) {
				if (data[i].nom.toLowerCase().sansAccent().indexOf(motQuery[j]) > -1) {
					occurence[i]++;
				}
				else {
					occurence[i]--;
				}
				j++;
			}

			if (occurence[i] > 0) {
   				document.getElementById(data[i].id.toString()).style.display = "block";
			}
			else {
				document.getElementById(data[i].id.toString()).style.display = "none";
			}

			i++;
		}

		oldQueryLength = query.length;
	}

    if (query.length > 0) {
        document.getElementById("close-icon").style.opacity = '1';
    }
    else {
        document.getElementById("close-icon").style.opacity = '0';
    }

}

function init() {
	oldQueryLength = 0;
    vue = "az";

	showAZ(); // Par défaut on affiche par ordre alphabétique
	setInterval(rechercher, 100);
}

function showFavori() {
    rememberPositions();
    vue = "favori";

	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();


	if (favori.length == 1) {
	    document.getElementById("search-wrapper").style.display = "none";
	    document.getElementById("listeAnimaux").innerHTML = "<div class='noFavori'> Vous n'avez pas enregistré de favori.</div>";
	}

	else {
	    document.getElementById("search-wrapper").style.display = "block";

        var i = 0;
        while (i < data.length) {
            numero = data[i].id.toString();
            if (favori.indexOf(numero) == -1) {
                document.getElementById(numero).style.display = "none";
            }
            else {
                document.getElementById(numero).style.display = "block";
            }
            i++;
        }
        window.scrollTo(0,previousPositions.favori)
    }
}

function showSeen() {
    rememberPositions();
    vue = "seen";

	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();


	if (seen.length == 1) {
	    document.getElementById("search-wrapper").style.display = "none";
	    document.getElementById("listeAnimaux").innerHTML = "<div class='noFavori'> Vous n'avez pas encore d'aperçus.</div>";
	}

	else {
	    document.getElementById("search-wrapper").style.display = "block";

        var i = 0;
        while (i < data.length) {
            numero = data[i].id.toString();
            if (seen.indexOf(numero) == -1) {
                document.getElementById(numero).style.display = "none";
            }
            else {
                document.getElementById(numero).style.display = "block";
            }
            i++;
        }
        window.scrollTo(0,previousPositions.favori)
    }
}

function showPoids() {
    rememberPositions();
    vue = "poids";

    data.sort(triPoids);

    document.getElementById("search-wrapper").style.display = "block";
	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();

	var i = 0;
    var str = "";
    while (i < data.length) {
   		str = str + '<div class="liste" onclick="afficherFiche(' + data[i].id.toString() + ')" id="' + data[i].id.toString() + '">' + data[i].nom + '<img class="favorite" id="line' + data[i].id.toString() + '" src = "not_favorite.png"/>' + data[i].poids + "</div>";
   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	updateFavori();
	updateParametres();

	window.scrollTo(0,previousPositions.poids)

}

function showTaille() {
    rememberPositions();
    vue = "taille";

    data.sort(triTaille);

    document.getElementById("search-wrapper").style.display = "block";
	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();

	var i = 0;
    var str = "";
    while (i < data.length) {
   		str = str + '<div class="liste" onclick="afficherFiche(' + data[i].id.toString() + ')" id="' + data[i].id.toString() + '">' + data[i].nom + '<img class="favorite" id="line' + data[i].id.toString() + '" src = "not_favorite.png"/>' + data[i].taille + "</div>";
   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	updateFavori();
	updateParametres();

	window.scrollTo(0,previousPositions.taille)

}

function showEsperance() {
    rememberPositions();
    vue = "esperance";

    data.sort(triEsperance);

    document.getElementById("search-wrapper").style.display = "block";
	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();

	var i = 0;
    var str = "";
    while (i < data.length) {
   		str = str + '<div class="liste" onclick="afficherFiche(' + data[i].id.toString() + ')" id="' + data[i].id.toString() + '">' + data[i].nom + '<img class="favorite" id="line' + data[i].id.toString() + '" src = "not_favorite.png"/>' + data[i].esperance + "</div>";
   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	updateFavori();
	updateParametres();

	window.scrollTo(0,previousPositions.esperance)

}

function showBiome() {
    rememberPositions();
    vue = "biome";

    data.sort(triEsperance);

	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();
	document.getElementById("search-wrapper").style.display = "none";

	updateFavori();
	updateParametres();

}