/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function FacilitySearchController($scope, AllFacilities, $location) {

  $scope.previousQuery = '';

  $scope.editFacility = function (id) {
    $location.path('edit/' + id);
  };


  $scope.filterFacilitiesByNameOrCode = function (query) {
    var filteredFacilities = [];
    query = query || "";

    angular.forEach($scope.facilityList, function (facility) {
      if (facility.name.toLowerCase().indexOf(query.toLowerCase()) >= 0 || facility.code.toLowerCase().indexOf(query.toLowerCase()) >= 0) {
        filteredFacilities.push(facility);
      }
    });
    $scope.resultCount = filteredFacilities.length;
    return filteredFacilities;
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchFacility").focus();
  };

  $scope.updateFilteredQueryList = function () {

    $scope.query = $scope.query.trim();
    var queryLength = $scope.query.length;
    if (queryLength >= 3) {
      if (compareQuery()) {
        AllFacilities.get({"searchParam":$scope.query.substring(0, 3)}, function (data) {
          $scope.filteredFacilities = data.facilityList;
          $scope.facilityList = $scope.filteredFacilities;
          $scope.previousQuery = $scope.query;
          if (queryLength > 3) {
            filterFacilities();
          }
          $scope.resultCount = $scope.facilityList.length;
        }, {});
      }
      else{
          filterFacilities();
          $scope.resultCount = $scope.facilityList.length;
      }
    }
  }

  var compareQuery = function() {
    if ($scope.previousQuery.substring(0, 3) != $scope.query.substring(0, 3)) {
      return true;
    }
  }

  var filterFacilities = function() {
    $scope.facilityList = [];
    angular.forEach($scope.filteredFacilities, function (facility) {
      var searchString = $scope.query.toLowerCase();
      if (facility.code.toLowerCase().indexOf(searchString) >= 0 || facility.name.toLowerCase().indexOf(searchString) >= 0) {
        $scope.facilityList.push(facility);
      }
    });
  }
}

