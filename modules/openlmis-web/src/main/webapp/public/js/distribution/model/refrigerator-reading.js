function RefrigeratorReading(refrigeratorReading) {

  var fieldList = ['temperature', 'functioningCorrectly', 'lowAlarmEvents', 'highAlarmEvents', 'problemSinceLastTime'];

  RefrigeratorReading.prototype.computeStatus = function () {
    var complete = 'is-complete';
    var incomplete = 'is-incomplete';
    var empty = 'is-empty';

    var statusClass = complete;
    var _this = this;

    function isEmpty(field) {
      if (isUndefined(_this[field])) {
        return true;
      }
      return (isUndefined(_this[field].value) && !_this[field].notRecorded);
    }

    $(fieldList).each(function (index, field) {
      if (isEmpty(field)) {
        statusClass = empty;
        return false;
      }
      return true;
    });

    if (statusClass === empty) {
      $(fieldList).each(function (index, field) {
        if (!isEmpty(field)) {
          statusClass = incomplete;
          return false;
        }
        return true;
      });
    }

    if (statusClass === complete && _this['problemSinceLastTime'] && _this['problemSinceLastTime'].value === 'Y') {
      if (!_this.problems) statusClass = incomplete;
      else {
        var hasAtLeastOneProblem = _.filter(_.values(_this.problems.problemMap),
          function (problem) {
            return problem;
          }).length;

        if (!_this.problems.problemMap || !hasAtLeastOneProblem)
          statusClass = incomplete;
      }
    }

    _this.status = statusClass;

    return statusClass;
  };

  init.call(this);

  function init() {
    var _this = this;
    $.extend(true, this, refrigeratorReading);
    $(fieldList).each(function (i, fieldName) {
      _this[fieldName] = _this[fieldName] || {};
    });
    this.computeStatus();
  }

}
