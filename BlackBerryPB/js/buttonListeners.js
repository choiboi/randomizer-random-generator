// Textfield related buttons.
$('#removeTextfieldButton').click(function() {
    window.tbFields.removeField();
});

$('#addTextfieldButton').click(function() {
	window.tbFields.addField();
});

$('#resetButton').click(function() {
    window.tbFields.resetFields();
});

// Main page menu buttons.
$('#prevChoicePageButton').click(function() {
    window.resultLayout.listPrevChoiceLayout(window.db.getChoiceJSON());
    $.mobile.changePage($('#prevChoicePage'));
});

$('#orderedListPageButton').click(function() {
    window.resultLayout.listSaveListLayout(window.db.getSaveListJSON());
    $.mobile.changePage($('#saveOrderedList'));
});

$('#deleteAllListButton').click(function() {
    window.db.deleteAllOrderedList();
});

// Main page footer buttons.
$('#randomChoiceButton').click(function() {
    var choicesL = window.tbFields.getTextValues();
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
});

$('#randomOrderButton').click(function() {
    var choicesL = window.tbFields.getTextValues();
    if (choicesL.length === 0) {
        $('#emptyAlertPopup').popup('open');
    } else {
        window.resultLayout.orderLayout(choicesL);
        $.mobile.changePage($('#result'));
    }
});

// Results page footer buttons.
$('#backResultButton').click(function() {
    $.mobile.changePage($('#randomizer'));
    window.resultLayout.resetValues();
});

$('#resultFooterSaveButton').click(function() {
    $.mobile.changePage($('#randomizer'));
    window.db.addOrderedList(window.resultLayout.orderedList);
    window.resultLayout.resetValues();
});

// Random number generator buttons in main page.
$('#randomNumButton').click(function() {
    $('#randomNumberPopup').popup('open');
});

$('#randomNumOKPopupBut').click(function() {
    var startNum = parseInt($('#startNum').val());
    var endNum = parseInt($('#endNum').val());

    // Change footer button layout.
    $('#resultFooter').attr("class", "ui-grid-solo");
    $('#resultFooterSaveButton').hide();

    // Update result page.
    var resultNum = window.randGen.generateNumber(startNum, endNum);
    window.resultLayout.choiceLayout(resultNum);
    window.db.addSingleChoice(resultNum, ["Between " + startNum + " to " + endNum]);
    $.mobile.changePage($('#result'));
});