var Rnr = function (rnr, programRnrColumns) {

  jQuery.extend(true, this, rnr);
  this.programRnrColumnList = programRnrColumns;

  this.isValid = function () {
    var isValid = true;
    $(rnr.fullSupplyLineItems).each(function (i, lineItem) {
      isValid = lineItem.validateRequiredFields() && !lineItem.arithmeticallyInvalid();
      if (!isValid) return false;
    });
    return isValid;
  };


};