// TODO: Remove this controller
function InitiateMySupervisedFacilitiesRnrController($scope, UserSupervisedProgramList, UserSupervisedFacilitiesForProgram) {

  UserSupervisedProgramList.get({}, function (data) {
        $scope.$parent.programs = data.programList;
      }, {}
  );

  $scope.loadFacilities = function () {
    $scope.$parent.facilities = null;
    if ($scope.$parent.selectedProgram) {
      UserSupervisedFacilitiesForProgram.get({programId: $scope.$parent.selectedProgram.id}, function (data) {
        $scope.$parent.facilities = data.facilities;
      }, {});
    } else {
      $scope.$parent.selectedFacilityId = null;
    }
  };
}