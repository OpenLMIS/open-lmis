function EpiUse(epiUse) {
  var fieldList = ['stockAtFirstOfMonth', 'received', 'distributed', 'loss', 'stockAtEndOfMonth', 'expirationDate'];

  this.epiUse = epiUse;

  EpiUse.prototype.computeStatus = function () {
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

    $(epiUse.productGroups).each(function (index, productGroup) {
      $(fieldList).each(function (index, field) {
        if (isEmpty(field, productGroup.reading)) {
          statusClass = empty;
          return false;
        }
      });
      if (statusClass == empty) {
        return false;
      }
    });


    if (statusClass === empty) {
      $(epiUse.productGroups).each(function (index, productGroup) {
        $(fieldList).each(function (index, field) {
          if (!isEmpty(field, productGroup.reading)) {
            statusClass = incomplete;
            return false;
          }
          return true;
        });

      });
    }


    epiUse.status = statusClass;
    return statusClass;
  }
}
