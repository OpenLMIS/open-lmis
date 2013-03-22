/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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