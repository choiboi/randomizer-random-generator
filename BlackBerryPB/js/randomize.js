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
    return rangeL[this.numberGenerator(rangeL.length)];
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
// This class is used for managing the textfields.
//
//
var TextboxFields = function() {
	 this.numFields = 2;
     this.reset = $('#textfields').html();
	 this.classValue = "ui-input-text ui-body-c ui-corner-all ui-shadow-inset";
};

// Adds textfields into the screen.
TextboxFields.prototype.addField = function() {
	this.numFields += 1;
    if ($('#' + this.numFields).length !== 0) {
        $('#' + this.numFields).show();
    } else {
    	htmlNewTB = "<input id='" + this.numFields + "' placeholder='Enter Choice Here' type='text'/>";
        $('#textfields').append(htmlNewTB);
        $('#' + this.numFields).addClass(this.classValue);
    }
};

// Removes a textfield.
TextboxFields.prototype.removeField = function() {
    if (this.numFields > 2) {
        $('#' + this.numFields).hide();
        $('#' + this.numFields).val("");
        this.numFields -= 1;
    }
};

// Resets the screen back to two empty textboxes.
TextboxFields.prototype.resetFields = function() {
    this.numFields = 2;
    $('#textfields').html(this.reset);
    $('#1').addClass("ui-input-text ui-body-c ui-corner-all ui-shadow-inset");
    $('#2').addClass(this.classValue);
};

// Returns a list of choices the user has inputed.
TextboxFields.prototype.getTextValues = function() {
    choicesL = [];

    for (var i = 1; i <= this.numFields; i++) {
        val = $('#' + i).val();
        if (val !== "") {
            choicesL.push(val);
        }
    }
    return choicesL;
}

window.tbFields = new TextboxFields();