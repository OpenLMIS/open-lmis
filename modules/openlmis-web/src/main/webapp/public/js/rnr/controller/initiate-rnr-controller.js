function InitiateRnrController($scope, $location, $rootScope, Requisition) {

  var DEFAULT_FACILITY_MESSAGE = '--choose facility--';
  var DEFAULT_PROGRAM_MESSAGE = '--choose program--';

  var optionMessage = function (entity, defaultMessage) {
    return entity == null || entity.length == 0 ? "--none assigned--" : defaultMessage;
  };

  $scope.facilityOptionMessage = function () {
    return optionMessage($scope.facilities, DEFAULT_FACILITY_MESSAGE);
  }

  $scope.programOptionMessage = function () {
    return optionMessage($scope.programs, DEFAULT_PROGRAM_MESSAGE);
  }

  $scope.selectedProgram = null;
  $scope.selectedFacilityId = null;

  $scope.initRnr = function () {
    if ($scope.selectedProgram) {
      $scope.error = "";
      $scope.$parent.sourceUrl = $location.$$url;

      Requisition.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id}, {},
          function (data) {
            if (data.rnr) {
              if (data.rnr.status != 'SUBMITTED' && !$rootScope.hasPermission('CREATE_REQUISITION')) {
                $scope.error = "An R&R has not been submitted yet";
                return;
              }
              $scope.$parent.rnr = data.rnr;
              $location.path('/create-rnr/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id);
            }
            else {
              Requisition.save({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id}, {}, function (data) {
                $scope.$parent.rnr = data.rnr;
                $location.path('/create-rnr/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id);
              }, function () {
                $scope.error = "Requisition does not exist. Please initiate.";
              })
            }
          }, {});
    } else {
      $scope.error = "Please select Facility and program for facility to proceed";
    }
  };
}
