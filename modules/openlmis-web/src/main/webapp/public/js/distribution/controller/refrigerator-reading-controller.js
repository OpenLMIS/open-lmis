function RefrigeratorReadingController($scope) {
  var fieldList = ['temperature', 'functioningCorrectly', 'lowAlarmEvents', 'highAlarmEvents', 'problemSinceLastTime'];

  $scope.getStatus = function () {
    var statusClass = 'is-complete';

    function isValidField(field) {
      return !$scope.refrigeratorReading[field] || (isUndefined($scope.refrigeratorReading[field].value) && !$scope.refrigeratorReading[field].notRecorded);
    }

    $(fieldList).each(function (index, field) {
      if (isValidField(field)) {
        statusClass = 'is-empty';
        return false;
      }
      return true;
    });

    if (statusClass === 'is-empty') {
      $(fieldList).each(function (index, field) {
        if (!isValidField(field)) {
          statusClass = 'is-incomplete';
          return false;
        }
        return true;
      });
    }

    if (statusClass === 'is-complete' && $scope.refrigeratorReading['problemSinceLastTime'].value === 'Y') {
      var hasAtLeastOneProblem = _.filter(_.values($scope.refrigeratorReading.problems.problemMap),function (problem) {
        return problem;
      }).length;

      if (!$scope.refrigeratorReading.problems.problemMap || !hasAtLeastOneProblem)
        statusClass = 'is-incomplete';
    }
    return statusClass;
  };
};