function RnrController($scope, Facility, FacilitySupportedPrograms, $location) {
  Facility.get({}, function (data) {
      $scope.facilities = data.facilityList;
    }, {}
  );

  $scope.loadPrograms = function ($scope) {
    FacilitySupportedPrograms.get({facility:$scope.facility}, function (data) {
      $scope.program = null;
      $scope.programsForFacility = data.programList;
    }, {});
  };

  var validate = function ($scope) {
    if ($scope.program) {
      return true;
    } else {
      return false;
    }
  };

  $scope.getRnrHeader = function ($scope) {
    if (validate($scope)) {
      $location.path('rnr-header');
    }
    else {
      alert('You need to select Facility and program for facility to proceed');
    }
  }
}

function RnrHeaderController($scope, RequisitionHeader, $location) {
  RequisitionHeader.get({code:$scope.facility}, function (data) {
    $scope.header = data.requisitionHeader;
  }, function () {
    $location.path("new-rnr");
  });
}