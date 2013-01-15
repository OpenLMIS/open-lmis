function InitiateRnrController($scope, $location, $rootScope, Requisition, UserSupportedProgramInFacilityForAnOperation, UserSupervisedFacilitiesForProgram, UserFacilityList, UserSupervisedProgramList) {

  const DEFAULT_FACILITY_MESSAGE = '--choose facility--';
  const DEFAULT_PROGRAM_MESSAGE = '--choose program--';

  UserFacilityList.get({}, function (data) {
        $scope.facilities = data.facilityList;
      }, {}
  );

  UserSupervisedProgramList.get({}, function (data) {
        $scope.programs = data.programList;
      }, {}
  );

  var optionMessage = function (entity, defaultMessage) {
    return entity == null || entity.length == 0 ? "--none assigned--" : defaultMessage;
  };

  $scope.facilityOptionMsg = optionMessage($scope.facilities, DEFAULT_FACILITY_MESSAGE);
  $scope.programOptionMsg = optionMessage($scope.programs, DEFAULT_PROGRAM_MESSAGE);

  $scope.$parent.program = null;
  $scope.$parent.facility = null;

  $scope.loadPrograms = function () {
    if ($scope.$parent.facility) {
      UserSupportedProgramInFacilityForAnOperation.get({facilityId: $scope.$parent.facility}, function (data) {
        $scope.programs = data.programList;
        $scope.programOptionMsg = optionMessage($scope.programs, DEFAULT_PROGRAM_MESSAGE)
      }, {});
    } else {
      $scope.programs = null;
      $scope.programOptionMsg = DEFAULT_PROGRAM_MESSAGE;
    }
  };

  $scope.loadFacilities = function () {
    if ($scope.$parent.program) {
      UserSupervisedFacilitiesForProgram.get({programId: $scope.$parent.program.id}, function (data) {
        $scope.facilities = data.facilities;
        $scope.facilityOptionMsg = optionMessage($scope.facilities, DEFAULT_FACILITY_MESSAGE);
      }, {});
    } else {
      $scope.facilities = null;
      $scope.facilityOptionMsg = DEFAULT_FACILITY_MESSAGE;
    }
  };

  $scope.initRnr = function () {
    if ($scope.$parent.program) {
      $scope.error = "";
      $scope.$parent.sourceUrl = $location.$$url;

      Requisition.get({facilityId: $scope.facility, programId: $scope.program.id}, {},
          function (data) {
            if (data.rnr) {
              if (data.rnr.status != 'SUBMITTED' && !$rootScope.hasPermission('CREATE_REQUISITION')) {
                $scope.error = "An R&R has not been submitted yet";
                return;
              }
              $scope.$parent.rnr = data.rnr;
              $location.path('/create-rnr/' + $scope.facility + '/' + $scope.program.id);
            }
            else {
              Requisition.save({facilityId: $scope.facility, programId: $scope.program.id}, {}, function (data) {
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
}
