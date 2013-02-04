function InitiateMyFacilityRnrController($scope, UserFacilityList, UserSupportedProgramInFacilityForAnOperation, UserSupervisedProgramList, UserSupervisedFacilitiesForProgram) {

  $scope.loadFacilityData = function(selectedType) {
    $scope.$parent.periodGridData = [];
    $scope.$parent.selectedProgram = null;
    $scope.$parent.selectedFacilityId = null;
    $scope.$parent.selectedPeriod = null;
    $scope.$parent.myFacility = null;
    $scope.$parent.programs = null;
    $scope.$parent.selectedProgram = null;
    $scope.$parent.facilities = null;
    
    if (selectedType == 0) { //My facility
      UserFacilityList.get({}, function (data) {
        $scope.$parent.facilities = data.facilityList;
        $scope.$parent.myFacility = data.facilityList[0];
    
        if($scope.$parent.myFacility) {
          $scope.$parent.selectedFacilityId = $scope.$parent.myFacility.id;
          
          UserSupportedProgramInFacilityForAnOperation.get({facilityId: $scope.$parent.selectedFacilityId}, function (data) {
            $scope.$parent.programs = data.programList;
          }, {});
        } else {
          $scope.$parent.programs = null;
          $scope.$parent.selectedProgram = null;
        }
      }, {});
    } else if (selectedType == 1) { // Supervised facility
      UserSupervisedProgramList.get({}, function (data) {
        $scope.$parent.programs = data.programList;
      }, {});
    }
  };

  $scope.loadFacilitiesForProgram = function () {
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