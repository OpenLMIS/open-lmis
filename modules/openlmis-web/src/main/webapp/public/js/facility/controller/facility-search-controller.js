/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function FacilitySearchController($scope, Facility, $location, navigateBackService) {

  $scope.searchOptions = [
    {value: "facility", name: "option.value.facility"},
    {value: "geographicZone", name: "option.value.geo.zone"}
  ];

  $scope.showResults = false;
  $scope.currentPage = 1;
  $scope.selectedSearchOption = navigateBackService.selectedSearchOption || $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
  });

  $scope.edit = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0)
      $scope.loadFacilities($scope.currentPage, $scope.searchedQuery);
  });

  $scope.loadFacilities = function (page, lastQuery) {
    if (!($scope.query || lastQuery)) return;
    lastQuery ? getFacilities(page, lastQuery) : getFacilities(page, $scope.query);
  };

  function getFacilities(page, query) {
    query = query.trim();
    $scope.searchedQuery = query;
    Facility.get({"searchParam": $scope.searchedQuery, "columnName": $scope.selectedSearchOption.value, "page": page}, function (data) {
      $scope.facilityList = data.facilities;
      $scope.pagination = data.pagination;
      $scope.totalItems = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      $scope.showResults = true;
    }, {});
  }

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.loadFacilities(1);
    }
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.totalItems = 0;
    $scope.facilityList = [];
    $scope.showResults = false;
    angular.element("#searchFacility").focus();
  };
}
