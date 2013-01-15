function InitiateMyFacilityRnrController($scope, UserFacilityList, UserSupportedProgramInFacilityForAnOperation) {

  UserFacilityList.get({}, function (data) {
        $scope.$parent.facilities = data.facilityList;
      }, {}
  );

  $scope.loadPrograms = function () {
    if ($scope.$parent.selectedFacilityId) {
      UserSupportedProgramInFacilityForAnOperation.get({facilityId: $scope.$parent.selectedFacilityId}, function (data) {
        $scope.$parent.programs = data.programList;
      }, {});
    } else {
      $scope.$parent.programs = null;
      $scope.$parent.selectedProgram = null;
    }
  };
}