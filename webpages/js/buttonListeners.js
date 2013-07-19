// Content area on Main Page related listeners.
$('#clearAllButtonMainPage').on("tap", function(e) {
    window.selections.clearSelections();
    e.preventDefault();
});

$('#addButtonMainPage').on("tap", function(e)  {
    window.selections.addSelection();
    e.preventDefault();
});

$('#loadSelectionsButtonMainPage').on("tap", function(e)  {
    window.selections.showSavedSelectionsListview();
    $.mobile.changePage($('#loadSelectionsPage'));
    e.preventDefault();
});

$('#loadSelectionPageDeleteButton').on("tap", function(e)  {
    window.selections.showDeleteSavedSelectionsListview();
    $.mobile.changePage($('#loadSelectionsDeletePage'));
    e.preventDefault();
});

$('#loadSelectionPageDoneButton').on("tap", function(e)  {
    window.selections.showSavedSelectionsListview();
    $.mobile.changePage($('#loadSelectionsPage'));
    e.preventDefault();
});

$('#saveSelectionsButtonMainPage').on("tap", function(e) {
    var sList = window.selections.getSelectionsList();
    
    if (sList.length !== 0) {
        window.db.addSelectionsList(sList);
    } else {
        $('#descSaveListAlertPopup').html("Your selections list is empty. Please add selections before saving.");
        $('#saveListAlertPopup').popup('open');
    }
    e.preventDefault();
});


// Main page menu buttons.
$('#prevChoicePageButton').on("tap", function(e) {
    e.preventDefault();
    window.resultLayout.listPrevChoiceLayout(window.db.getChoiceJSON());
    $.mobile.changePage($('#prevChoicePage'));
});

$('#orderedListPageButton').on("tap", function(e) {
    e.preventDefault();
    window.resultLayout.listSaveListLayout(window.db.getSaveListJSON());
    $.mobile.changePage($('#saveOrderedList'));
});

$('#deleteAllListButton').on("tap", function(e) {
    window.db.deleteAllOrderedList();
    $.mobile.changePage($('#randomizer'));
    e.preventDefault();
});

$('#aboutPageButton').on("tap", function(e) {
    $.mobile.changePage($('#about'));
    e.preventDefault();
});


// Main page footer buttons.
$('#randomChoiceButton').on("tap", function(e) {
    var choicesL = window.selections.getSelectionsList();
    if (choicesL.length === 0) {
        $('#emptyAlertPopup').popup('open');
    } else {
        $('#resultFooter').attr("class", "ui-grid-solo");
        $('#resultFooterSaveButton').hide();
        var resultInd = window.randGen.generateNumber(0, choicesL.length - 1);
        window.resultLayout.choiceLayout(choicesL[resultInd], choicesL);
        window.db.addSingleChoice(choicesL[resultInd], choicesL);
        $.mobile.changePage($('#result'));
    }
    e.preventDefault();
});

$('#randomOrderButton').on("tap", function(e) {
    var choicesL = window.selections.getSelectionsList();
    if (choicesL.length === 0) {
        $('#emptyAlertPopup').popup('open');
    } else {
        window.resultLayout.orderLayout(choicesL);
        $.mobile.changePage($('#result'));
    }
    e.preventDefault();
});


// Results page footer buttons.
$('#backResultButton').on("tap", function(e) {
    $.mobile.changePage($('#randomizer'));
    window.resultLayout.resetValues();
    var historyCount = history.length - 1;
    history.go(-historyCount);
    e.preventDefault();
});

$('#resultFooterSaveButton').on("tap", function(e) {
    $.mobile.changePage($('#randomizer'));
    window.db.addOrderedList(window.resultLayout.orderedList);
    window.resultLayout.resetValues();
    var historyCount = history.length - 1;
    history.go(-historyCount);
    e.preventDefault();
});


// Random number generator buttons in main page.
$('#randomNumButton').on("tap", function(e) {
    $('#randomNumberPopup').popup('open');
    e.preventDefault();
});

$('#randomNumOKPopupBut').on("tap", function(e) {
    var startNum = parseInt($('#startNum').val());
    var endNum = parseInt($('#endNum').val());

    // Change footer button layout.
    $('#resultFooter').attr("class", "ui-grid-solo");
    $('#resultFooterSaveButton').hide();

    // Update result page.
    var resultNum = 0;
    if (startNum > endNum) {
        resultNum = window.randGen.generateNumber(endNum, startNum);
        window.db.addSingleChoice(resultNum, ["Between " + endNum + " to " + startNum]);
        window.resultLayout.randomNumberLayout(resultNum, endNum, startNum);
    } else {
        resultNum = window.randGen.generateNumber(startNum, endNum);
        window.db.addSingleChoice(resultNum, ["Between " + startNum + " to " + endNum]);
        window.resultLayout.randomNumberLayout(resultNum, startNum, endNum);
    }

    $.mobile.changePage($('#resultRandomNumber'));
    e.preventDefault();
});

$('#resultRandomNumberAgainButton').on("tap", function(e) {
    e.preventDefault();
    var range = window.resultLayout.getRandomNumberRange();
    var resultNum = window.randGen.generateNumber(range[0], range[1]);
    window.db.addSingleChoice(resultNum, ["Between " + range[0] + " to " + range[1]]);
    window.resultLayout.randomNumberLayout(resultNum, range[0], range[1]);
});

// Back button listener which goes back to the main page.
// Can be found in Random Number Generator Popup cancel button, Empty Field Alert
// popup OK button, and About page back button.
$('.backButton').on("tap", function(e) {
    $.mobile.changePage($('#randomizer'));
    e.preventDefault();
    window.resultLayout.resetValues();
    var historyCount = history.length - 1;
    history.go(-historyCount);
});