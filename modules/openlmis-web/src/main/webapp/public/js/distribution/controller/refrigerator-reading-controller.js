function RefrigeratorReadingController($scope) {
  $scope.getStatus = function () {
    return new RefrigeratorReading($scope.refrigeratorReading).computeStatus();
  };
};
