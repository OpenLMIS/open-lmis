// TODO: Remove this controller
function InitiateMySupervisedFacilitiesRnrController($scope, UserSupervisedProgramList, UserSupervisedFacilitiesForProgram) {

  UserSupervisedProgramList.get({}, function (data) {
        $scope.$parent.programs = data.programList;
      }, {}
  );

  $scope.loadFacilities = function () {
    if ($scope.$parent.selectedProgram) {
      UserSupervisedFacilitiesForProgram.get({programId: $scope.$parent.selectedProgram.id}, function (data) {
        $scope.$parent.facilities = data.facilities;
      }, {});
    } else {
      $scope.$parent.facilities = null;
      $scope.$parent.selectedFacilityId = null;
    }
  };
}