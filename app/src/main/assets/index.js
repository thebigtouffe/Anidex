var oldQueryLength;
var previousPosition = {"az":0, "favori":0, "poids":0};
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

function rememberPosition() {
     if (vue == "favori") previousPosition.favori = window.pageYOffset;
     if (vue == "az") previousPosition.az = window.pageYOffset;
     if (vue == "poids") previousPosition.poids = window.pageYOffset;
}

function triAZ (a, b) {
    if (a.name.toLowerCase().sansAccent() == b.name.toLowerCase().sansAccent()) {
        return 0;
    }
    else {
        return (a.name.toLowerCase().sansAccent() < b.name.toLowerCase().sansAccent()) ? -1 : 1;
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

function updateFavori() {
	// les favori récupéré d'Android sont un string qu'on convertit en liste
	favori = Android.getFavori().split(", ");

	var i = 1;
	while (i<favori.length) {
		document.getElementById('line' + favori[i]).src = "favorite.png";
		i++;
	}

    // Si on est dans la vue favori on actualise la liste des favori affichés
	if (vue == "favori") {
        var i = 0;
        while (i < data.length) {
            numero = i.toString();
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

function updateParametres() {
	param = Android.getParametres();
}

function showAZ() {
	rememberPosition();
    vue = "az";

    // trie les données dans l'ordre alphabétique
    data.sort(triAZ);

    document.getElementById("search-wrapper").style.display = "block";
	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();

    var i = 0;
    var str = "";
    while (i < data.length) {
   		str = str + '<div class="liste" onclick="afficherFiche(' + i.toString() + ')" id="' + i.toString() + '">' + data[i].name + '<img class="favorite" id="line' + i.toString() + '" src = "not_favorite.png"/>' + "</div></div>";
   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	updateFavori();
	updateParametres();

	window.scrollTo(0,previousPosition.az);

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
		document.getElementById(i.toString()).style.display = "block";
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
				if (data[i].name.toLowerCase().sansAccent().indexOf(motQuery[j]) > -1) {
					occurence[i]++;
				}
				else {
					occurence[i]--;
				}
				j++;
			}

			if (occurence[i] > 0) {
   				document.getElementById(i.toString()).style.display = "block";
			}
			else {
				document.getElementById(i.toString()).style.display = "none";
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

	console.debug(data["name"]);
}

function showFavori() {
    rememberPosition();
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
            numero = i.toString();
            if (favori.indexOf(numero) == -1) {
                document.getElementById(numero).style.display = "none";
            }
            else {
                document.getElementById(numero).style.display = "block";
            }
            i++;
        }
        window.scrollTo(0,previousPosition.favori)
    }
}

function showPoids() {
    rememberPosition();
    vue = "poids";

    data.sort(triPoids);

    document.getElementById("search-wrapper").style.display = "block";
	document.getElementById("champRecherche").value = "";
	oldQueryLength = 0;
	document.getElementById("champRecherche").blur();

	var i = 0;
    var str = "";
    while (i < data.length) {
   		str = str + '<div class="liste" onclick="afficherFiche(' + i.toString() + ')" id="' + i.toString() + '">' + data[i].name + '<img class="favorite" id="line' + i.toString() + '" src = "not_favorite.png"/>' + data[i].poids + "</div>";
   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	updateFavori();
	updateParametres();

	window.scrollTo(0,previousPosition.poids)

}