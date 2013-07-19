var deleteL = function(id) {
	window.db.deleteOrderedList(parseInt(id));
	window.resultLayout.listSaveListLayout(window.db.getSaveListJSON());
};

//
//
// This Class handles everything related to store and deleting localStorage
// database.
//
//
var RandomizerDB = function() {

	this.single = "single";
	this.orderedList = "list";
    this.savedList = "savedList";
    this.savedListCount = "savedListCount";

	// This function returns current month, day, and year in string.
	this.getCurrDate = function() {
		var dateRaw = new Date();
		var dateSplit = dateRaw.toString().split(" ");

		return dateSplit[1] + ". " + dateSplit[2] + ", " + dateSplit[3];
	};

	// Counts number of objects in JSOn.
	this.countJSONObj = function(JSONObj) {
		var counter = 0;

		for (var x in JSONObj) {
			if (JSONObj.hasOwnProperty(x)) {
				counter++;
			}
		}
		return counter;
	};

	// This function removes the first entry from single choice JSON.
	this.deleteFirstEntry = function(JSONObj) {
		var indCounter = 2;

		for (var i = 1; i < 20; i++) {
			JSONObj[i.toString()] = JSONObj[indCounter.toString()];
			indCounter++;
		}
		return JSONObj;
	};
    
    // This function checks if there are duplicates for savedLists.
    this.existsDuplicates = function(currJSON, sList) {
        
        for (key in currJSON) { 
            if (currJSON[key].length === sList.length) {
                var count = 0;
                var currList = currJSON[key];
                
                for (var i = 0; i < currList.length; i++) {
                    for (var i2 = 0; i2 < sList.length; i2++) {
                        if (currList[i] === sList[i2]) {
                            count++;
                            break;
                        }
                    }
                }
                
                if (count === currList.length) {
                    return true;
                }
            }
        }
        
        return false;
    };
};

// This function checks if localstorage db has been initiated.
RandomizerDB.prototype.init = function() {
	if (typeof localStorage[this.single] === "undefined") {
		localStorage[this.single] = JSON.stringify({});
	}

	if (typeof localStorage[this.orderedList] === "undefined") {
		localStorage[this.orderedList] = JSON.stringify({});
	}
    
    if (typeof localStorage[this.savedList] === "undefined") {
        localStorage[this.savedList] = JSON.stringify({});
    }
    
    if (typeof localStorage[this.savedListCount] === "undefined") {
        localStorage[this.savedListCount] = "0";
    }
};

// This function adds single random choices into localStorage. If there
// are more than 20 previous entries then remove the oldest entry and
// add the new entry.
RandomizerDB.prototype.addSingleChoice = function(choice, choiceL) {
	var s = jQuery.parseJSON(localStorage[this.single]);
	var numEntries = this.countJSONObj(s);
	var newEntry = Array(choice, this.getCurrDate(), choiceL);

	if (numEntries === 20) {
		s = this.deleteFirstEntry(s);
		s[numEntries] = newEntry;
	} else {
		s[numEntries + 1] =	newEntry;	
	}
	localStorage[this.single] = JSON.stringify(s);
};

// This function adds new ordered list entry.
RandomizerDB.prototype.addOrderedList = function(orderedL) {
	var s = jQuery.parseJSON(localStorage[this.orderedList]);
	var numEntries = this.countJSONObj(s);
	var newEntry = Array(orderedL, this.getCurrDate());

	if (orderedL.length !== 0) {
		s[numEntries + 1] = newEntry;
		localStorage[this.orderedList] = JSON.stringify(s);
	}
};

// This function will delete an ordered list entry from localStorage.
RandomizerDB.prototype.deleteOrderedList = function(id) {
	var s = jQuery.parseJSON(localStorage[this.orderedList]);
	var numEntries = this.countJSONObj(s);
	var replaceInd = -1;

	for (var  i = 1; i <= numEntries; i++) {
		if (i === id) {
			replaceInd = id + 1;
		}
		if (replaceInd !== -1) {
			if (i === numEntries) {
				delete s[i.toString()];
			} else if (i <= numEntries) {
				s[i.toString()] = s[replaceInd.toString()];
			}
			replaceInd++;
		}
	}
	localStorage[this.orderedList] = JSON.stringify(s);
};

// This function will delete all of the ordered lists from localStorage.
RandomizerDB.prototype.deleteAllOrderedList = function() {
	localStorage[this.orderedList] = JSON.stringify({});
};

// Returns the size of the given JSON object.
RandomizerDB.prototype.lengthJSON = function(JSONObj) {
	return this.countJSONObj(JSONObj);
};

// This function returns the JSON object containing previous choices.
RandomizerDB.prototype.getChoiceJSON = function() {
	return jQuery.parseJSON(localStorage[this.single]);
};

// This function returns the JSON object containing previous saved lists.
RandomizerDB.prototype.getSaveListJSON = function() {
	return jQuery.parseJSON(localStorage[this.orderedList]);
};

// This function stores a list of selections.
RandomizerDB.prototype.addSelectionsList = function(sList) {
    var s = jQuery.parseJSON(localStorage[this.savedList]);
    var num = parseInt(jQuery.parseJSON(localStorage[this.savedListCount]));
    
    if (!this.existsDuplicates(s, sList)) {
        s[num] = sList;
        localStorage[this.savedList] = JSON.stringify(s);
        localStorage[this.savedListCount] = (num + 1).toString();
        $('#descSaveListAlertPopup').html("Your list of selections is saved.");
        $('#saveListAlertPopup').popup('open');
    } else {
        $('#descSaveListAlertPopup').html("These selections have already been saved.");
        $('#saveListAlertPopup').popup('open');
    }
};

// This function removes an entry in the list of selections JSON object.
RandomizerDB.prototype.deleteSelectionList = function(sKey) {
    var s = jQuery.parseJSON(localStorage[this.savedList]);
    delete s[sKey];
    localStorage[this.savedList] = JSON.stringify(s);
    
    if (jQuery.isEmptyObject(s)) {
        localStorage[this.savedListCount] = "0";
    }
};

// This function returns the JSON object containing saved lists of selections.
RandomizerDB.prototype.getSavedSelectionsJSON = function() {
    return jQuery.parseJSON(localStorage[this.savedList]);
};

window.db = new RandomizerDB();