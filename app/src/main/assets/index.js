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

function init() {
	rafraichir();
	setInterval(rechercher, 100);
}

function rafraichir() {

    var i = 0;
    var str = "";
    while (i < data.length) {
   		str = str + data[i].name + "<br>";
   		i++;
	}
	document.getElementById("listeAnimaux").innerHTML = str;

	Android.getFavori();
	Android.getParametres();

}

function updateFavori() {
    //alert(favori[0]);

}

function updateParametres() {
    //alert(settings[0]);

}

function afficherFiche(id) {
    Android.AfficherFiche(id.toString());
}

function effacerRecherche() {
	document.getElementById("champRecherche").value="";
	document.getElementById("champRecherche").focus();
	rafraichir();
}



function rechercher() {
	var query = document.getElementById("champRecherche").value.toLowerCase().sansAccent();

	// on génère la liste des occurences des mots de la requête dans le nom de l'animal
	var occurence = [];
	i=0;
	while (i < data.length) {
		occurence.push(0);
		i++;
	}

	if (query.length < oldQueryLength && query.length < 1) {
		oldQueryLength = 0;
		effacerRecherche();
	}

	if (query.length > 2) {
		document.getElementById("listeAnimaux").innerHTML = "";

		// on regarde les mots de la requête
		motQuery = query.split(" ");

		var i = 0;
		var str = "";
		while (i < data.length) {
			j=0;
			while(j<motQuery.length) {
				if (data[i].name.toLowerCase().sansAccent().indexOf(motQuery[j]) > -1) {
					occurence[i]++;
				}
				else {
					occurence[i]--;
				}
				j++;
			}

			if (occurence[i] > 0) {
				str = str + data[i].name + "<br>";
			}

			i++;
		}
		document.getElementById("listeAnimaux").innerHTML = str;
		oldQueryLength = query.length;
	}

}