var PageLayout = function(){
	this.orderedList = [];

    this.getList = function(L) {
        var htmlStr = "";

        for (var i = 0; i < L.length; i++) {
            htmlStr += "<p style='font-size: 1.2em; margin: 0;'><b>" + (i + 1).toString() + ". " + L[i] + "</b></p>";
        }
        return htmlStr;
    };

    this.getChoices = function(L) {
        var htmlStr = "Selected from: ";

        for (var i = 0; i < L.length; i++) {
            if (i === L.length - 1) {
                htmlStr += L[i]; 
            } else {
                htmlStr += L[i] + ", ";
            }
        }
        return htmlStr;
    };
};

PageLayout.prototype.choiceLayout = function(choice) {
	$('#resultHeader').html("Randomizer Has <br>Selected");

	$('#resultContent').attr("style", "text-align: center; margin-top: 1.5em; font-size: 2.5em; overflow: scroll;");
	$('#resultContent').html("<p><b>" + choice + "</b></p>");
};

PageLayout.prototype.orderLayout = function(choicesL) {
    var randList = window.randGen.generateListOfNumbers(choicesL.length);
    var orderedRandL = [];

    var listStr = "";
    for (var i = 0; i < randList.length; i++) {
    	orderedRandL.push(choicesL[randList[i]]);
    	listStr += "<p style='font-size: 1.1em; margin: 0;'><b>" + (i + 1) + ".</b> " + choicesL[randList[i]] + "</p>";
    }
    this.orderedList = orderedRandL;

	$('#resultHeader').html("Randomized Order");

	$("#resultContent").attr("style", "font-size: 1.3em; margin: 0 auto; text-align: center; margin-top: 1.4em; overflow: scroll;");
	$("#resultContent").html(listStr);
};

PageLayout.prototype.listPrevChoiceLayout = function(choiceJSON) {
    var size = window.db.lengthJSON(choiceJSON);
    var htmlStr = "";

    for (var i = 1; i <= size; i++) {
        var choiceL = choiceJSON[i.toString()]
        htmlStr += "<li class='ui-li ui-li-static ui-btn-up-c'><h3 class='ui-li-heading'>" + 
                    choiceL[0] + "</h3><p class='ui-li-desc'>" + this.getChoices(choiceL[2]) + 
                    "</p><p class='ui-li-desc choiceDate'>" + choiceL[1] + "</p></li>";
    }
    
    if (htmlStr === "") {
        htmlStr = "<p style='margin: 5em auto; text-align: center; color: gray; font-size: 1.2em;'><b>NO SAVED CHOICES</b></p>";
    }
    
    $('#singleChoiceListView').html(htmlStr);
    $('.choiceDate').attr("style", "color: gray");
};

PageLayout.prototype.listSaveListLayout = function(listJSON) {
    var size = window.db.lengthJSON(listJSON);
    var htmlStr = "";

    for (var i = 1; i <= size; i++) {
        var listL = listJSON[i.toString()];
        htmlStr += "<li class='ui-li ui-li-static ui-btn-up-c ui-li-has-thumb'>" + this.getList(listL[0]) +
                    "<p style='margin-top: 0.5em;' class='ui-li-desc'>Randomized on " + listL[1] + "</p>" +
                    "<img onclick='deleteL(" + i.toString() + ");' src='css-framework/images/delete_icon_black.png' " +
                    "class='ui-li-thumb' style='left: 2.5em; top: 40%'/>";
    }

    if (htmlStr === "") {
        htmlStr = "<p style='margin: 5em auto; text-align: center; color: gray; font-size: 1.2em;'><b>NO SAVED LISTS</b></p>";
    }

    $('#randomizedListView').html(htmlStr);
};

PageLayout.prototype.resetValues = function() {
	// Reset back to the original footer layout.
    $('#resultFooter').attr("class", "ui-grid-a");
    $('#resultFooterSaveButton').show();

    // Clear the content fields.
    $('#resultContent').empty();
    $('#resultHeader').empty();

    // Reset values.
    this.orderedList = [];
};

window.resultLayout = new PageLayout();