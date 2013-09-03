function EpiUse(epiUse) {
  var DATE_REGEXP = /^(0[1-9]|1[012])[/]((2)\d\d\d)$/;

  $.extend(true, this, epiUse);

  var fieldList = ['stockAtFirstOfMonth', 'received', 'distributed', 'loss', 'stockAtEndOfMonth', 'expirationDate'];

  EpiUse.prototype.computeStatus = function () {
    var _this = this;
    var complete = 'is-complete';
    var incomplete = 'is-incomplete';
    var empty = 'is-empty';

    var statusClass = complete;

    function isEmpty(field, obj) {
      if (isUndefined(obj[field])) {
        return true;
      }
      return (isUndefined(obj[field].value) && !obj[field].notRecorded);
    }

    function isValid(field, obj) {
      return field != 'expirationDate' || DATE_REGEXP.test(obj[field].value);
    }

    $(_this.productGroups).each(function (index, productGroup) {
      $(fieldList).each(function (index, field) {
        if (!productGroup.reading || isEmpty(field, productGroup.reading)) {
          statusClass = empty;
          return false;
        }
        return true;
      });
      return statusClass != empty;
    });

    $(_this.productGroups).each(function (index, productGroup) {
      $(fieldList).each(function (index, field) {
        if (productGroup.reading && !isEmpty(field, productGroup.reading) && isValid(field, productGroup.reading)) {
          statusClass = incomplete;
          return false;
        }
        return true;
      });

    });

    _this.status = statusClass;

    return statusClass;
  }
}
