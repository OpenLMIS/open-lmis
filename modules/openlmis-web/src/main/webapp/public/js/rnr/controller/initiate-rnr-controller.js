function InitiateRnrController($scope, $location, $rootScope, Requisition, PeriodsForFacilityAndProgram) {

  var DEFAULT_FACILITY_MESSAGE = '--choose facility--';
  var DEFAULT_PROGRAM_MESSAGE = '--choose program--';
  var DEFAULT_PERIOD_MESSAGE = '--choose period--';

  var optionMessage = function (entity, defaultMessage) {
    return entity == null || entity.length == 0 ? "--none assigned--" : defaultMessage;
  };

  $scope.facilityOptionMessage = function () {
    return optionMessage($scope.facilities, DEFAULT_FACILITY_MESSAGE);
  }

  $scope.programOptionMessage = function () {
    return optionMessage($scope.programs, DEFAULT_PROGRAM_MESSAGE);
  }

  $scope.periodOptionMessage = function () {
    return optionMessage($scope.periods, DEFAULT_PERIOD_MESSAGE);
  }

  $scope.selectedProgram = null;
  $scope.selectedFacilityId = null;
  $scope.selectedPeriod = null;

  $scope.loadPeriods = function () {
    if ($scope.selectedProgram && $scope.selectedFacilityId) {
      PeriodsForFacilityAndProgram.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id}, function (data) {
        $scope.periods = data.periods;
      });
    } else {
      $scope.periods = null;
      $scope.selectedPeriod = null;
    }
  }

  $scope.initRnr = function () {
    if (!($scope.selectedProgram && $scope.selectedPeriod)) {
      $scope.error = "Please select Facility, Program and Period to proceed";
      return;
    }

    $scope.error = "";
    $scope.$parent.sourceUrl = $location.$$url;

    Requisition.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id, periodId: $scope.selectedPeriod.id}, {},
        function (data) {
          if (data.rnr) {
            if (data.rnr.status != 'SUBMITTED' && !$rootScope.hasPermission('CREATE_REQUISITION')) {
              $scope.error = "An R&R has not been submitted yet";
              return;
            }
            $scope.$parent.rnr = data.rnr;
            $scope.$parent.program = $scope.selectedProgram;
            $location.path('/create-rnr/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + '/' + $scope.selectedPeriod.id);
          }
          else {
            Requisition.save({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id, periodId: $scope.selectedPeriod.id}, {}, function (data) {
              $scope.$parent.rnr = data.rnr;
              $scope.$parent.program = $scope.selectedProgram;
              $location.path('/create-rnr/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + '/' + $scope.selectedPeriod.id);
            }, function () {
              $scope.error = "Requisition does not exist. Please initiate.";
            })
          }
        }, {});
  };
}
