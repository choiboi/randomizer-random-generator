var PageLayout = function(){
	this.orderedList = [];
    this.randomRange = [];

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

PageLayout.prototype.randomNumberLayout = function(choice, start, end) {
    this.randomRange = [start, end];

    $('#resultRandomNumberHeader').html("Randomizer Has <br>Selected");
    $('#resultRandomNumberHeader').append("<br><span style='color: gray; font-size: 0.7em;'>Between " + start + " to " + end + "</span>");

    $('#resultRandomNumberContent').attr("style", "text-align: center; margin-top: 1.5em; font-size: 2.5em; overflow: scroll;");
    $('#resultRandomNumberContent').html("<p><b>" + choice + "</b></p>");
};

PageLayout.prototype.getRandomNumberRange = function() {
    var temp = this.randomRange;
    this.randomRange = [];
    return temp;
};

PageLayout.prototype.listPrevChoiceLayout = function(choiceJSON) {
    var size = window.db.lengthJSON(choiceJSON);
    $('#singleChoiceListView').html("");

    if (size === 0) {
        htmlStr = "<p style='margin: 5em auto; text-align: center; color: gray; font-size: 1.2em;'><b>NO SAVED CHOICES</b></p>";
        $('#singleChoiceListView').append(htmlStr);
    } else {
        for (var i = 1; i <= size; i++) {
            var choiceL = choiceJSON[i.toString()];

            var div = $("<div></div>");
            if (i !== size) {
                div.attr("style", "border-bottom: 1px solid #a0a0a0;");
            }
            div.append("<h3>" + choiceL[0] + "</h3>");
            var p1 = $("<p>" + this.getChoices(choiceL[2]) + "</p>");
            p1.attr("style", "margin-top: -1em; font-size: 0.8em;");
            var p2 = $("<p>" + choiceL[1] + "</p>");
            p2.attr("style", "color: gray; margin-top: -0.9em; font-size: 0.8em;");
            div.append(p1);
            div.append(p2);
            $('#singleChoiceListView').append(div);
        }
    }
};

PageLayout.prototype.listSaveListLayout = function(listJSON) {
    var size = window.db.lengthJSON(listJSON);
    $('#saveRandomizedListView').html("");

    if (size === 0) {
        htmlStr = "<p style='margin: 5em auto; text-align: center; color: gray; font-size: 1.2em;'><b>NO SAVED LISTS</b></p>";
        $('#saveRandomizedListView').html(htmlStr);
    } else {
        for (var i = 1; i <= size; i++) {
            var listL = listJSON[i.toString()];

            var div = $("<div>" + this.getList(listL[0]) + "</div>");
            if (i !== size) {
                div.attr("style", "border-bottom: 1px solid #a0a0a0; margin-top: 0.7em;");
            } else {
                div.attr("style", "margin-top: 0.7em;");
            }
            var date = $("<p>Randomized on " + listL[1] + "</p>");
            date.attr("style", "margin-top: 0.3em; font-size: 0.85em; color: gray;");
            var deleteButton = $("<input></input>");
            deleteButton.attr({
                type : "image",
                src : "css-framework/images/delete_list_button.png",
                style : "width: 100px; height: 20px; margin-top: -0.5em; margin-bottom: 1.1em;",
                value : i
            });
            deleteButton.on("tap", function(e) {
                e.preventDefault();
                deleteL($(this).val());
            });
            div.append(date);
            div.append(deleteButton);
            $('#saveRandomizedListView').append(div);
        }
    }
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