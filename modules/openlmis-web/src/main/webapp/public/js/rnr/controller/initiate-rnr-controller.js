function InitiateRnrController($scope, Requisition, facilities, programs, UserSupportedProgramInFacilityForAnOperation, UserSupervisedFacilitiesForProgram, $location) {

  $scope.programOptionMsg = "--choose program--";
  $scope.facilities = facilities;
  $scope.facilityOptionMsg = "--choose facility--";
  if ($scope.facilities == null || $scope.facilities.length == 0) {
    $scope.facilityOptionMsg = "--none assigned--";
  }

  $scope.programs = programs;
  if ($scope.programs == null || $scope.programs.length == 0) {
    $scope.programOptionMsg = "--none assigned--";
  }
  $scope.$parent.program = null;
  $scope.$parent.facility = null;

  $scope.loadPrograms = function () {
    if ($scope.$parent.facility) {
      UserSupportedProgramInFacilityForAnOperation.get({facilityId:$scope.$parent.facility}, function (data) {
        $scope.programs = data.programList;
        $scope.programOptionMsg = "--choose program--";
        if ($scope.programs == null || $scope.programs.length == 0) {
          $scope.programOptionMsg = "--none assigned--";
        }
      }, {});
    } else {
      $scope.programs = null;
      $scope.programOptionMsg = "--choose program--";
    }
  };

  $scope.loadFacilities = function () {
    if ($scope.$parent.program) {
      UserSupervisedFacilitiesForProgram.get({programId:$scope.$parent.program.id}, function (data) {
        $scope.facilities = data.facilities;
        $scope.facilityOptionMsg = "--choose facility--";
        if ($scope.facilities == null || $scope.facilities.length == 0) {
          $scope.facilityOptionMsg = "--none assigned--";
        }
      }, {});
    } else {
      $scope.facilities = null;
      $scope.facilityOptionMsg = "--choose facility--";
    }
  };

  $scope.initRnr = function () {
    if (validate()) {
      $scope.error = "";
      $scope.$parent.sourceUrl = $location.$$url;

      Requisition.get({facilityId:$scope.facility, programId:$scope.program.id},{},
        function (data) {
          if (data.rnr) {
            $scope.$parent.rnr = data.rnr;
            $location.path('/create-rnr/' + $scope.facility + '/' + $scope.program.id);
          }
          else {
            Requisition.save({facilityId:$scope.facility, programId:$scope.program.id}, {}, function (data) {
              $scope.$parent.rnr = data.rnr;
              $location.path('/create-rnr/' + $scope.facility + '/' + $scope.program.id);
            }, function () {
              $scope.error = "Requisition does not exist. Please initiate.";
            })
          }
        }, {});


    } else {
      $scope.error = "Please select Facility and program for facility to proceed";
    }
  };

  var validate = function () {
    return $scope.$parent.program;
  };
}


InitiateRnrController.resolve = {
  facilities:function ($q, $timeout, UserFacilityList) {
    var deferred = $q.defer();
    $timeout(function () {
      UserFacilityList.get({}, function (data) {
          deferred.resolve(data.facilityList);
        }, {}
      );
    }, 100);

    return deferred.promise;
  },
  programs:function () {
  }
}

function InitiateSupervisedRnrController() {
}
;
InitiateSupervisedRnrController.resolve = {
  programs:function ($q, $timeout, UserSupervisedProgramList) {
    var deferred = $q.defer();
    $timeout(function () {
      UserSupervisedProgramList.get({}, function (data) {
          deferred.resolve(data.programList);
        }, {}
      );
    }, 100);

    return deferred.promise;
  },
  facilities:function () {
  }
}