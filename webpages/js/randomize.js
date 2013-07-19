//
//
// This class is used for randomly generating a number and a list.
//
//
var RandomGenerator = function() {
    // This function generates a number between 0 - num.
    this.numberGenerator = function(num) {
        return Math.floor(Math.random() * (num + 1));
    };
    
    // This function generates an array between the given numbers start and end.
    this.range = function(start, end) {
        rangeL = [];
        
        for (var i = start; i <= end; i++) {
            rangeL.push(i);
        }
        return rangeL;
    };
};

// Generates a single number from and including given start to end
// numbers.
RandomGenerator.prototype.generateNumber = function(start, end) {
    if (start === 0) {
        return this.numberGenerator(end);
    } 

    var rangeL = this.range(start, end);
    return rangeL[this.numberGenerator(rangeL.length - 1)];
};

// This app will randomize the ordering of a list by randomizing the
// indicies of a list.
RandomGenerator.prototype.generateListOfNumbers = function(size) {
    var indList = this.range(0, size - 1);
    var resIndList = [];
    
    while (indList.length !== 0) {
        var i = this.numberGenerator(indList.length - 1);
        resIndList.push(indList[i]);
        indList.splice(i, 1);
    }
    return resIndList;
};

window.randGen = new RandomGenerator();


//
//
// This class is used to handle adding in selections.
//
//
var Selections = function() {
    this.selectionsList = [];

    this.init = function() {
        $('#selectionsListMainPage').html("<li data-role='list-divider'>Your Selections</li>");
        $('#selectionsListMainPage').append("<li>None</li>");
        $('#selectionsListMainPage').listview('refresh');
    };

    this.updatePage = function() {
        $('#selectionsListMainPage').html("<li data-role='list-divider'>Your Selections</li>");

        for (var i = 0; i < this.selectionsList.length; i++) {
            var li = $("<li></li>");
            li.append(this.selectionsList[i]);
            var span = $("<span></span>");
            span.attr("style", "float: right;");
            var img = $("<img></img>");
            img.attr({
                src : "css-framework/images/delete_icon_black.png",
                style : "width: 20px; height: 20px",
                value : i
            });
            img.on("tap", function (e) {
                window.selections.removeChoice(parseInt($(this).val()));
                e.preventDefault();
            });

            span.append(img);
            li.append(span);
            $('#selectionsListMainPage').append(li);
        }
        
        if (this.selectionsList.length === 0) {
            $('#selectionsListMainPage').append("<li>None</li>");
        }

        $('#selectionsListMainPage').listview('refresh');
    };
};

Selections.prototype.addSelection = function() {
    var choice = $('#selectionTextfieldMainPage').val();
    if (choice !== "") {
        this.selectionsList.push(choice);
        this.updatePage();
        $('#selectionTextfieldMainPage').val("");
    }
};

Selections.prototype.clearSelections = function() {
    this.selectionsList = [];
    this.updatePage();
};

Selections.prototype.removeChoice = function(index) {
    this.selectionsList.splice(index, 1);
    this.updatePage();
};

Selections.prototype.getSelectionsList = function() {
    return this.selectionsList;
};

Selections.prototype.setNewSelectionsList = function(sKey) {
    var sJSON = window.db.getSavedSelectionsJSON();
    this.selectionsList = sJSON[sKey];
    this.updatePage();
    $.mobile.changePage($('#randomizer'));
    var historyCount = history.length - 1;
    history.go(-historyCount);
};

Selections.prototype.showSavedSelectionsListview = function() {
    var sJSON = window.db.getSavedSelectionsJSON();

    $('#loadSelectionsListview').html("<li data-role='list-divider'>Select the list that you would like to load:</li>");
    
    if (jQuery.isEmptyObject(sJSON)) {
        $('#loadSelectionsListview').append("<li>You have no saved lists.</li>");
    } else {
        var count = 1;
        for (key in sJSON) {
            var sList = sJSON[key];
            var li = $("<li></li>");
            var a = $("<a></a>");
            a.attr({
                style : "margin-top: -0.6em;",
                href : "",
                id : key
            });
            a.append("<h3 style='margin-bottom: 0.2em;'>List " + count + "</h3>");
            
            for (var i = 0; i < sList.length; i++) {
                var p = $("<p>" + sList[i] + "</p>");
                p.attr("style", "margin: 0;");
                a.append(p);
            }
            a.on("tap", function (e) {
                e.preventDefault();
                window.selections.setNewSelectionsList($(this).attr("id"));
            });
            
            li.append(a);
            $('#loadSelectionsListview').append(li);
           
            count++;
        }
    }
};

Selections.prototype.showDeleteSavedSelectionsListview = function() {
    var sJSON = window.db.getSavedSelectionsJSON();

    $('#loadSelectionsDeleteListview').html("<li data-role='list-divider'>Select lists that you would like to delete:</li>");
    
    if (jQuery.isEmptyObject(sJSON)) {
        $('#loadSelectionsDeleteListview').append("<li>You have no saved lists.</li>");
    } else {
        var count = 1;
        for (key in sJSON) {
            var sList = sJSON[key];
            var li = $("<li></li>");
            var a = $("<a></a>");
            a.attr({
                href : "",
                id : key
            });
            a.append("<h3>List " + count + "</h3>");
            
            for (var i = 0; i < sList.length; i++) {
                var p = $("<p>" + sList[i] + "</p>");
                p.attr("style", "margin: 0;");
                a.append(p);
            }
            a.on("tap", function (e) {
                e.preventDefault();
                window.db.deleteSelectionList($(this).attr("id"));
                window.selections.showDeleteSavedSelectionsListview();
                $('#loadSelectionsDeleteListview').listview('refresh');
            });
            
            li.append(a);
            $('#loadSelectionsDeleteListview').append(li);
           
            count++;
        }
    }
};

window.selections = new Selections();


// Updates listview in Load Saved Selections Pages.
$('#loadSelectionsPage').live("pagebeforeshow", function() {
    $('#loadSelectionsListview').listview('refresh');
});

$('#loadSelectionsDeletePage').live("pagebeforeshow", function() {
    $('#loadSelectionsDeleteListview').listview('refresh');
});

// Handles history.
$(document).bind("pagebeforechange", function(e, data) {
    if (typeof data.toPage === "string") {
        var requestURL = $.mobile.path.parseUrl( data.toPage );

        if (requestURL.hash.search(/^#randomizer/) !== -1) {
            // Remove all history.
            var historyCount = history.length - 1;
            history.go(-historyCount);
        }
    }
});