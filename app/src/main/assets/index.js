var data = JSON.parse(data);
var oldQueryLength = 0;

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
    for(var i = 0; i < accent.length; i++){
        str = str.replace(accent[i], noaccent[i]);
    }

    return str;
}

function updateFavori() {
	// les favori récupéré d'Android sont un string qu'on convertit en liste
	favori = Android.getFavori().split(", ");

	var i = 1;
	while (i<favori.length) {
		document.getElementById('line' + favori[i]).src = "favorite.png";
		i++;
	}
}

function updateParametres() {
	param = Android.getParametres();
}

function rafraichir() {
    var i = 0;
    var str = "";
    while (i < data.length) {
   		str = str + '<div id="' + i.toString() + '">' + data[i].name + '<img id="line' + i.toString() + '" src = "not_favorite.png"/>' + "</div>";
   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	updateFavori();
	updateParametres();

}

function afficherFiche(id) {
    Android.AfficherFiche(id.toString());
}

function effacerRecherche() {
	document.getElementById("champRecherche").value="";
	document.getElementById("champRecherche").focus();

	var i = 0;
	while (i < data.length) {
		document.getElementById(i.toString()).style.display = "block";
		i++;
	}
}

function rechercher() {
	var query = document.getElementById("champRecherche").value.toLowerCase().sansAccent();
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

	if (query.length > 2) {

		// on regarde les mots de la requête
		motQuery = query.split(" ");

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

}

function init() {
	rafraichir();
	setInterval(rechercher, 100);
}

function showFavori() {
	document.getElementById("champRecherche").blur();
	document.getElementById("champRecherche").value="";

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