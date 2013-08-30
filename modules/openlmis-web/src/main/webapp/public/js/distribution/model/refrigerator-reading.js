function RefrigeratorReading(refrigeratorReading) {
  var fieldList = ['temperature', 'functioningCorrectly', 'lowAlarmEvents', 'highAlarmEvents', 'problemSinceLastTime'];

  this.refrigeratorReading = refrigeratorReading;

  RefrigeratorReading.prototype.computeStatus = function() {
    var complete = 'is-complete';
    var incomplete = 'is-incomplete';
    var empty = 'is-empty';

    var statusClass = complete;
    function isEmpty(field) {
      if(isUndefined(refrigeratorReading[field])) {
        return true;
      }
      return (isUndefined(refrigeratorReading[field].value) && !refrigeratorReading[field].notRecorded);
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

    if (statusClass === complete && refrigeratorReading['problemSinceLastTime'] && refrigeratorReading['problemSinceLastTime'].value === 'Y') {
      if (!refrigeratorReading.problems) statusClass = incomplete;
      else {
        var hasAtLeastOneProblem = _.filter(_.values(refrigeratorReading.problems.problemMap),
          function (problem) {
            return problem;
          }).length;

        if (!refrigeratorReading.problems.problemMap || !hasAtLeastOneProblem)
          statusClass = incomplete;
      }
    }

    refrigeratorReading.status = statusClass;

    return statusClass;
  }

}
